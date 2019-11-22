package com.appcenter.inubus.fragment

import android.content.Context
import android.content.Intent
import android.databinding.Observable
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.widget.CircularProgressDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.appcenter.inubus.R
import com.appcenter.inubus.activity.MainActivity
import com.appcenter.inubus.model.BusArrivalInfo
import com.appcenter.inubus.model.BusInformation
import com.appcenter.inubus.model.RecyclerArrivalItem
import com.appcenter.inubus.recycler.RecyclerAdapterSchoolBus
import com.appcenter.inubus.util.LocalIntent
import com.appcenter.inubus.util.Singleton
import kotlinx.android.synthetic.main.fragment_swipepull_recycler.*

/**
 * Created by ByoungMean on 2019-10-11.
 */

class SchoolBusFragment : Fragment(){

    private lateinit var mContext : Context
    private val mBroadcastManager by lazy { LocalBroadcastManager.getInstance(mContext) }
    private val mAdapter by lazy { RecyclerAdapterSchoolBus() }

    companion object {
        fun newInstance(context: Context): SchoolBusFragment {
            val fragment = SchoolBusFragment()
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
        Singleton.schoolbusGPS.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback(){
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                dataRefresh()
            }
        })
        refreshLoading()
        dataRefresh()
    }
    // 등교용 정류장 정보를 다시 받아 어댑터에 적용
    private fun dataRefresh(){
        fragment_node_arrival_swipeRefreshLayout?.isRefreshing = false
        Singleton.schoolbusGPS.get()?.let{ gpsData ->
            val notEmptyDataSet = ArrayList<RecyclerArrivalItem>()
            if(gpsData.isNotEmpty()){
                gpsData.forEach {

                    val temp = BusArrivalInfo("${it.busTime.routeID}",-1,it.location,-1,9999,
                            "${it.busTime.startTime}",BusInformation.BusType.TONG)

                    (activity as MainActivity).favList.forEachIndexed { index, favorite ->
                        if (it.busTime.routeID == favorite)
                            temp.favorite = true
                    }
                    notEmptyDataSet.add(RecyclerArrivalItem(temp))
                }
            }
            mAdapter.applyDataSet(notEmptyDataSet,(activity as MainActivity).favList)
            mAdapter.notifyDataSetChanged()
        }
    }

    private fun refreshLoading() {
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