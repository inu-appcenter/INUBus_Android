package com.inu.bus.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.inu.bus.R
import com.inu.bus.databinding.RecyclerDestinationItemBinding
import com.inu.bus.model.BusInformation
import com.inu.bus.recycler.DestinationRecyclerViewHolder
import com.inu.bus.recycler.RecyclerAdapterDestination
import com.inu.bus.recycler.SearchHistoryAdapter
import com.inu.bus.recycler.SearchResultAdapter
import kotlinx.android.synthetic.main.fragment_swipepull_recycler.*

/**
 * Created by ByoungMean on 2019-09-19.
 */

class SearchResultFragment : Fragment() {

    private lateinit var mFm: FragmentManager
    private lateinit var mContext : Context
    private val mBroadcastManager by lazy { LocalBroadcastManager.getInstance(mContext) }
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

    fun filter(str : String) {

        var filtered :ArrayList<BusInformation>

        filtered =
                // 검색 취소
                if(str == ""){
                    mAdapter.mSearchList
                }
                else {
                    ArrayList(
                            mAdapter.mSearchList.filter { item ->

                                item.no.contains(str)
//                                    !Singleton.busInfo.get()!![item.arrivalInfo!!.no]
//                                            ?.nodeList
//                                            ?.find{
//                                                Log.d("0598","it -> ${it}")
//                                                it.contains(str)
//                                            }.isNullOrEmpty()
                            }
                    )
                }

        // 도착 정보를 비교해서 업데이트
//        mAdapter.mFilteredList = filtered
        mAdapter.notifyDataSetChanged()
    }
}