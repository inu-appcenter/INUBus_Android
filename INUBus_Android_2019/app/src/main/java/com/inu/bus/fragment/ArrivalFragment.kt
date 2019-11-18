package com.inu.bus.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.databinding.Observable
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageButton
import android.widget.LinearLayout
import com.inu.bus.R
import com.inu.bus.activity.MainActivity
import com.inu.bus.recycler.ViewPagerAdapter
import com.inu.bus.util.LocalIntent
import com.inu.bus.util.Singleton
import com.inu.bus.util.Singleton.LOG_TAG
import kotlinx.android.synthetic.main.custom_tabicon.view.*
import kotlinx.android.synthetic.main.fragment_arrival_tabs.*


/**
 * Created by Minjae Son on 2018-08-13.
 * Updated by ByoungMean on 2019-09-24.
 */

class ArrivalFragment : Fragment(){

    private lateinit var mFm: FragmentManager
    private lateinit var mContext : Context
    private lateinit var mTabLayoutWrapper : LinearLayout
    private lateinit var mFabRefresh : ImageButton
    private lateinit var mFabRefreshAnimation : RotateAnimation

    companion object {
        fun newInstance(fm : FragmentManager, context : Context) : ArrivalFragment{
            val fragment = ArrivalFragment()
            fragment.mFm = fm
            fragment.mContext = context
            return fragment
        }
    }

    private val mBroadcastManager by lazy { LocalBroadcastManager.getInstance(mContext) }
    private var mFragmentReadyCount = 0
    private val mTabIcons = listOf(R.drawable.tabicon_select, R.drawable.tabicon_select, R.drawable.tabicon_select, R.drawable.tabicon_select)
    private val mTabText = listOf("공대", "자연대", "정문","통학")
    private val mViewPagerAdapter by lazy { ViewPagerAdapter(mFm, mContext) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_arrival_tabs, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mTabLayoutWrapper = ll_fragment_arrival_tabs_tablayout
        // intent 수신
        mBroadcastManager.registerReceiver(mBroadcastReceiver, IntentFilter(LocalIntent.NOTIFY_FRAGMENT_READY.value))
        mBroadcastManager.registerReceiver(mBroadcastReceiver, IntentFilter(LocalIntent.ARRIVAL_DATA_REFRESH_REQUEST.value))
        mBroadcastManager.registerReceiver(mBroadcastReceiver, IntentFilter(LocalIntent.FAVORITE_CLICK.value))

        // tab setting
        mTabIcons.forEachIndexed { index, it ->
            val v = LayoutInflater.from(mContext).inflate(R.layout.custom_tabicon, null, false)
            v.iv_tabicon.setBackgroundResource(it)
            v.tv_tabicon.text = mTabText[index]
            fragment_arrival_tablayout.addTab(fragment_arrival_tablayout.newTab())
            fragment_arrival_tablayout.getTabAt(index)!!.customView = v
        }

        // ViewPagerAdapter에 프레그먼트 추가
        mViewPagerAdapter.addFragment(ArrivalFragmentTab.newInstance(mContext, "engineer"))
        mViewPagerAdapter.addFragment(ArrivalFragmentTab.newInstance(mContext, "science"))
        mViewPagerAdapter.addFragment(ArrivalFragmentTab.newInstance(mContext, "frontgate"))
        mViewPagerAdapter.addFragment(SchoolBusFragment.newInstance(mContext))
        // ViewPager 설정
        vp_fragment_arrival_tabs.offscreenPageLimit = 4
        vp_fragment_arrival_tabs.adapter = mViewPagerAdapter
        vp_fragment_arrival_tabs.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(fragment_arrival_tablayout))
        vp_fragment_arrival_tabs.currentItem = 0
        setTabIcon(fragment_arrival_tablayout.getTabAt(0)!!, true)
        fragment_arrival_tablayout.addOnTabSelectedListener(mTabChangeListener)


        // 새로고침 이미지 회전 애니메이션 설정
        mFabRefreshAnimation = RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        mFabRefreshAnimation.duration = 1000
        mFabRefreshAnimation.repeatCount = 1
        mFabRefreshAnimation.interpolator = AccelerateInterpolator()


        mFabRefresh = ib_fragment_arrival_tabs_refresh
        mFabRefresh.setOnClickListener {
            mBroadcastManager.sendBroadcast(Intent(LocalIntent.ARRIVAL_DATA_REFRESH_REQUEST.value))
            fabRefreshAnimation(true)
        }

        (activity as MainActivity).startpopup(false)
    }

    // 탭 아이콘 선택 여부 변경하는 함수 아이콘, 타이틀은 checkbox 형태로 만들어 true/false를 주면 색이 변하도록 xml로 설정
    private fun setTabIcon(tab : TabLayout.Tab, selected : Boolean) {
        val icon = tab.customView!!.iv_tabicon
        val title = tab.customView!!.tv_tabicon
        icon.isChecked = selected
        title.isChecked = selected
    }

    // 모든 프래그먼트가 준비 완료되면 초기 데이터 요청 Broadcast 시작
    private val mBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when(intent.action){
                LocalIntent.NOTIFY_FRAGMENT_READY.value->{
                    mFragmentReadyCount++
                    Log.d(LOG_TAG, "$mFragmentReadyCount")

                    if(mFragmentReadyCount == 4){
                        mBroadcastManager.sendBroadcast(Intent(LocalIntent.FIRST_DATA_REQUEST.value))
                    }
                }
                LocalIntent.ARRIVAL_DATA_REFRESH_REQUEST.value -> fabRefreshAnimation(true)
            }
        }
    }
    // 새로 고침 애니메이션 작동
    private fun fabRefreshAnimation(isRefresh : Boolean){
        if(isRefresh){
            mFabRefresh?.startAnimation(mFabRefreshAnimation)
        }
        else {
            mFabRefresh?.animation?.repeatCount = 0
        }
    }

    // 탭 아이콘 상태 변경
    private val mTabChangeListener = object : TabLayout.OnTabSelectedListener{
        override fun onTabReselected(tab: TabLayout.Tab) {}

        override fun onTabUnselected(tab: TabLayout.Tab) {
            setTabIcon(tab, false)
        }

        override fun onTabSelected(tab: TabLayout.Tab) {
            vp_fragment_arrival_tabs.currentItem = tab.position
            setTabIcon(tab, true)
        }
    }

}