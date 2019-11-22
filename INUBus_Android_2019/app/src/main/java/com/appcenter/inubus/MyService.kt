package com.appcenter.inubus

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import android.util.Log
import com.appcenter.inubus.model.*
import com.appcenter.inubus.util.LocalIntent
import com.appcenter.inubus.util.Singleton
import com.appcenter.inubus.util.Singleton.LOG_TAG
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

/**
 * Created by Minjae Son on 2018-08-07.
 * Updated by ByoungMean on 2019-10-11.
 */

class MyService : Service(){

    private val mBroadcastManager by lazy { androidx.localbroadcastmanager.content.LocalBroadcastManager.getInstance(applicationContext) }
    private var mTimer : Timer? = null
    private var currentTimerTask : TimerTask? = null

    override fun onBind(intent: Intent): IBinder? = null

    override fun onCreate() {
        // 프로세스 안의 데이터 통신을 위한 LocalIntent를 필터링
        mBroadcastManager.registerReceiver(mBroadcastReceiver, IntentFilter(LocalIntent.FIRST_DATA_REQUEST.value))
        mBroadcastManager.registerReceiver(mBroadcastReceiver, IntentFilter(LocalIntent.ARRIVAL_DATA_REFRESH_REQUEST.value))
        mBroadcastManager.registerReceiver(mBroadcastReceiver, IntentFilter(LocalIntent.FAVORITE_CLICK.value))
        mBroadcastManager.registerReceiver(mBroadcastReceiver, IntentFilter(LocalIntent.SERVICE_EXIT.value))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("test", "Service Started")
        requestNodeRoutes()
        requestArrivalInfo()
        startAutoRefresh()
        return START_STICKY_COMPATIBILITY
    }

    private fun startAutoRefresh(){
        currentTimerTask = newTimerTask()
        mTimer = Timer()
        mTimer!!.schedule(currentTimerTask,5000)
    }
    // 도착정보 요청 타이머 설정
    private fun newTimerTask() : TimerTask {return object : TimerTask() {
        override fun run() {
            requestArrivalInfo()
            currentTimerTask?.cancel()
            mTimer?.cancel()
            currentTimerTask = this
            mTimer = Timer()
            mTimer!!.schedule(newTimerTask(), 30000)
        }}
    }

    override fun onDestroy() {
        // 리시버 해제
        mBroadcastManager.unregisterReceiver(mBroadcastReceiver)
        Log.d("test", "Service Exit")
        super.onDestroy()
    }

    private fun requestNodeRoutes(){
        // 비동기식 Request를 위해 enqueue()를 호출
        Singleton.retrofit.getNodeRoute().enqueue(object : Callback<ArrayList<BusInformation>>{
            override fun onFailure(call: Call<ArrayList<BusInformation>>, t: Throwable) {
                //TODO 에러 표시
                Log.e(LOG_TAG, "requestNodeRoutes", t)
            }
            // Response가 들어오면 BusInformation 객체를 파싱
            override fun onResponse(call: Call<ArrayList<BusInformation>>, response: Response<ArrayList<BusInformation>>) {
                val newMap = mutableMapOf<String, BusInformation>()

                newMap["송내"] = (BusInformation("송내","",800,840,BusInformation.BusType.TONG,
                        arrayListOf(
                                BusRoutenode(0,"송내남부역CU"),
                                BusRoutenode(1,"미추홀캠퍼스"),
                                BusRoutenode(2,"송도캠퍼스")
                        ),"앱"))
                newMap["수원"] = (BusInformation("수원-안산-시흥","",640,830,BusInformation.BusType.TONG,
                        arrayListOf(
                                BusRoutenode(1,"수원역 4번출구"),
                                BusRoutenode(3,"상록수역 1번출구"),
                                BusRoutenode(5,"안산 중앙역"),
                                BusRoutenode(7,"안산역 제1승차장"),
                                BusRoutenode(9,"함현중학교 정문"),
                                BusRoutenode(11,"미추홀캠퍼스"),
                                BusRoutenode(13,"송도캠퍼스")
                        ),"센"))
                newMap["일산"] = (BusInformation("일산-김포","",640,830,BusInformation.BusType.TONG,
                        arrayListOf(
                                BusRoutenode(1,"마두역 5번출구"),
                                BusRoutenode(3,"대화역 4번출구"),
                                BusRoutenode(5,"장기동 예가아파트"),
                                BusRoutenode(7,"김포IC"),
                                BusRoutenode(9,"미추홀캠퍼스"),
                                BusRoutenode(11,"송도캠퍼스")
                        ),"터"))
                newMap["청라"] = (BusInformation("청라","",730,845,BusInformation.BusType.TONG,
                        arrayListOf(
                                BusRoutenode(1,"검암역 1번출구"),
                                BusRoutenode(3,"가정역 4번출구"),
                                BusRoutenode(5,"미추홀캠퍼스"),
                                BusRoutenode(7,"송도캠퍼스")
                        ),"I"))
                newMap["광명"] = (BusInformation("광명","",740,850,BusInformation.BusType.TONG,
                        arrayListOf(
                                BusRoutenode(1,"석수역 1번출구"),
                                BusRoutenode(3,"미추홀캠퍼스"),
                                BusRoutenode(5,"송도캠퍼스")
                        ),"N"))

                // 버스 번호 Map
                response.body()!!.forEach {
                    newMap[it.no] = it
                }
                newMap

                Singleton.busInfo.set(newMap)
            }
        })
    }

