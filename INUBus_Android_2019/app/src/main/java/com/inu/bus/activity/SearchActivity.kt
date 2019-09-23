package com.inu.bus.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.inu.bus.R
import com.inu.bus.fragment.ArrivalFragment
import com.inu.bus.fragment.SearchHistoryFragment
import com.inu.bus.fragment.SearchResultFragment
import com.inu.bus.model.DBSearchHistoryItem
import com.inu.bus.recycler.ViewPagerAdapter
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.custom_searchbar.*

class SearchActivity : AppCompatActivity() {

    private val mViewPagerAdapter by lazy { ViewPagerAdapter(supportFragmentManager, this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        btn_search_back.setOnClickListener {
            finish()
        }

        btn_actionbar_search.setOnClickListener {
            //
            (activity_search_viewpager.adapter as ViewPagerAdapter).fragments.forEach {
                if(it is SearchHistoryFragment){
                    var newSHitem = DBSearchHistoryItem()
                    newSHitem.name = actionbar_searchView.text.toString()
                    it.mAdapter.insertHistory(this,newSHitem)
                }
            }
            activity_search_viewpager.currentItem = 1
//
//            Log.d("1234","searchhistory insert")
//            Singleton.hideKeyboard(this)
        }

        setMainViewPager()
    }

    private fun setMainViewPager() {
        val arrivalFragment = ArrivalFragment.newInstance(supportFragmentManager, this)

//        // 도착, 목적지 Fragment 추가
        mViewPagerAdapter.addFragment(SearchHistoryFragment.newInstance(supportFragmentManager, this))
        mViewPagerAdapter.addFragment(SearchResultFragment.newInstance(supportFragmentManager, this))
//        mViewPagerAdapter.addFragment(arrivalFragment)
//
        // ViewPager 설정
        activity_search_viewpager.adapter = mViewPagerAdapter
        activity_search_viewpager.offscreenPageLimit = 2
        activity_search_viewpager.currentItem = 1
        activity_search_viewpager.setScrollDurationFactor(4.0)

    }
}
