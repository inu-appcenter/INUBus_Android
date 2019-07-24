package com.inu.bus.util

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import com.inu.bus.model.DBBusFavoriteItem

@Dao
interface BusFavoriteDAO{
    @Insert
    fun insert(contacts : DBBusFavoriteItem )

    @Delete
    fun delete(item : DBBusFavoriteItem)

    @Query("SELECT * FROM DBBusFavoriteItem")
    fun getAll(): List<DBBusFavoriteItem>
}