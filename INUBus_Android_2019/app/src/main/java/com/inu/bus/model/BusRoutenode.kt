package com.inu.bus.model

import com.google.gson.annotations.SerializedName

data class BusRoutenode (
        @SerializedName("nodeno")
        val nodeNo : Int,

        @SerializedName("nodenm")
        var nodeName : String
)