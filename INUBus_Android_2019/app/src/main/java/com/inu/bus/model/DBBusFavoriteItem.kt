package com.inu.bus.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey

@Entity
data class DBBusFavoriteItem(
        @PrimaryKey(autoGenerate = true)
        var id: Int = 0,
        var favorite:Boolean = false

) {
    // id 0은 무시 -> id가 1부터 시작
    @Ignore
    constructor() : this(0)
}