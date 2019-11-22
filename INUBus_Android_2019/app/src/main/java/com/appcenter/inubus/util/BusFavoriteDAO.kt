package com.appcenter.inubus.util

import android.arch.persistence.room.*
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import com.appcenter.inubus.model.DBBusFavoriteItem

@Dao
interface BusFavoriteDAO{

    @Query("SELECT * FROM DBBusFavoriteItem")
    fun getAll(): List<DBBusFavoriteItem>

//    @Query("DELETE FROM DBBusFavoriteItem WHERE `no` in (:id)")
//    fun deleteNumber(id : Int): List<DBBusFavoriteItem>

    @Insert (onConflict = REPLACE)
    fun insert(item : DBBusFavoriteItem )

    @Update (onConflict = REPLACE)
    fun update(item : DBBusFavoriteItem)

    @Delete
    fun delete(item : DBBusFavoriteItem)

    @Query("DELETE from DBBusFavoriteItem")
    fun deleteall()
}