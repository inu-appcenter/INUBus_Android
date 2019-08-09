package com.inu.bus.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey

/**
 * Created by Minjae Son on 2018-08-10.
 */

// 검색 기록을 담을 객체
@Entity
data class DBSearchHistoryItem(
        @PrimaryKey(autoGenerate = true)
        var id : Int = 0,
        var name : String  = "",
        var date : Long = System.currentTimeMillis()
)  {
    // id 0은 무시 -> id가 1부터 시작
}