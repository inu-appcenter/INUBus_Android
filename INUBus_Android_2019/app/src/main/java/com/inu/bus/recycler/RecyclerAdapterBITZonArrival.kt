package com.inu.bus.recycler

import android.databinding.ObservableBoolean
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.inu.bus.databinding.RecyclerArrivalBitzonSeparatorBinding
import com.inu.bus.databinding.RecyclerArrivalHeaderBinding
import com.inu.bus.databinding.RecyclerArrivalItemBinding
import com.inu.bus.model.ArrivalToNodeInfo
import com.inu.bus.model.RecyclerArrivalItem
import com.inu.bus.util.ArrivalInfoDiffUtil
import java.util.*
import kotlin.collections.ArrayList


/**
 * Created by Minjae Son on 2018-08-07.
 */

class RecyclerAdapterBITZonArrival(val mStrBusStop : String) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    var isShowing = ObservableBoolean(false)
    private val mArrivalItems = ArrayList<RecyclerArrivalItem>()

    // ItemType에 따라 생성할 뷰홀더 객체 선택
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val type = RecyclerArrivalItem.ItemType.findByOrdinal(viewType)
        val layoutInflater = LayoutInflater.from(parent.context)
        return when(type){
            RecyclerArrivalItem.ItemType.Header->
                ViewHolderArrivalHeader(RecyclerArrivalHeaderBinding.inflate(layoutInflater, parent, false))
            RecyclerArrivalItem.ItemType.SectionHeader->
                ViewHolderArrivalBitZonSection(RecyclerArrivalBitzonSeparatorBinding.inflate(layoutInflater, parent, false))
            RecyclerArrivalItem.ItemType.ArrivalInfo->
                ViewHolderArrivalItem(RecyclerArrivalItemBinding.inflate(layoutInflater, parent, false), isShowing)
        }
    }
    // ItemType에 따라 각 뷰 홀더에 데이터 연결
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(mArrivalItems[position].itemType){
            RecyclerArrivalItem.ItemType.Header->{ }
            RecyclerArrivalItem.ItemType.SectionHeader->{
                (holder as ViewHolderArrivalBitZonSection).bind(mArrivalItems[position].sectionHeader!!, mArrivalItems[position].needButton )
            }
            RecyclerArrivalItem.ItemType.ArrivalInfo->{
                val customInfo = mArrivalItems[position].arrivalInfo!!
                (holder as ViewHolderArrivalItem).bind(mArrivalItems[position].arrivalInfo!!)
            }
        }
    }
    // 아이템 개수 리턴
    override fun getItemCount(): Int = mArrivalItems.size
    // 아이템 타입 리턴
    override fun getItemViewType(position: Int): Int = mArrivalItems[position].itemType.ordinal

    fun applyDataSet(items: ArrayList<ArrivalToNodeInfo>) {
        val newDataSet = ArrayList<RecyclerArrivalItem>()

        // 도착정보가 없으면 리스트 비움
        if(items.isEmpty()){
            mArrivalItems.clear()
            notifyDataSetChanged()
            return
        }

        // 정렬
        val sorted = items.sortedWith(
                Comparator { o1, o2 ->
                    o1.id.ordinal - o2.id.ordinal
                }
        )

        sorted.forEachIndexed { index, it ->
            if(it.id == ArrivalToNodeInfo.ID.ICB164000395){
                if(index != 0){
                    newDataSet.add(0, RecyclerArrivalItem("지식정보단지", true))
                }
                newDataSet.add(RecyclerArrivalItem("인천대입구"))
            }
            it.data.sortWith(kotlin.Comparator { o1, o2 ->
                when{
                    (o1.type != o2.type) -> o1.type!!.ordinal - o2.type!!.ordinal
                    else -> o1.no.compareTo(o2.no)
                }
            })

            it.data.forEach { item ->
                // 출구 정보를 보여주기위해 배차간격 텍스트를 재활용
                if(it.id.exitName != ""){
                    item.intervalString = it.id.exitName
                }
                else {
                    item.intervalString = "${item.interval}분"
                }
                newDataSet.add(RecyclerArrivalItem(item)) }
        }
        // 데이터 목록 업데이트
        val diffUtil = ArrivalInfoDiffUtil(mArrivalItems, newDataSet)
        val result = DiffUtil.calculateDiff(diffUtil)
        mArrivalItems.clear()
        mArrivalItems.addAll(newDataSet)
        result.dispatchUpdatesTo(this)
    }

    // 아이템이 화면에 보일때만 Ticker가 작동하도록 설정
    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)
        if(holder is ViewHolderArrivalItem){
            if(isShowing.get()){
                holder.startTick()
            }
        }
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        if(holder is ViewHolderArrivalItem){
            holder.stopTick()
        }
    }
}