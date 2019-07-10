package com.inu.bus.util

import android.app.Activity
import android.databinding.ObservableField
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.inu.bus.model.ArrivalFromNodeInfo
import com.inu.bus.model.ArrivalToNodeInfo
import com.inu.bus.model.BusInformation
import com.inu.bus.server
import com.inu.bus.util.Singleton.myPackageName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


/**
 * Created by Minjae Son on 2018-08-07.
 */

object Singleton{

    val retrofit = Retrofit.Builder()
            // 유출되지 않도록 서버 URL을 따로 저장
            .baseUrl(server.serverUrl)
            // JSON파일을 변환해주는 Gson 지정
            .addConverterFactory(GsonConverterFactory.create())
            // 엔드포인트 설정
            .build().create(BusRetrofitService::class.java)
    val msgRetrofit = Retrofit.Builder().baseUrl(server.serverUrl).addConverterFactory(GsonConverterFactory.create()).build().create(MsgRetrofitService::class.java)

    // ObservableField를 통해 데이터 값이 변경될 때 View를 자동으로 업데이트
    val busInfo  = ObservableField(mutableMapOf<String, BusInformation>())
    var arrivalFromInfo = ObservableField<ArrayList<ArrivalFromNodeInfo>>()
    var arrivalToInfo = ObservableField<ArrayList<ArrivalToNodeInfo>>()
    const val myPackageName = "com.bungabear.inubus"
    const val LOG_TAG = "INU Bus"
    const val DB_VERSION = 1

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
}
// 상태에 따른 Intent값 설정
enum class LocalIntent(val value : String) : CharSequence by value {
    FIRST_DATA_REQUEST("$myPackageName.FIRST_DATA_REQUEST"),
    FIRST_DATA_RESPONSE("$myPackageName.FIRST_DATA_RESPONSE"),
    ARRIVAL_DATA_REFRESH_REQUEST("$myPackageName.ARRIVAL_DATA_REFRESH_REQUEST"),
    ARRIVAL_DATA_REFRESHED("$myPackageName.ARRIVAL_DATA_REFRESHED"),
    SERVICE_EXIT("$myPackageName.SERVICE_EXIT"),
    NOTIFY_FRAGMENT_READY("$myPackageName.NOTIFY_FRAGMENT_READY");

    override fun toString() = value
}