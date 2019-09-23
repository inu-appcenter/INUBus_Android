package com.inu.bus.activity

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.TextAppearanceSpan
import android.util.Log
import android.view.Gravity
import android.view.WindowManager
import android.widget.AutoCompleteTextView
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import com.inu.bus.MyService
import com.inu.bus.R
import com.inu.bus.custom.FirstPopUp
import com.inu.bus.custom.IconPopUp
import com.inu.bus.fragment.ArrivalFragment
import com.inu.bus.fragment.SearchHistoryFragment
import com.inu.bus.model.DBBusFavoriteItem
import com.inu.bus.recycler.ViewPagerAdapter
import com.inu.bus.util.AppDatabase
import com.inu.bus.util.LocalIntent
import com.inu.bus.util.Singleton
import com.ms_square.etsyblur.BlurSupport
import com.ms_square.etsyblur.BlurringView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.activity_main_viewpager
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.custom_actionbar.*
import kotlinx.android.synthetic.main.custom_info_drawer.*
import kotlinx.android.synthetic.main.custom_info_drawer.view.*
import kotlinx.android.synthetic.main.custom_searchbar.*
import java.lang.ref.WeakReference


/**
 * Created by Minjae Son on 2018-08-07.
 */

class MainActivity : AppCompatActivity(){

    companion object {
        // ArrivalFragmentTab에서 각 검색결과를 참조하기 위해
        // TODO Observable로 변경
        lateinit var mWrSearchView : WeakReference<AutoCompleteTextView>
        // Drawer BlurView를 공유하면 dim alpha가 적용이 안되서 팝업용 BlurView를 별도로 설정
        lateinit var mWrBlurringView2 : WeakReference<BlurringView>
        // ArrivalViewPager에서 지정단탭일시 액션바를 바꾸기 위해
        lateinit var mWrMainUpperView : WeakReference<LinearLayout>
        // Drawer 여는 이벤트용
        lateinit var mWrBtnInfo : WeakReference<ImageButton>
    }

    var mDB : AppDatabase? = null
    var DBfavorite = listOf<DBBusFavoriteItem>()
    var favList = arrayListOf<String?>()
    var firstDBload = false

    // 지연 초기화
    private val mViewPagerAdapter by lazy { ViewPagerAdapter(supportFragmentManager, this) }
    private val mBroadcastManager by lazy { LocalBroadcastManager.getInstance(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.inu.bus.R.layout.activity_main)

        // room DB 생성
        mDB = AppDatabase.getInstance(this)

        // 메모리 누수를 방지하기 위해 WeakReference 사용
        mWrMainUpperView = WeakReference(ll_main_upper_view_wrapper)
        mWrSearchView = WeakReference(actionbar_searchView)
        mWrBlurringView2 = WeakReference(activity_main_popup_blur)
        mWrSearchView.get()?.addTextChangedListener(mSearchTextWatcher)
        changestatusBarColor()
        setActionBar()
        setDrawer()
        setMainViewPager()
        SpanText()
    }

