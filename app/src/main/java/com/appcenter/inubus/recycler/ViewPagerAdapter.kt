package com.appcenter.inubus.recycler

import android.content.Context
import android.os.Parcelable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

/**
 * Created by Minjae Son on 2018-08-07.
 */
class ViewPagerAdapter(mFm: androidx.fragment.app.FragmentManager, mContext: Context) : androidx.fragment.app.FragmentStatePagerAdapter(mFm){

    val fragments = ArrayList<androidx.fragment.app.Fragment>()

    // 저장된 State 로드
    override fun restoreState(state: Parcelable?, loader: ClassLoader?) {
//        re-attache crash fix
//        super.restoreState(state, loader)
    }

    // 해당 Fragment
    override fun getItem(position: Int): androidx.fragment.app.Fragment = fragments[position]

    // Fragment 개수
    override fun getCount(): Int = fragments.size


    // Fragment 추가
    fun addFragment(fragment : androidx.fragment.app.Fragment){
        fragments.add(fragment)
    }

}