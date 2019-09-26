package com.inu.bus.recycler

import android.content.Intent
import android.databinding.Observable
import android.databinding.ObservableBoolean
import android.graphics.Color
import android.graphics.Rect

import android.os.Message
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.TouchDelegate
import android.widget.CheckBox
import com.inu.bus.R
import com.inu.bus.activity.MainActivity
import com.inu.bus.activity.RouteActivity
import com.inu.bus.custom.HandlerArrivalText
import com.inu.bus.databinding.RecyclerArrivalItemBinding
import com.inu.bus.model.BusArrivalInfo
import com.inu.bus.model.BusRoutenode
import com.inu.bus.util.LocalIntent
import com.inu.bus.util.Singleton
import java.util.*

/**
 * Created by Minjae Son on 2018-08-25.
 */

class ViewHolderArrivalItem(private val mBinding : RecyclerArrivalItemBinding,
                            private val isShowing : ObservableBoolean,
                            private val schoolTab : Boolean) : RecyclerView.ViewHolder(mBinding.root) {

    // Ticker 필요여부
    private var needTick = false
    // 실제 타이머
    private var mTimer : Timer? = null
    // 시간마다 수행될 작업
    private var currentTask : TimerTask? = null

    private fun newTimerTask() : TimerTask {return object : TimerTask() {
        override fun run() {
//            Log.d(Singleton.LOG_TAG, "$mStrBusStop : ${mBinding.data!!.no} is tick")
            currentTask = this
            // 버스 도착시간 전송
            sendTime(mBinding.data!!)
            // 버스가 도착하면 타이머 초기화
            if(needTick){
                mTimer?.cancel()
                mTimer = Timer()
                mTimer!!.schedule(newTimerTask(), 1000)
            }
        }}
    }

    private val mHandler by lazy { HandlerArrivalText(mBinding.recyclerArrival) }



    // recycler_arrival_item.xml 바인딩
    fun bind(data : BusArrivalInfo){

        val btnFav = itemView.findViewById<CheckBox>(R.id.btn_favorite)
        btnFav.touchDelegate = TouchDelegate(Rect(0,0,20,20),btnFav)

        mBinding.btnFavorite.isChecked = data.favorite

//        val parent = mBinding.btnFavorite.parent as View
//        parent.post {
//            val rect = Rect()
//            parent.getHitRect(rect)
//            rect.top -= 100
//            rect.bottom += 100
//            rect.left -= 100
//            rect.right += 100
//
//            parent.touchDelegate = TouchDelegate(rect,mBinding.btnFavorite)
//        }

        if(schoolTab){
            mBinding.recyclerBusno.textSize = 14.0f
        }

        mBinding.data = data
        mBinding.listener = this
        sendTime(mBinding.data!!)
        // 아이템이 화면에 보일때 Ticker가 작동
        isShowing.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback(){
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                if(sender is ObservableBoolean){
                    if(sender.get()){
                        sendTime(mBinding.data!!)
                        startTick()
                    }
                    else {
                        stopTick()
                    }
                }
            }
        })
    }

    // 버스 도착 정보 전송
    fun sendTime(data: BusArrivalInfo){
        // 남은 시간 = 도착 시간 - 현재 시간
        var remain = (data.arrival - System.currentTimeMillis())/1000
        var str = ""
        // 배차간격을 이용한 남은 시간 설정
        while(remain < 0){
            remain += data.interval*60
        }
        while(remain > data.interval*60){
            remain -= data.interval*60
        }
        if(remain > 3600){
            str += "${remain/3600}시간 "
        }
        if(remain % 3600 >= 60L){
            str += "${(remain%3600)/60}분 "
        }

        if(str == ""){
            // 1분 미만은 잠시후
            str = "잠시후"
        }

        else if(remain % 60 != 0L)
        {
            str += "${remain%60}초"
        }

        // arrivalitem textview
        val msg = Message()
        if(schoolTab){

            var tempArray = ArrayList<BusRoutenode>()
            Singleton.SchoolBusRoute.forEach { (id, list) ->
                if(id == data.no.substring(0,2)) {
                    tempArray = list
                }
            }
////            Singleton.SchoolBusRoute.forEach() { (routeID, Routenode) ->
////                if(data.no == routeID){
////                    fee = cost
////                }
////            }
////            fee = fee?: Singleton.busCost["else"]
////            mBinding.fee = "${fee}원"
            var location = "대기 중"
            tempArray.forEach{
                if(it.nodeNo == data.start)
                    location = it.nodeName
            }
            msg.obj = location
        }
        else { msg.obj = str }
        mHandler.sendMessage(msg)
    }
    // Ticker 작동
    fun startTick(){
        needTick = true
        mTimer?.cancel()
        currentTask?.cancel()
        mTimer = Timer()
        mTimer!!.schedule(newTimerTask(), 1000)
    }
    // Ticker 종료
    fun stopTick(){
        needTick = false
        mTimer?.cancel()
        currentTask?.cancel()
    }

    // 즐겨찾기가 체크되면 AppDatabase 로 item 전달
    fun onCheck(data: BusArrivalInfo){

        val btnFavorite = itemView.findViewById<CheckBox>(R.id.btn_favorite)
        val context = mBinding.root.context

        if(btnFavorite.isChecked){
            data.favorite = true
            (context as MainActivity).favList.add(data.no)
            context.insertDB(data.no)
        }
        else if(!btnFavorite.isChecked){
            data.favorite = false
            (context as MainActivity).favList.remove(data.no)
            context.deleteDB(data.no)
        }

        val mBroadcastManager = LocalBroadcastManager.getInstance(context)
        mBroadcastManager.sendBroadcast(Intent(LocalIntent.FAVORITE_CLICK.value))

//        mAdapter.applyDataSet(tempList,(context as MainActivity).favList)
//        mAdapter.notifyDataSetChanged()
    }



    // 바인딩된 아이템 클릭시 intent를 가지고 RouteActivity로 이동
    fun onClick(data : BusArrivalInfo){

        val context = mBinding.root.context
        val intent = Intent(context, RouteActivity::class.java)
        val str : String = if(schoolTab) "R" + data.no
        else data.no

        intent.putExtra("routeNo", str)
        context.startActivity(intent)
        Log.d("route", "$data")
    }
}