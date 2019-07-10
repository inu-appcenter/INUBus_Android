package com.inu.bus.recycler

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.inu.bus.databinding.RecyclerDestinationItemBinding

/**
 * Created by Minjae Son on 2018-08-18.
 */
class RecyclerAdapterDestination : RecyclerView.Adapter<DestinationRecyclerViewHolder>() {

    // TODO 해당 타이틀이 정류장이름이 아닌 동네 이름이 되는 경우, 하위 정류장들을 같이 검색해줘야함.
    private val mDefaultNode = mutableListOf("인천대입구", "지식정보단지", "해양경찰청", "구월동", "신연수", "선학")

    // DestinationRecyclerViewHolder 레이아웃 연결
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DestinationRecyclerViewHolder {
        val binding = RecyclerDestinationItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DestinationRecyclerViewHolder(binding)
    }

    // 아이템 개수 리턴
    override fun getItemCount(): Int = mDefaultNode.size

    // 각 뷰홀더에 데이터 리스트 바인딩
    override fun onBindViewHolder(holder: DestinationRecyclerViewHolder, position: Int) {
        holder.bind(mDefaultNode[position])
    }
}