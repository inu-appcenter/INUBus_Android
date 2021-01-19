package com.appcenter.inubus.util

import androidx.recyclerview.widget.DiffUtil
import com.appcenter.inubus.model.RecyclerArrivalItem

/**
 * Created by Minjae Son on 2018-08-08.
 * 도착정보 RecyclerItems에 대하여 추가, 삭제, 데이터 변경의 적용을 계산해주는 유틸
 */

class ArrivalInfoDiffUtil(private val mOldList : ArrayList<RecyclerArrivalItem>, private val mNewList : ArrayList<RecyclerArrivalItem>) : DiffUtil.Callback() {

    // 예전 리스트와 새로운 리스트가 같은 아이템인지 비교
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return mOldList[oldItemPosition].equals(mNewList[newItemPosition])
    }

    override fun getOldListSize(): Int = mOldList.size

    override fun getNewListSize(): Int = mNewList.size

    // 예전 아이템과 새로운 아이템의 내용이 같은지 비교
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val old = mOldList[oldItemPosition]
        val new = mNewList[newItemPosition]
        return if(old.itemType == RecyclerArrivalItem.ItemType.ArrivalInfo)
             old.arrivalInfo!!.arrival == new.arrivalInfo!!.arrival
        else false
    }

    // areItemsTheSame() && !areContentsTheSame() 일때 호출
    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        // Implement method if you're going to use ItemAnimator
        return super.getChangePayload(oldItemPosition, newItemPosition)
    }
}