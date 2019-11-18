package com.inu.bus.util

import android.app.Activity
import android.content.Context
import android.databinding.ObservableField
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.inu.bus.model.*
import com.inu.bus.recycler.SearchHistoryAdapter
import com.inu.bus.server
import com.inu.bus.util.Singleton.myPackageName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


/**
 * Created by Minjae Son on 2018-08-07.
 * Updated by ByoungMean on 2019-10-29.
 */

object Singleton{

    val retrofit: BusRetrofitService = Retrofit.Builder()
            // 유출되지 않도록 서버 URL을 따로 저장
            .baseUrl(server.serverUrl)
            // JSON파일을 변환해주는 Gson 지정
            .addConverterFactory(GsonConverterFactory.create())
            // 엔드포인트 설정
            .build().create(BusRetrofitService::class.java)
    val msgRetrofit = Retrofit.Builder().baseUrl(server.serverUrl).addConverterFactory(GsonConverterFactory.create()).build().create(MsgRetrofitService::class.java)!!

    // ObservableField를 통해 데이터 값이 변경될 때 View를 자동으로 업데이트
    val busInfo  = ObservableField(mutableMapOf<String, BusInformation>())
    var arrivalFromInfo = ObservableField<ArrayList<ArrivalFromNodeInfo>>()
    val schoolbusGPS = ObservableField<ArrayList<SchoolBusGPS>>()
    const val myPackageName = "com.sayheybongmany.inubus"
    const val LOG_TAG = "INU Bus"
    const val DB_VERSION = 2

    // 키보드 숨기기
    fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager

        // 현재 View의 포커스를 통해 windowToken 얻기
        var view = activity.currentFocus
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    // 버스 요금 Map
    val busCost = mutableMapOf(
            Pair("6405", 2600),
            Pair("1301", 2650),
            Pair("3002", 2400),
            Pair("92", 950),
            Pair("else", 1250)
    )
    // 스쿨 버스 노선
    val SchoolBusRoute= mutableMapOf(
            Pair("송내",arrayListOf(
                    BusRoutenode(1,"송내남부역CU"),
                    BusRoutenode(3,"미추홀캠퍼스"),
                    BusRoutenode(5,"송도캠퍼스")
            )),
            Pair("수원", arrayListOf(
                    BusRoutenode(1,"수원역 4번출구"),
                    BusRoutenode(3,"상록수역 1번출구"),
                    BusRoutenode(5,"안산 중앙역"),
                    BusRoutenode(7,"안산역 제1승차장"),
                    BusRoutenode(9,"함현중학교 정문"),
                    BusRoutenode(11,"미추홀캠퍼스"),
                    BusRoutenode(13,"송도캠퍼스")
            )),
            Pair("일산", arrayListOf(
                    BusRoutenode(1,"마두역 5번출구"),
                    BusRoutenode(3,"대화역 4번출구"),
                    BusRoutenode(5,"장기동 예가아파트"),
                    BusRoutenode(7,"김포IC"),
                    BusRoutenode(9,"미추홀캠퍼스"),
                    BusRoutenode(11,"송도캠퍼스")
            )),
            Pair("청라", arrayListOf(
                    BusRoutenode(1,"검암역 1번출구"),
                    BusRoutenode(3,"가정역 4번출구"),
                    BusRoutenode(5,"미추홀캠퍼스"),
                    BusRoutenode(7,"송도캠퍼스")
            )),
            Pair("광명", arrayListOf(
                    BusRoutenode(1,"석수역 1번출구"),
                    BusRoutenode(3,"미추홀캠퍼스"),
                    BusRoutenode(5,"송도캠퍼스")
            ))
    )

}
// 상태에 따른 Intent값 설정
enum class LocalIntent(val value : String) : CharSequence by value {
    FIRST_DATA_REQUEST("$myPackageName.FIRST_DATA_REQUEST"),
    FIRST_DATA_RESPONSE("$myPackageName.FIRST_DATA_RESPONSE"),
    ARRIVAL_DATA_REFRESH_REQUEST("$myPackageName.ARRIVAL_DATA_REFRESH_REQUEST"),
    FAVORITE_CLICK("$myPackageName.FAVORITE_CLICK"),
    SERVICE_EXIT("$myPackageName.SERVICE_EXIT"),
    NOTIFY_FRAGMENT_READY("$myPackageName.NOTIFY_FRAGMENT_READY");

    override fun toString() = value
}