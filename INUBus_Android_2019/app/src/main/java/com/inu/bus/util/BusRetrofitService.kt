package com.inu.bus.util

import com.inu.bus.model.ArrivalFromNodeInfo
import com.inu.bus.model.ArrivalToNodeInfo
import com.inu.bus.model.BusInformation
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by Minjae Son on 2018-08-07.
 */

// baseURL에 엔드 포인트를 추가해 서버 데이터를 GET방식 처리
interface BusRetrofitService{

    @GET("/arrivalInfo")
    fun getFromArrivalInfo() : Call<ArrayList<ArrivalFromNodeInfo>>

    @GET("/arrivalInfoTo")
    fun getToArrivalInfo() : Call<ArrayList<ArrivalToNodeInfo>>


    @GET("/nodeData")
    fun getNodeRoute() : Call<ArrayList<BusInformation>>

    @GET("/nodeData/{nodenum}")
    fun getNodeRoute(@Query("nodenum") no : String) : Call<ArrayList<BusInformation>>

//    @GET("/arrivalinfoSeoul")
//    fun getSeoulArrivalInfo()
}