package com.inu.bus.util

import android.app.Application
import android.arch.lifecycle.LiveData
import android.databinding.Observable


class FavoriteRepository(application: Application){

    private val busfavoriteDAO : BusFavoriteDAO by lazy{
        val mDB = AppDatabase.getInstance(application)!!
        mDB.busfavoriteDAO()
    }

//    private val busfavorites : LiveData<List<BusFavoriteDAO>> by lazy {
//        busfavoriteDAO.getAll()
//
//    }
//
//    fun insert(item : BusFavoriteDAO): Observable<Unit> {
//        return Observable
//    }

}