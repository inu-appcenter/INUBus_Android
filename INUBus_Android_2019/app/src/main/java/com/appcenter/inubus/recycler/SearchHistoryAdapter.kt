package com.appcenter.inubus.recycler

import android.content.Context
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import com.appcenter.inubus.R
import com.appcenter.inubus.activity.MainActivity
import com.appcenter.inubus.activity.SearchActivity
import com.appcenter.inubus.databinding.SearchHistoryListItemBinding
import com.appcenter.inubus.model.DBSearchHistoryItem
import com.appcenter.inubus.recycler.SearchHistoryAdapter.SearchHistoryViewHolder
import com.appcenter.inubus.util.AppDatabase

/**
 * Created by Minjae Son on 2018-08-10.
 * Updated by ByoungMean on 2019-10-21.
 */

class SearchHistoryAdapter : androidx.recyclerview.widget.RecyclerView.Adapter<SearchHistoryViewHolder>() {

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
            private val mContext : Context = mBinding.root.context) : androidx.recyclerview.widget.RecyclerView.ViewHolder(mBinding.root) {

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
                    mDB!!.searchhistoryDAO().delete(data)
                    mHistoryList = mDB!!.searchhistoryDAO().getAll() as ArrayList
                }
                val thread = Thread(r)
                thread.start()
                mHistoryList.remove(data)
                notifyDataSetChanged()
                Log.d("fastfastfast","delete click")
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