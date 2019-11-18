package com.inu.bus.util

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.inu.bus.model.DBBusFavoriteItem
import com.inu.bus.model.DBSearchHistoryItem
import com.inu.bus.util.Singleton.DB_VERSION

/**
 * Created by Minjae Son on 2018-08-10.
 */
//// searchHistoryDAO의 SQL을 RoomDatabase로 처리
//@Database(entities = [DBSearchHistoryItem::class], version = DB_VERSION)
//abstract class AppDatabase : RoomDatabase() {
//    abstract fun searchHistoryDAO(): SearchHistoryDAO

@Database(entities = [DBBusFavoriteItem::class, DBSearchHistoryItem::class], version = DB_VERSION)
abstract class AppDatabase : RoomDatabase() {
    abstract fun busfavoriteDAO(): BusFavoriteDAO
    abstract fun searchhistoryDAO() : SearchHistoryDAO

    companion object {
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase? {
            if(INSTANCE == null) {
                synchronized(AppDatabase::class) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext
                            , AppDatabase::class.java,"inubus.db")
                            .fallbackToDestructiveMigration()
                            .build()
                }
            }
            return INSTANCE
        }
    }
}