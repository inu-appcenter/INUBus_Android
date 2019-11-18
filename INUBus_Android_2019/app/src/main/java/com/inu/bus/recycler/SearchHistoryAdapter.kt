package com.inu.bus.recycler

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import com.inu.bus.R
import com.inu.bus.activity.MainActivity
import com.inu.bus.activity.SearchActivity
import com.inu.bus.databinding.SearchHistoryListItemBinding
import com.inu.bus.model.DBSearchHistoryItem
import com.inu.bus.recycler.SearchHistoryAdapter.SearchHistoryViewHolder
import com.inu.bus.util.AppDatabase

/**
 * Created by Minjae Son on 2018-08-10.
 * Updated by ByoungMean on 2019-10-21.
 */

class SearchHistoryAdapter : RecyclerView.Adapter<SearchHistoryViewHolder>() {

    // 검색결과 데이터베이스

    private var mDB = MainActivity().mDB
    var mHistoryList = arrayListOf<DBSearchHistoryItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchHistoryViewHolder {

        val binding = SearchHistoryListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchHistoryViewHolder(binding,parent.context)
    }

    override fun onBindViewHolder(holder: SearchHistoryViewHolder, position: Int) {
        holder.bind(mHistoryList[position])
    }

    override fun getItemCount(): Int {
        return mHistoryList.size
    }

    inner class SearchHistoryViewHolder(private val mBinding : SearchHistoryListItemBinding,
            private val mContext : Context = mBinding.root.context) : RecyclerView.ViewHolder(mBinding.root) {

        fun bind(data: DBSearchHistoryItem){
            mDB = AppDatabase.getInstance(mContext)!!
            val mBtnfinder = itemView.findViewById<ConstraintLayout>(R.id.btn_history_select)
            val mBtndelete = itemView.findViewById<ImageButton>(R.id.btn_autocomplete_item_delete)

            mBinding.item =  data
            mBtnfinder.setOnClickListener {
                SearchActivity.mWrSearchView.get()?.setText(data.name)
            }

            mBtndelete.setOnClickListener {
                val r = Runnable {
                    mDB?.searchhistoryDAO()?.delete(data)
                }
                val thread = Thread(r)
                thread.start()
//                refreshHistory(mContext)
                mHistoryList.remove(data)
                notifyDataSetChanged()
            }
        }
    }

    fun refreshHistory(context : Context){
        val mDB = AppDatabase.getInstance(context)!!
        val r = Runnable {
            try {
                mHistoryList = mDB.searchhistoryDAO()?.getAll() as ArrayList
            } catch (e:Exception){
                Log.d("error","Error - $e")
            }
        }
        val thread = Thread(r)
        thread.start()
        notifyDataSetChanged()
    }

    fun insertHistory(context : Context, data : DBSearchHistoryItem){
        var overlap = false
        mHistoryList.forEach {
            if(it.name == data.name) overlap = true
        }

        if(!overlap){
            val mDB = AppDatabase.getInstance(context)!!
            val r = Runnable {
                mDB.searchhistoryDAO().insert(data)
            }
            val thread = Thread(r)
            thread.start()

            mHistoryList.add(data)
            notifyDataSetChanged()
        }
    }
}