    private val mSearchTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if(s.isNullOrEmpty())
                activity_main_viewpager.currentItem = 1
            else
                activity_main_viewpager.currentItem = 0
        }
    }

    private fun setActionBar(){
        // 젤리빈 호환
//        supportActionBar?.hide()
//        actionbar_searchView.setAdapter(mSearchAdapter)
//        actionbar_searchView.setOnEditorActionListener( TextView.OnEditorActionListener { v, actionId, _ ->
//            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                mDB?.searchhistoryDAO()?.insert(DBSearchHistoryItem(name = v.text.toString()))
//                mSearchAdapter.refreshHistory()
//                return@OnEditorActionListener true
//            }
//            false
//        })
    }



    fun setDB() {
        val r = Runnable{
            try {
                DBfavorite = mDB?.busfavoriteDAO()?.getAll()!!
            } catch (e:Exception){
                Log.d("kBm0598","Error - $e")
            }
        }
        val addThread = Thread(r)
        addThread.start()
        DBfavorite.forEach {
            if(!favList.contains(it.no))
                favList.add(it.no)
        }
        if(!favList.isEmpty()){
            firstDBload = true
        }
    }

    fun insertDB(no : String) {
        val r = Runnable{
            try {
                mDB?.busfavoriteDAO()?.insert(DBBusFavoriteItem(no))
            } catch (e:Exception){
                Log.d("kBm0598","Error - $e")
            }
        }
        val addThread = Thread(r)
        addThread.start()
    }

    fun deleteDB(no : String) {
        val r = Runnable{
            try {
                mDB?.busfavoriteDAO()?.delete(DBBusFavoriteItem(no))
            } catch (e:Exception){
                Log.d("kBm0598","Error - $e")
            }
        }
        val addThread = Thread(r)
        addThread.start()
    }

    fun startpopup(btnclick : Boolean){
        val pref = getSharedPreferences("isFirst", Context.MODE_PRIVATE)
        val first = pref.getBoolean("isFirst",false)
        if(!first || btnclick){
            val editor = pref.edit()
            editor.putBoolean("isFirst",true)
            editor.commit()

            val popupView = FirstPopUp(this)
                    .setDimBlur(activity_main_popup_blur)
                    .setShowDuration(60000)
                    .setOnConfirmButtonClickListener{
                        it.dismiss()
                    }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                popupView.setWindow(window)
            }
            popupView.show()
        } else {
          Log.d("test","not first")
        }

    }

    // TextView 특정 문자열 폰트 변경
    fun SpanText(){
//        val v = LayoutInflater.from(applicationContext)).inflate()
        val mMessage = findViewById<TextView>(R.id.tv_drawer_note)
        val str = "공공데이터"
        val start = mMessage.text.indexOf(str)
        val end = start + str.length
        val ssb = SpannableString(mMessage.text)
//        ssb.setSpan(ForegroundColorSpan(Color.parseColor("#00FF00")),start,end,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        ssb.setSpan(TextAppearanceSpan(applicationContext,R.style.pulbic_data),start,end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        mMessage.text = ssb
    }


    // 버튼 리스너 설정
    private fun setDrawer(){
        activity_main_popup_blur.blurredView(drawer_layout)
        mWrBtnInfo = WeakReference(btn_actionbar_info)

        btn_actionbar_popup.setOnClickListener{
            startpopup(true)
        }

        ll_start_search.setOnClickListener {
            callSearchBar()
        }

        tv_start_search.setOnClickListener {
            callSearchBar()
        }
//        btn_actionbar_search.setOnClickListener {
//
//            (activity_main_viewpager.adapter as ViewPagerAdapter).fragments.forEach {
//                if(it is SearchHistoryFragment){
//                    var newSHitem = DBSearchHistoryItem()
//                    newSHitem.name = actionbar_searchView.text.toString()
//                    it.mAdapter.insertHistory(this,newSHitem)
//                }
//            }
//            activity_main_viewpager.currentItem = 1
//
//            Log.d("1234","searchhistory insert")
//            Singleton.hideKeyboard(this)
//        }

        btn_actionbar_info.setOnClickListener {
            Singleton.hideKeyboard(this)
            drawer_layout.openDrawer(Gravity.END)
        }

        drawer_btn_ask.setOnClickListener {
            startActivity(Intent(this, InquireActivity::class.java))
//            drawer_layout.closeDrawer(Gravity.END)
        }

        activity_main_drawer.btn_back.setOnClickListener { drawer_layout.closeDrawer(Gravity.END) }

        val pInfo = this.packageManager.getPackageInfo(packageName, 0)
        tv_drawer_version.text = pInfo.versionName
        BlurSupport.addTo(drawer_layout)
    }

    fun callSearchBar(){
        startActivity(Intent(this, SearchActivity::class.java))
    }

    // ViewPagerAdapter 설정
    private fun setMainViewPager(){
        val arrivalFragment = ArrivalFragment.newInstance(supportFragmentManager, this)

        // 도착, 목적지 Fragment 추가
        mViewPagerAdapter.addFragment(SearchHistoryFragment.newInstance(supportFragmentManager,this))
        mViewPagerAdapter.addFragment(arrivalFragment)

        // ViewPager 설정
        activity_main_viewpager.adapter = mViewPagerAdapter
        activity_main_viewpager.offscreenPageLimit = 2
        activity_main_viewpager.currentItem = 1
        activity_main_viewpager.setScrollDurationFactor(4.0)

        // SegmentedButton 토글에 따라 아이템 선택
//        activity_main_toggle.setOnPositionChangedListener {
//            activity_main_viewpager.currentItem =
//                    when(it){
//                        // 도착정보
//                        0-> 1
//                        // 목적지 정보
//                        else -> 0
//                    }
//        }
    }


    fun changestatusBarColor(){
        // 롤리팝 버전 이상부터 statusBar를 하얀색, 아이콘을 검은색으로 표시
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(applicationContext,R.color.colorActionBar)
//            window.decorView.systemUiVisibility = 1
        }
    }
    // 재시작되면 서비스 시작
    override fun onResume() {
        super.onResume()
        startService(Intent(applicationContext, MyService::class.java))
    }

    // Pause상태에 서비스 종료
    override fun onPause() {
        super.onPause()
        mBroadcastManager.sendBroadcast(Intent(LocalIntent.SERVICE_EXIT.value))
    }

    // 백버튼 기능
    override fun onBackPressed() {
        when {
            // 검색창이 비어있지 않으면 비움
            actionbar_searchView.text.toString() != "" -> actionbar_searchView.text.clear()

            // information 열려있으면 닫음
            drawer_layout.isDrawerOpen(Gravity.END) -> drawer_layout.closeDrawer(Gravity.END)

            // 문의하기 열려 있으면 닫음
            IconPopUp.mInstance?.get() != null -> IconPopUp.mInstance?.get()?.dismiss()
            else -> {
                super.onBackPressed()
            }
        }
    }
}