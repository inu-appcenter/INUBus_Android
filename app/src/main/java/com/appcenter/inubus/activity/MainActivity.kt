package com.appcenter.inubus.activity

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.appcompat.app.AppCompatActivity
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.TextAppearanceSpan
import android.util.Log
import android.view.Gravity
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.appcenter.inubus.MyService
import com.appcenter.inubus.R
import com.appcenter.inubus.custom.FirstPopUp
import com.appcenter.inubus.custom.IconPopUp
import com.appcenter.inubus.fragment.ArrivalFragment
import com.appcenter.inubus.fragment.SearchHistoryFragment
import com.appcenter.inubus.model.DBBusFavoriteItem
import com.appcenter.inubus.recycler.ViewPagerAdapter
import com.appcenter.inubus.util.AppDatabase
import com.appcenter.inubus.util.LocalIntent
import com.appcenter.inubus.util.Singleton
import com.ms_square.etsyblur.BlurSupport
import com.ms_square.etsyblur.BlurringView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.custom_actionbar.*
import kotlinx.android.synthetic.main.custom_info_drawer.*
import kotlinx.android.synthetic.main.custom_info_drawer.view.*
import java.lang.ref.WeakReference


/**
 * Created by Minjae Son on 2018-08-07.
 * Updated by ByoungMean on 2019-09-27.
 */

class MainActivity : AppCompatActivity(){

    companion object {
        // ArrivalFragmentTab에서 각 검색결과를 참조하기 위해
        // TODO Observable로 변경
//        lateinit var mWrSearchView : WeakReference<AutoCompleteTextView>
        // Drawer BlurView를 공유하면 dim alpha가 적용이 안되서 팝업용 BlurView를 별도로 설정
        lateinit var mWrBlurringView2 : WeakReference<BlurringView>
        // ArrivalViewPager에서 지정단탭일시 액션바를 바꾸기 위해
        lateinit var mWrMainUpperView : WeakReference<LinearLayout>
        // Drawer 여는 이벤트용
        lateinit var mWrBtnInfo : WeakReference<LinearLayout>
    }

    var mDB : AppDatabase? = null
    var DBfavorite = listOf<DBBusFavoriteItem>()
    var favList = arrayListOf<String?>()
    var firstDBload = false

    // 지연 초기화
    private val mViewPagerAdapter by lazy { ViewPagerAdapter(supportFragmentManager, this) }
    private val mBroadcastManager by lazy { androidx.localbroadcastmanager.content.LocalBroadcastManager.getInstance(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // room DB 생성
        mDB = AppDatabase.getInstance(this)

        // 메모리 누수를 방지하기 위해 WeakReference 사용
        mWrMainUpperView = WeakReference(ll_main_upper_view_wrapper)
        mWrBlurringView2 = WeakReference(activity_main_popup_blur)
        changestatusBarColor()
        setDrawer()
        setMainViewPager()
        SpanText()
    }

    fun setDB() {
        val r = Runnable{
            try {
                DBfavorite = mDB?.busfavoriteDAO()?.getAll()!!
            } catch (e:Exception){
                Log.d("error","Error - $e")
            }
        }
        val addThread = Thread(r)
        addThread.start()
        DBfavorite.forEach {
            if(!favList.contains(it.no))
                favList.add(it.no)
        }
        if(favList.isNotEmpty()){
            firstDBload = true
        }
    }

    fun insertDB(no : String) {
        val r = Runnable{
            try {
                mDB?.busfavoriteDAO()?.insert(DBBusFavoriteItem(no))
            } catch (e:Exception){
                Log.d("error","Error - $e")
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
                Log.d("error","Error - $e")
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
    private fun SpanText(){
        val mMessage = findViewById<TextView>(R.id.tv_drawer_note)
        val str = "공공데이터"
        val start = mMessage.text.indexOf(str)
        val end = start + str.length
        val ssb = SpannableString(mMessage.text)
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

        btn_actionbar_info.setOnClickListener {
            Singleton.hideKeyboard(this)
            drawer_layout.openDrawer(Gravity.END)
        }

        drawer_btn_ask.setOnClickListener {
            startActivity(Intent(this, InquireActivity::class.java))
        }

        activity_main_drawer.btn_back.setOnClickListener { drawer_layout.closeDrawer(Gravity.END) }

        val pInfo = this.packageManager.getPackageInfo(packageName, 0)
        tv_drawer_version.text = pInfo.versionName
        BlurSupport.addTo(drawer_layout)
    }

    private fun callSearchBar(){
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

    }


    private fun changestatusBarColor(){
        // 롤리팝 버전 이상부터 statusBar를 하얀색, 아이콘을 검은색으로 표시
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(applicationContext,R.color.colorActionBar)
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