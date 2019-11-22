package com.appcenter.inubus.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Byoung Mean on 2019-09-25.
 */

// 각각의 버스 도착정보를 담을 객체
data class SchoolBusGPS(
        @SerializedName("routeId")
        val busTime : BusTime,

        @SerializedName("status")
        var status : Int = -1,

        @SerializedName("location")
        var location : Int = -1,

        @SerializedName("lat")
        var lat : Double = -1.0,

        @SerializedName("lng")
        val lng : Double = -1.0

){
    enum class BusTime(val routeID: String, val startTime : String) {
        @SerializedName("송내")
        SONGNAE("송내","08:00\n09:00"),

        @SerializedName("수원")
        SUWON("수원-안산-시흥","06:40"),

        @SerializedName("일산")
        ILSAN("일산-김포","06:40"),

        @SerializedName("청라")
        CHUNGRA("청라","07:30"),

        @SerializedName("광명")
        GWANGMYOUNG("광명", "07:40");
    }
}