package com.appcenter.inubus.util

import com.appcenter.inubus.model.ArrivalFromNodeInfo
import com.appcenter.inubus.model.BusInformation
import com.appcenter.inubus.model.SchoolBusGPS
import retrofit2.Call
import retrofit2.http.GET

/**
 * Created by Minjae Son on 2018-08-07.
 */

// baseURL에 엔드 포인트를 추가해 서버 데이터를 GET방식 처리
interface BusRetrofitService{

    @GET("/arrivalInfo")
    fun getFromArrivalInfo() : Call<ArrayList<ArrivalFromNodeInfo>>

    @GET("/nodeData")
    fun getNodeRoute() : Call<ArrayList<BusInformation>>

    @GET("/gps")
    fun getSBgps() : Call<ArrayList<SchoolBusGPS>>

}