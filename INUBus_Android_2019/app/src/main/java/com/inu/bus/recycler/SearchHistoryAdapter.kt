package com.inu.bus.recycler

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.inu.bus.activity.MainActivity
import com.inu.bus.model.DBSearchHistoryItem
import com.inu.bus.util.millisToDate
import kotlinx.android.synthetic.main.search_history_list_item.view.*

/**
 * Created by Minjae Son on 2018-08-10.
 */

//class SearchHistoryAdapter(private val mContext : Context, private val mLayout : Int) : ArrayAdapter<DBSearchHistoryItem>(mContext, mLayout) {
//
//    // 검색결과 데이터베이스
//    private val mDB = MainActivity.DB
//    private var mHistoryList = mDB.searchHistoryDAO().getAll()
//
//    // mDB의 검색기록을 mLayout에 연결
//    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
//        val root = convertView ?: LayoutInflater.from(mContext).inflate(mLayout, parent, false)
//        root.tv_autocomplete_item_name.text = mHistoryList[position].name
//        root.tv_autocomplete_item_date.text = millisToDate( mHistoryList[position].date)
//        root.btn_autocomplete_item_delete.setOnClickListener {
//            mDB.searchHistoryDAO().delete(mHistoryList[position])
//            notifyDataSetChanged()
//        }
//        return root
//    }
//    // 아이템 개수 호출
//    override fun getCount(): Int = mHistoryList.size
//
//    fun refreshHistory(){
//        mHistoryList = mDB.searchHistoryDAO().getAll()
//    }
//}