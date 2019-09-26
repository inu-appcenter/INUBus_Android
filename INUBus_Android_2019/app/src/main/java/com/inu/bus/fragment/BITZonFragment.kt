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
import android.widget.ImageView
import com.inu.bus.R
import com.inu.bus.activity.MainActivity
import com.inu.bus.model.*
import com.inu.bus.recycler.RecyclerAdapterSchoolBus
import com.inu.bus.util.LocalIntent
import com.inu.bus.util.Singleton
import kotlinx.android.synthetic.main.fragment_arrival_tab_bitzon.*
import kotlinx.android.synthetic.main.fragment_swipepull_recycler.*

/**
 * Created by Minjae Son on 2018-08-13.
 */

class BITZonFragment : Fragment(){

    private var isShowing = false
    private lateinit var mContext : Context
    private val mBroadcastManager by lazy { LocalBroadcastManager.getInstance(mContext) }
    val mAdapter by lazy { RecyclerAdapterSchoolBus("school") }

    companion object {
        fun newInstance(context: Context): BITZonFragment {
            val fragment = BITZonFragment()
            fragment.mContext = context
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_swipepull_recycler, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        rv_fragment_node_arrival_recycler.adapter = mAdapter
        // 프래그먼트 준비 알림 Broadcast 시작
        mBroadcastManager.sendBroadcast(Intent(LocalIntent.NOTIFY_FRAGMENT_READY.value))
        // 프래그먼트를 당기면 데이터 리프레시 Broadcast 시작
        fragment_node_arrival_swipeRefreshLayout.setOnRefreshListener {
            mBroadcastManager.sendBroadcast(Intent(LocalIntent.ARRIVAL_DATA_REFRESH_REQUEST.value))
        }
//        Singleton.SBgps.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback(){
//            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
//                dataRefresh()
//            }
//        })
        refreshLoading()
        dataRefresh()
    }
    // 등교용 정류장 정보를 다시 받아 어댑터에 적용
    fun dataRefresh(){
        fragment_arrival_bitzon_swiperefresh?.isRefreshing = false
        Singleton.SBgps.get()?.let{gpsData ->
            val notEmptyDataSet = ArrayList<RecyclerArrivalItem>()
            if(gpsData.isNotEmpty()){
                gpsData.forEach {
                    val witch = it.location
                    Log.d("gps","routeid -> ${it.busTime.routeID}")
                    notEmptyDataSet.add(RecyclerArrivalItem(BusArrivalInfo(
                            "${it.busTime.routeID}",-1,witch,-1,9999,"${it.busTime.startTime}",BusInformation.BusType.TONG)
                    ))
                }
            }
            mAdapter.applyDataSet(notEmptyDataSet,(activity as MainActivity).favList)
        }
    }

    fun refreshLoading() {
//        Log.d("0598", "refreshLoading")
        val mSwipeRefreshLayout = fragment_node_arrival_swipeRefreshLayout
        mSwipeRefreshLayout.setProgressViewOffset(true, 0, 130)
        mSwipeRefreshLayout.setColorSchemeColors(Color.parseColor("#0061f4"))

        val f = mSwipeRefreshLayout.javaClass.getDeclaredField("mCircleDiameter")
        f.isAccessible = true
        f.setInt(mSwipeRefreshLayout, 130)
        val f2 = mSwipeRefreshLayout.javaClass.getDeclaredField("mProgress")
        f2.isAccessible = true
        var prog = f2.get(mSwipeRefreshLayout) as CircularProgressDrawable
        prog.centerRadius = 30f
        prog.strokeWidth = 9f
        val f3 = mSwipeRefreshLayout.javaClass.getDeclaredField("mCircleView")
        f3.isAccessible = true
        var img = f3.get(mSwipeRefreshLayout) as ImageView
        img.setBackgroundResource(R.drawable.refresh_loading)
    }
}