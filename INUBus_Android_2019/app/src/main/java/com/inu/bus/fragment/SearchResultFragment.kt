package com.inu.bus.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.inu.bus.R
import com.inu.bus.recycler.SearchResultAdapter
import kotlinx.android.synthetic.main.fragment_swipepull_recycler.*

/**
 * Created by ByoungMean on 2019-09-19.
 */

class SearchResultFragment : Fragment() {

    private lateinit var mFm: FragmentManager
    private lateinit var mContext : Context
    lateinit var mAdapter : SearchResultAdapter

    companion object {
        fun newInstance(fm : FragmentManager, context : Context) : SearchResultFragment{
            val fragment = SearchResultFragment()
            fragment.mFm = fm
            fragment.mContext = context
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_swipepull_recycler, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // swipeRefresh 기능 해제
        fragment_node_arrival_swipeRefreshLayout.isEnabled = false
        fragment_node_arrival_swipeRefreshLayout.isRefreshing = false

        // 레이아웃 설정
        rv_fragment_node_arrival_recycler.layoutManager = LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, false)
        // 리사이클러뷰와 어댑터 연결
        mAdapter = SearchResultAdapter()
        rv_fragment_node_arrival_recycler.adapter = mAdapter
    }
}