package com.inu.bus.fragment

import android.content.Context
import android.content.Intent
import android.databinding.Observable
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.widget.CircularProgressDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.RotateAnimation
import android.widget.ImageView
import com.inu.bus.R
import com.inu.bus.activity.MainActivity
import com.inu.bus.recycler.RecyclerAdapterArrival
import com.inu.bus.util.LocalIntent
import com.inu.bus.util.Singleton
import kotlinx.android.synthetic.main.fragment_swipepull_recycler.*


/**
 * Created by Minjae Son on 2018-08-07.
 * Updated by ByoungMean on 2019-07-27.
 */

class ArrivalFragmentTab : Fragment(){

    private var isShowing = false
    private lateinit var mStrBusStop: String
    private lateinit var mContext : Context
    private lateinit var mFabRefreshAnimation : RotateAnimation
    private val mBroadcastManager by lazy { LocalBroadcastManager.getInstance(mContext) }
    val mAdapter by lazy { RecyclerAdapterArrival(mStrBusStop) }

    private var favList = arrayListOf<String?>()
    private var firstDBload = false

    companion object {
        fun newInstance(context: Context, stopName: String): ArrivalFragmentTab {
            val fragment = ArrivalFragmentTab()
            fragment.mStrBusStop = stopName
            fragment.mContext = context
            Log.d("test", "$stopName fragment created")
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_swipepull_recycler, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("test", "$mStrBusStop fragment onViewCreated")
        rv_fragment_node_arrival_recycler.adapter = mAdapter
        // 프래그먼트 준비 알림 Broadcast 시작
        mBroadcastManager.sendBroadcast(Intent(LocalIntent.NOTIFY_FRAGMENT_READY.value))
//        mBroadcastManager.sendBroadcast(Intent(LocalIntent.FAVORITE_CLICK.value))
        // 프래그먼트를 당기면 데이터 리프레시 Broadcast 시작

        fragment_node_arrival_swipeRefreshLayout.setOnRefreshListener {
            mBroadcastManager.sendBroadcast(Intent(LocalIntent.ARRIVAL_DATA_REFRESH_REQUEST.value))
        }
        Singleton.arrivalFromInfo.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback(){
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                dataRefresh()
            }
        })
        refreshLoading()
        dataRefresh()
    }
    // 하교용 정류장 정보를 다시 받아 어댑터에 적용
    fun dataRefresh(){
        Log.d("0598", "dataRefresh")
        fragment_node_arrival_swipeRefreshLayout?.isRefreshing = false
        Singleton.arrivalFromInfo.get()?.let{
            val checked = it.filter{ subIt->  subIt.name == mStrBusStop }
            if(checked.isEmpty()) return
            val filtered = checked[0].data


            Log.d("0598","firstdb ${firstDBload}")
            if(!(activity as MainActivity).firstDBload){
                (activity as MainActivity).setDB()
            }

            filtered.forEach {
                (activity as MainActivity).favList.forEachIndexed { index, favorite ->
                    if (it.no == favorite)
                        it.favorite = true
                }
            }

            mAdapter.applyDataSet(filtered,(activity as MainActivity).favList)
            mAdapter.notifyDataSetChanged()
        }
    }

    // 당겨서 새로고침 이미지 설정
    fun refreshLoading(){
//        Log.d("0598", "refreshLoading")
        val mSwipeRefreshLayout = fragment_node_arrival_swipeRefreshLayout
        mSwipeRefreshLayout.setProgressViewOffset(true,0,130)
        mSwipeRefreshLayout.setColorSchemeColors(Color.parseColor("#0061f4"))

        val f = mSwipeRefreshLayout.javaClass.getDeclaredField("mCircleDiameter")
        f.isAccessible = true
        f.setInt(mSwipeRefreshLayout,130)
        val f2 = mSwipeRefreshLayout.javaClass.getDeclaredField("mProgress")
        f2.isAccessible = true
        var prog = f2.get(mSwipeRefreshLayout) as CircularProgressDrawable
        prog.centerRadius = 30f
        prog.strokeWidth = 9f
        val f3 = mSwipeRefreshLayout.javaClass.getDeclaredField("mCircleView")
        f3.isAccessible = true
        var img = f3.get(mSwipeRefreshLayout) as ImageView
        img.setBackgroundResource(R.drawable.refresh_loading)

//        val dfields = mSwipeRefreshLayout.javaClass.declaredFields
//
//        var temp:String = ""
//        var temp2:String = ""
//        var temps:String = ""
//        for(i in 0 until dfields.size){
//            //temp = temp + dfields[i].toString() + "\n"
//            temp = dfields[i].toString()
//            temp2 = temp.substring(temp.indexOf("widget.SwipeRefreshLayout")+26)
//            if(temp.contains("final"))
//                temps = temps + temp2 + "\n"
//            else
//                temps = temps + temp + "\n"
//        }
    }

    fun filter(str : String){
        mAdapter.filter(str,(activity as MainActivity).favList.size)
    }

    // TimeTicker 생명주기에 맞춰 활성, 비활성.

    // ViewPager에서 현재 페이지가 보이고 있는지 오는 콜백.
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
//        Log.d(LOG_TAG, "isVisibleToUser $mStrBusStop, $isVisibleToUser")
        mAdapter.isShowing.set(isVisibleToUser)
        isShowing = isVisibleToUser
    }

    // 생명주기에 맞춤
    override fun onPause() {
        super.onPause()
        mAdapter.isShowing.set(false)
    }

    // 재시작되면서 현재 보이는 탭만 Ticker를 실행하도록.
    override fun onResume() {
        super.onResume()
        mAdapter.isShowing.set(isShowing)
    }

}
