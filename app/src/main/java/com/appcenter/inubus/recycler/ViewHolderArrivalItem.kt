package com.appcenter.inubus.recycler

import android.content.Intent
import android.os.Message
import android.widget.CheckBox
import androidx.databinding.Observable
import androidx.databinding.ObservableBoolean
import com.appcenter.inubus.R
import com.appcenter.inubus.activity.MainActivity
import com.appcenter.inubus.activity.RouteActivity
import com.appcenter.inubus.custom.HandlerArrivalText
import com.appcenter.inubus.databinding.RecyclerArrivalItemBinding
import com.appcenter.inubus.model.BusArrivalInfo
import com.appcenter.inubus.model.BusRoutenode
import com.appcenter.inubus.util.LocalIntent
import com.appcenter.inubus.util.Singleton
import java.util.*

/**
 * Created by Minjae Son on 2018-08-25.
 */

class ViewHolderArrivalItem(private val mBinding : RecyclerArrivalItemBinding,
                            private val isShowing : ObservableBoolean,
                            private val schoolTab : Boolean) : androidx.recyclerview.widget.RecyclerView.ViewHolder(mBinding.root) {

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

        mBinding.btnFavorite.isChecked = data.favorite

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

        val msg = Message()
        if(schoolTab){

            var tempArray = ArrayList<BusRoutenode>()
            Singleton.SchoolBusRoute.forEach { (id, list) ->
                if(id == data.no.substring(0,2)) {
                    tempArray = list
                }
            }
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

        val mBroadcastManager = androidx.localbroadcastmanager.content.LocalBroadcastManager.getInstance(context)
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
    }
}