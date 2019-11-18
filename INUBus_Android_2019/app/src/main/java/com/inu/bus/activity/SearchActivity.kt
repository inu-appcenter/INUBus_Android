package com.inu.bus.activity

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.inputmethod.EditorInfo
import android.widget.AutoCompleteTextView
import com.inu.bus.R
import com.inu.bus.fragment.SearchHistoryFragment
import com.inu.bus.fragment.SearchResultFragment
import com.inu.bus.model.DBSearchHistoryItem
import com.inu.bus.recycler.ViewPagerAdapter
import com.inu.bus.util.AppDatabase
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.custom_searchbar.*
import java.lang.ref.WeakReference

/**
 * Created by ByoungMean on 2019-10-12.
 */

class SearchActivity : AppCompatActivity() {

    companion object {
        lateinit var mWrSearchView : WeakReference<AutoCompleteTextView>
    }

    private val mViewPagerAdapter by lazy { ViewPagerAdapter(supportFragmentManager, this) }
    private var mHistoryList = arrayListOf<DBSearchHistoryItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        mWrSearchView = WeakReference(actionbar_searchView)

        actionbar_searchView.setOnEditorActionListener { textView, actionID, keyEvent ->
            when(actionID){
                EditorInfo.IME_ACTION_SEARCH ->{
                    getResult()
                    true
                }
                else -> {
                    false
                }
            }
        }


        btn_search_back.setOnClickListener {
            if(activity_search_viewpager.currentItem == 1) activity_search_viewpager.currentItem = 0
            else finish()
        }

        btn_actionbar_search.setOnClickListener {
            getResult()
        }

        getHistory(this)
        setMainViewPager()
    }

    private fun setMainViewPager() {
//        // 도착, 목적지 Fragment 추가
        mViewPagerAdapter.addFragment(SearchHistoryFragment.newInstance(supportFragmentManager, this))
        mViewPagerAdapter.addFragment(SearchResultFragment.newInstance(supportFragmentManager, this))
//        mViewPagerAdapter.addFragment(arrivalFragment)

        // ViewPager 설정
        activity_search_viewpager.adapter = mViewPagerAdapter
        activity_search_viewpager.offscreenPageLimit = 2
        activity_search_viewpager.currentItem = 0
        activity_search_viewpager.setScrollDurationFactor(4.0)
    }


    private fun getHistory(context : Context){
        val mDB = AppDatabase.getInstance(context)!!
        val r = Runnable {
            mHistoryList = mDB.searchhistoryDAO()?.getAll() as ArrayList
        }
        val thread = Thread(r)
        thread.start()
    }

    fun insertHistory(newSHitem : DBSearchHistoryItem){
        (activity_search_viewpager.adapter as ViewPagerAdapter).fragments.forEach {
            if(it is SearchHistoryFragment){
                it.mAdapter.insertHistory(this,newSHitem)
                getHistory(this)
            }
        }
    }

    private fun getResult(){
        activity_search_viewpager.currentItem = 1
        (activity_search_viewpager.adapter as ViewPagerAdapter).fragments.forEach {
            if(it is SearchResultFragment){
                it.mAdapter.filter(actionbar_searchView.text.toString())
            }
        }
    }

    override fun onBackPressed() {
        when {
            // 검색창이 비어있지 않으면 비움
            actionbar_searchView.text.toString() != "" -> actionbar_searchView.text.clear()

            else -> {
                super.onBackPressed()
            }
        }
    }
}
