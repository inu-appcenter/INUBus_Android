package com.inu.bus.activity

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.widget.AutoCompleteTextView
import com.inu.bus.R
import com.inu.bus.fragment.ArrivalFragment
import com.inu.bus.fragment.SearchHistoryFragment
import com.inu.bus.fragment.SearchResultFragment
import com.inu.bus.model.DBSearchHistoryItem
import com.inu.bus.recycler.SearchResultAdapter
import com.inu.bus.recycler.ViewPagerAdapter
import com.inu.bus.util.AppDatabase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.custom_searchbar.*
import java.lang.ref.WeakReference

class SearchActivity : AppCompatActivity() {

    companion object {
        lateinit var mWrSearchView : WeakReference<AutoCompleteTextView>
    }

    private val mViewPagerAdapter by lazy { ViewPagerAdapter(supportFragmentManager, this) }
    var mHistoryList = arrayListOf<DBSearchHistoryItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        mWrSearchView = WeakReference(actionbar_searchView)
//        mWrSearchView.get()?.addTextChangedListener(mSearchTextWatcher)

        btn_search_back.setOnClickListener {
            if(activity_search_viewpager.currentItem == 1) activity_search_viewpager.currentItem = 0
            else finish()
        }

        btn_actionbar_search.setOnClickListener {
            activity_search_viewpager.currentItem = 1
            (activity_search_viewpager.adapter as ViewPagerAdapter).fragments.forEach {
                if(it is SearchResultFragment){
                    it.mAdapter.filter(actionbar_searchView.text.toString())
                }
            }
        }
        getHistory(this)
        setMainViewPager()
    }

    private fun setMainViewPager() {
//        val arrivalFragment = ArrivalFragment.newInstance(supportFragmentManager, this)

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

    private val mSearchTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if(s.isNullOrEmpty())
                activity_search_viewpager.currentItem = 0
            else {
                activity_search_viewpager.currentItem = 1
                (activity_search_viewpager.adapter as ViewPagerAdapter).fragments.forEach {
                    if(it is SearchResultFragment){
                        it.mAdapter.filter(s.toString())
                    }
                }
            }
        }
    }


    fun getHistory(context : Context){
        val mDB = AppDatabase.getInstance(context)!!
        val r = Runnable {
            try {
                mHistoryList = mDB.searchhistoryDAO()?.getAll() as ArrayList
            } catch (e:Exception){

            }
        }
        val thread = Thread(r)
        thread.start()
    }

    fun insertHistory(newSHitem : DBSearchHistoryItem){
        var overlap = false

        // overlap test
        mHistoryList.forEach {
            if(it.name == newSHitem.name) overlap = true
        }
        if(!overlap){
            (activity_search_viewpager.adapter as ViewPagerAdapter).fragments.forEach {
                if(it is SearchHistoryFragment){
                    it.mAdapter.insertHistory(this,newSHitem)
                    getHistory(this)
                }
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
