package com.appcenter.inubus.activity

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AutoCompleteTextView
import com.appcenter.inubus.R
import com.appcenter.inubus.fragment.SearchHistoryFragment
import com.appcenter.inubus.fragment.SearchResultFragment
import com.appcenter.inubus.model.DBSearchHistoryItem
import com.appcenter.inubus.recycler.ViewPagerAdapter
import com.appcenter.inubus.util.AppDatabase
import com.appcenter.inubus.util.Singleton
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
                    Singleton.hideKeyboard(this)
                    true
                }
                else -> {
                    false
                }
            }
        }

        ll_searchbar.setOnClickListener {
            actionbar_searchView.requestFocus()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
        }

        btn_search_back.setOnClickListener {
            if(activity_search_viewpager.currentItem == 1) activity_search_viewpager.currentItem = 0
            else{
                Singleton.hideKeyboard(this)
                finish()
            }
        }

        btn_actionbar_search.setOnClickListener {
            getResult()
            Singleton.hideKeyboard(this)
        }

        actionbar_searchView.requestFocus()
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)

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
