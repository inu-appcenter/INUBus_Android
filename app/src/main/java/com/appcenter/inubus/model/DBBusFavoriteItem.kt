package com.appcenter.inubus.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DBBusFavoriteItem(
        @PrimaryKey
        var no : String = ""
) {
    // id 0은 무시 -> id가 1부터 시작
    constructor(): this("")
}