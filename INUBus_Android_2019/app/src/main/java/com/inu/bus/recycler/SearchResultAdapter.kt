package com.inu.bus.recycler

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.inu.bus.databinding.SearchResultListItemBinding
import com.inu.bus.model.BusInformation
import com.inu.bus.model.BusRoutenode
import com.inu.bus.recycler.SearchResultAdapter.SearchResultViewHolder
import com.inu.bus.util.Singleton

class SearchResultAdapter() : RecyclerView.Adapter<SearchResultViewHolder>() {

//    var mHistoryList = arrayListOf<BusInformation>(
//            BusInformation("333333","123",1,1,BusInformation.BusType.RED,ArrayList<BusRoutenode>(),"3"))

    var mSearchList = ArrayList(Singleton.busInfo.get()!!.values)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultViewHolder {
        val binding = SearchResultListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchResultViewHolder(binding,parent.context)
    }

    override fun onBindViewHolder(holder: SearchResultViewHolder, position: Int) {
        holder.bind(mSearchList[position],position)
    }

    override fun getItemCount(): Int {
        return mSearchList.size
    }

    inner class SearchResultViewHolder(private val mBinding : SearchResultListItemBinding,
            private val mContext : Context = mBinding.root.context) : RecyclerView.ViewHolder(mBinding.root) {

        fun bind(data : BusInformation, position : Int){

            mBinding.item = data

        }
    }

}