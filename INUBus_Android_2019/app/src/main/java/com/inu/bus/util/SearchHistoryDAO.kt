package com.inu.bus.util

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import com.inu.bus.model.DBSearchHistoryItem

/**
 * Created by Minjae Son on 2018-08-10.
 */

@Dao

// 검색 기록에 대한 SQL 쿼리 메소드 저장
interface SearchHistoryDAO{
    @Insert
    fun insert(contacts : DBSearchHistoryItem )

    @Delete
    fun delete(item : DBSearchHistoryItem)

    @Query("SELECT * FROM DBSearchHistoryItem")
    fun getAll(): List<DBSearchHistoryItem>
}