package com.appcenter.inubus.model

import com.google.gson.annotations.SerializedName

data class SearchResultNode (
        @SerializedName("title")
        val title : String,

        @SerializedName("typenumber")
        var typenumber : String,

        @SerializedName("color")
        val color : Int
)