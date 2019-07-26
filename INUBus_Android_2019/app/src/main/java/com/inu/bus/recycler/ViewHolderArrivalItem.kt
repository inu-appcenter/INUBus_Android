package com.inu.bus.recycler

import android.content.Intent
import android.databinding.Observable
import android.databinding.ObservableBoolean
import android.os.Message
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.Adapter
import android.widget.CheckBox
import com.inu.bus.R
import com.inu.bus.activity.MainActivity
import com.inu.bus.activity.RouteActivity
import com.inu.bus.custom.HandlerArrivalText
import com.inu.bus.databinding.RecyclerArrivalItemBinding
import com.inu.bus.fragment.ArrivalFragmentTab
import com.inu.bus.model.BusArrivalInfo
import com.inu.bus.model.DBBusFavoriteItem
import com.inu.bus.util.AppDatabase
import kotlinx.android.synthetic.main.recycler_arrival_item.view.*
import java.util.*

/**
 * Created by Minjae Son on 2018-08-25.
 */

class ViewHolderArrivalItem(private val mBinding : RecyclerArrivalItemBinding, private val isShowing : ObservableBoolean) : RecyclerView.ViewHolder(mBinding.root) {

    // Ticker 필요여부
    private var needTick = false
    // 실제 타이머
    private var mTimer : Timer? = null
    // 시간마다 수행될 작업
    private var currentTask : TimerTask? = null

    private var mDB:AppDatabase? = null

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
        msg.obj = str
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
    fun onChecked(data: BusArrivalInfo){
        Log.d("kBm0598","OnChecked called!!!")
        val context = mBinding.root.context
        mDB = AppDatabase.getInstance(context)
        var temp = listOf<DBBusFavoriteItem>()
        val mcheckBox = itemView.findViewById<CheckBox>(R.id.checkBox)
        val addRunnable : Runnable

        if(mcheckBox.isChecked) {
            addRunnable = Runnable {
                // 삽입
                val newData = DBBusFavoriteItem()
                newData.no = data.no
                mDB?.busfavoriteDAO()?.insert(newData)
                // 조회
                temp = mDB?.busfavoriteDAO()?.getAll()!!
                Log.d("kBm0598","insert success!!")
            }
            val addThread = Thread(addRunnable)
            addThread.start()
//            (context as MainActivity).insertDB(data.no)
        }
        else if(!mcheckBox.isChecked) {
//            addRunnable = Runnable {
//                temp = mDB?.busfavoriteDAO()?.getAll()!!
//                var p = -1
//                // 삭제
//                for(i in 0 until temp.size){
//                    if(temp[i].no == data.no) {
//                        p = i
//                    }
//                }
//                mDB?.busfavoriteDAO()?.delete(temp[p])
//                // 조회
//                Log.d("kBm0598","delete success!!")

//            }
//            val addThread = Thread(addRunnable)
//            addThread.start()
            (context as MainActivity).deleteDB(data.no)
        }

        (context as MainActivity).setDB()
        val temp2 = context.temp
        for(i in 0 until temp.size)
            Log.d("0598","bus number : ${temp[i].no}")

    }

    // 바인딩된 아이템 클릭시 intent를 가지고 RouteActivity로 이동
    fun onClick(data : BusArrivalInfo){
        Log.d("test", "OnClick called")
        val context = mBinding.root.context
        val intent = Intent(context, RouteActivity::class.java)
        intent.putExtra("routeNo", data.no)
        context.startActivity(intent)
    }
}