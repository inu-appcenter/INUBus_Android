package com.inu.bus.recycler

import android.content.Intent
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.inu.bus.R
import com.inu.bus.activity.RouteActivity
import com.inu.bus.activity.SearchActivity
import com.inu.bus.databinding.SearchResultListItemBinding
import com.inu.bus.model.DBSearchHistoryItem
import com.inu.bus.model.SearchResultNode
import com.inu.bus.recycler.SearchResultAdapter.SearchResultViewHolder
import com.inu.bus.util.Singleton

class SearchResultAdapter() : RecyclerView.Adapter<SearchResultViewHolder>() {

    var mSearchList = ArrayList(Singleton.busInfo.get()!!.values)
    private var mFilteredList = arrayListOf<SearchResultNode>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultViewHolder {
        val binding = SearchResultListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchResultViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchResultViewHolder, position: Int) {
        holder.bind(mFilteredList[position])
    }

    override fun getItemCount(): Int {
        return mFilteredList.size
    }

    inner class SearchResultViewHolder(private val mBinding: SearchResultListItemBinding) : RecyclerView.ViewHolder(mBinding.root) {

        fun bind(data: SearchResultNode){
            mBinding.item = data

            val mBtnGoroute = itemView.findViewById<ConstraintLayout>(R.id.btn_search_select)
            val context = mBinding.root.context

            mBtnGoroute.setOnClickListener {
                var newSHitem = DBSearchHistoryItem()
                newSHitem.name = data.title
                newSHitem.typenumber = data.typenumber
                newSHitem.color = data.color
                (context as SearchActivity).insertHistory(newSHitem)

                val context = mBinding.root.context
                val intent = Intent(context, RouteActivity::class.java)
                if(data.typenumber == "통학버스") intent.putExtra("routeNo", "R" + data.title)
                else intent.putExtra("routeNo", data.title)
                context.startActivity(intent)
            }
        }
    }

    fun filter(str : String) {

        var filtered = arrayListOf<SearchResultNode>()

        mSearchList.forEach {
            if(it.no.contains(str))
                filtered.add(SearchResultNode(it.no,it.type.value,it.type.color))
            else if(it.type.value.contains(str))
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