package com.appcenter.inubus.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.graphics.Color

/**
 * Created by Minjae Son on 2018-08-10.
 * Updated by ByoungMean on 2019-10-18.
 */

// 검색 기록을 담을 객체
@Entity
data class DBSearchHistoryItem(
        @PrimaryKey(autoGenerate = true)
        var id : Int = 0,
        var name : String = "",
        var typenumber : String = "",
        var color : Int = Color.parseColor("#123123"),
        var date : Long = System.currentTimeMillis()
)  {
    // id 0은 무시 -> id가 1부터 시작
}