    private fun requestArrivalInfo(callback: (() -> Unit)? = null){
        // TODO 콜백, 응답 단일처리
        Singleton.retrofit.getFromArrivalInfo().enqueue(object : Callback<ArrayList<ArrivalFromNodeInfo>>{
            override fun onFailure(call: Call<ArrayList<ArrivalFromNodeInfo>>, t: Throwable) {
                //TODO 에러 표시
                Log.e(LOG_TAG, "requestArrivalInfo getFrom", t)
//                stopSelf()
            }
            // Response가 들어오면 ArrivalFromNodeInfo 객체를 파싱
            override fun onResponse(call: Call<ArrayList<ArrivalFromNodeInfo>>, response: Response<ArrayList<ArrivalFromNodeInfo>>) {
                Singleton.arrivalFromInfo.set(response.body())
                if(callback != null)
                    callback()
            }
        })

        Singleton.retrofit.getSBgps().enqueue(object : Callback<ArrayList<SchoolBusGPS>>{
            override fun onFailure(call: Call<ArrayList<SchoolBusGPS>>, t: Throwable) {
                //TODO 에러 표시
                Log.e(LOG_TAG, "requestSBgps", t)
            }

            // Response가 들어오면 BusInformation 객체를 파싱
            override fun onResponse(call: Call<ArrayList<SchoolBusGPS>>, response: Response<ArrayList<SchoolBusGPS>>) {
                Singleton.schoolbusGPS.set(response.body())

                if(callback != null)
                    callback()
            }
        })
    }

    private val mBroadcastReceiver = object : BroadcastReceiver() {
        // 필터링된 intent별로 서비스 선택
        override fun onReceive(context: Context, intent: Intent) {
            when(intent.action){
                LocalIntent.FIRST_DATA_REQUEST.value -> {
                    Log.d("test", "Service received first data request")
                    if(Singleton.arrivalFromInfo.get() == null){
                        // TODO 에러 표시
                    }
                    else {
                        // FIXME 타이밍이 빨라 Fragment들이 수신 못하는 문제가 간혹 있음.
                        // Timer사용해서 재전송?
                        val newIntent = Intent(LocalIntent.FIRST_DATA_RESPONSE.value)
                        mBroadcastManager.sendBroadcast(newIntent)
                        Log.d("test", "Service sent first data response")
                    }
                }
                // 서비스 종료
                LocalIntent.SERVICE_EXIT.value -> {
                    mTimer?.cancel()
                    currentTimerTask?.cancel()
                    stopSelf()
                }
                // 도착 데이터를 다시 요청
                LocalIntent.ARRIVAL_DATA_REFRESH_REQUEST.value -> {
                    Log.d("test", "Service received ARRIVAL_DATA_REFRESH_REQUEST")
                    requestArrivalInfo()
                }

                LocalIntent.FAVORITE_CLICK.value -> {
                    requestArrivalInfo()
                }
            }
        }
    }
}


