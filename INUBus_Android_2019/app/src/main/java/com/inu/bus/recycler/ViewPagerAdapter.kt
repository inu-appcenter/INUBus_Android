package com.inu.bus.recycler

import android.content.Context
import android.os.Parcelable
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter

/**
 * Created by Minjae Son on 2018-08-07.
 */
class ViewPagerAdapter(mFm: FragmentManager, mContext: Context) : FragmentStatePagerAdapter(mFm){

    val fragments = ArrayList<Fragment>()

    // 저장된 State 로드
    override fun restoreState(state: Parcelable?, loader: ClassLoader?) {
//        re-attache crash fix
//        super.restoreState(state, loader)
    }

    // Fragment 추가
    override fun getItem(position: Int): Fragment = fragments[position]

    // Fragment 개수 설정
    override fun getCount(): Int = fragments.size

    // Fragment 추가
    fun addFragment(fragment : Fragment){
        fragments.add(fragment)
    }

}