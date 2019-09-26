package com.inu.bus.recycler

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.inu.bus.R
import com.inu.bus.activity.MainActivity
import com.inu.bus.activity.RouteActivity
import com.inu.bus.activity.SearchActivity
import com.inu.bus.databinding.SearchResultListItemBinding
import com.inu.bus.fragment.SearchHistoryFragment
import com.inu.bus.model.*
import com.inu.bus.recycler.SearchResultAdapter.SearchResultViewHolder
import com.inu.bus.util.AppDatabase
import com.inu.bus.util.Singleton
import kotlinx.android.synthetic.main.activity_search.*

class SearchResultAdapter() : RecyclerView.Adapter<SearchResultViewHolder>() {

//    var mHistoryList = arrayListOf<BusInformation>(
//            BusInformation("333333","123",1,1,BusInformation.BusType.RED,ArrayList<BusRoutenode>(),"3"))

    var mSearchList = ArrayList(Singleton.busInfo.get()!!.values)

    var mFilteredList = arrayListOf<SearchResultNode>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultViewHolder {
        val binding = SearchResultListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchResultViewHolder(binding,parent.context)
    }

    override fun onBindViewHolder(holder: SearchResultViewHolder, position: Int) {
        holder.bind(mFilteredList[position],position)
    }

    override fun getItemCount(): Int {
        return mFilteredList.size
    }

    inner class SearchResultViewHolder(private val mBinding : SearchResultListItemBinding,
            private val mContext : Context = mBinding.root.context) : RecyclerView.ViewHolder(mBinding.root) {

        fun bind(data : SearchResultNode, position : Int){
            mBinding.item = data

            val mBtnGoroute = itemView.findViewById<ConstraintLayout>(R.id.btn_search_select)
            val context = mBinding.root.context

            mBtnGoroute.setOnClickListener {
                var newSHitem = DBSearchHistoryItem()
                newSHitem.name = data.title
                (context as SearchActivity).insertHistory(newSHitem)

                val context = mBinding.root.context
                val intent = Intent(context, RouteActivity::class.java)
                intent.putExtra("routeNo", data.title)
                context.startActivity(intent)
            }
        }
    }

    fun filter(str : String) {

        var filtered = arrayListOf<SearchResultNode>()

        mSearchList.forEach {
            if(it.no.contains(str))
                filtered.add(SearchResultNode(it.no,it.type.value,it.type.color))
//            정류장 검색 추가
//            it.nodeList.forEach{ node ->
//                if(node.nodeName.contains(str))
//                    filtered.add(SearchResultNode(node.nodeName,node.nodeNo.toString(), Color.parseColor("#0061f4")))
//            }
        }

        // 도착 정보를 비교해서 업데이트
        mFilteredList = filtered
        notifyDataSetChanged()
    }

}