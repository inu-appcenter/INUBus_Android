package com.inu.bus.recycler

import android.support.v7.widget.RecyclerView
import com.inu.bus.databinding.RecyclerArrivalSeparatorBinding

/**
 * Created by Minjae Son on 2018-08-25.
 */
class ViewHolderArrivalSection(private val mBinding : RecyclerArrivalSeparatorBinding) : RecyclerView.ViewHolder(mBinding.root) {

    // recycler_arrival_separator.xml 바인딩
    fun bind(sectionText: String){
        mBinding.tvSection.text = sectionText
    }

}