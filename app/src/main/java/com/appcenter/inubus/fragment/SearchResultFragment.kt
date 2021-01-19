package com.appcenter.inubus.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.appcenter.inubus.R
import com.appcenter.inubus.recycler.SearchResultAdapter
import kotlinx.android.synthetic.main.fragment_search_recycler.*
import kotlinx.android.synthetic.main.fragment_swipepull_recycler.*

/**
 * Created by ByoungMean on 2019-09-19.
 */

class SearchResultFragment : androidx.fragment.app.Fragment() {

    private lateinit var mFm: androidx.fragment.app.FragmentManager
    private lateinit var mContext : Context
    lateinit var mAdapter : SearchResultAdapter

    companion object {
        fun newInstance(fm : androidx.fragment.app.FragmentManager, context : Context) : SearchResultFragment{
            val fragment = SearchResultFragment()
            fragment.mFm = fm
            fragment.mContext = context
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_search_recycler, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // swipeRefresh 기능 해제

        // 레이아웃 설정
        rv_fragment_search_recycler.layoutManager = LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, false)
        // 리사이클러뷰와 어댑터 연결
        mAdapter = SearchResultAdapter()
        rv_fragment_search_recycler.adapter = mAdapter
    }
}