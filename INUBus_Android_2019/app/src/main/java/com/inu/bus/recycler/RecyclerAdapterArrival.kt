package com.inu.bus.recycler

import android.databinding.ObservableBoolean
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.inu.bus.databinding.RecyclerArrivalHeaderBinding
import com.inu.bus.databinding.RecyclerArrivalItemBinding
import com.inu.bus.databinding.RecyclerArrivalSeparatorBinding
import com.inu.bus.model.BusArrivalInfo
import com.inu.bus.model.DBBusFavoriteItem
import com.inu.bus.model.RecyclerArrivalItem
import com.inu.bus.util.AppDatabase
import com.inu.bus.util.ArrivalInfoDiffUtil


/**
 * Created by Minjae Son on 2018-08-07.
 */

class RecyclerAdapterArrival(val mStrBusStop : String) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    var isShowing = ObservableBoolean(false)
    val mArrivalItems = ArrayList<RecyclerArrivalItem>()
    private var mFilteredItems = ArrayList<RecyclerArrivalItem>()
    private var mFilteringString = ""

    private lateinit var mDB: AppDatabase
    private var dataSet = arrayListOf<DBBusFavoriteItem>()
    var tempList = arrayListOf<BusArrivalInfo>()

    init {
        // Header
        mArrivalItems.add(RecyclerArrivalItem())
    }


    // ItemType에 따라 생성할 뷰홀더 객체 선택
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val type = RecyclerArrivalItem.ItemType.findByOrdinal(viewType)
        val layoutInflater = LayoutInflater.from(parent.context)

        mDB = AppDatabase.getInstance(parent.context)!!
        val r = Runnable {
            try {
//                Log.d("0598","group Success")
                dataSet = mDB?.busfavoriteDAO()?.getAll()!! as ArrayList

            } catch (e:Exception){
                Log.d("05981","Error - $e")
            }
        }
        val thread = Thread(r)
        thread.start()


        return when(type){
            RecyclerArrivalItem.ItemType.Header->
                ViewHolderArrivalHeader(RecyclerArrivalHeaderBinding.inflate(layoutInflater, parent, false))
            RecyclerArrivalItem.ItemType.SectionHeader->
                ViewHolderArrivalSection(RecyclerArrivalSeparatorBinding.inflate(layoutInflater, parent, false))
            RecyclerArrivalItem.ItemType.ArrivalInfo->
                ViewHolderArrivalItem(RecyclerArrivalItemBinding.inflate(layoutInflater, parent, false), isShowing,this)
        }
    }
    // ItemType에 따라 각 뷰 홀더에 데이터 연결
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when(mFilteredItems[position].itemType){
            RecyclerArrivalItem.ItemType.Header->{ }
            RecyclerArrivalItem.ItemType.SectionHeader->{
                (holder as ViewHolderArrivalSection).bind(mFilteredItems[position].sectionHeader!!)
            }
            RecyclerArrivalItem.ItemType.ArrivalInfo->{
                (holder as ViewHolderArrivalItem).bind(mFilteredItems[position].arrivalInfo!!,this)
            }
        }
    }
    // 아이템 개수 리턴
    override fun getItemCount(): Int = mFilteredItems.size
    // 아이템 타입 리턴
    override fun getItemViewType(position: Int): Int = mFilteredItems[position].itemType.ordinal



    // RecyclerView item position setting
    fun applyDataSet(items: ArrayList<BusArrivalInfo>,favList : ArrayList<String?>) {

        var favfirst = false

        mArrivalItems.clear()
        tempList = items
        // 버스 순 정렬
        val sorted = items.sortedWith(Comparator { o1, o2 ->
            when{
                (o1.type != o2.type) -> o1.type!!.ordinal - o2.type!!.ordinal
                else -> o1.no.compareTo(o2.no)
            }
        })

        dataSet.sortedWith(kotlin.Comparator { o1, o2 ->
            o2.no.compareTo(o1.no)
        })

        val grouped  = sorted.groupBy { it.type }
        var count = 1
        if(favList.isNotEmpty()) {
            mArrivalItems.add(RecyclerArrivalItem("즐겨찾기"))
            favList.sortedWith(Comparator {o1, o2 ->
                o2!!.compareTo(o1!!)
            })
        }

        grouped.forEach { group ->
            // 현재 필요한 섹션 헤더만 추가
            mArrivalItems.add(RecyclerArrivalItem(group.key!!.value))
            group.value.forEach {


                for(i in 0 until favList.size){
                    if (it.no == favList[i]) {
                        mArrivalItems.add(count, RecyclerArrivalItem(it))
                        count++
                    }
                }

                it.intervalString = "${it.interval}분"
                mArrivalItems.add(RecyclerArrivalItem(it))

            }
        }
        mArrivalItems.add(0,RecyclerArrivalItem())

        filter()
    }

    fun filter(str : String = mFilteringString) {

        var filtered :ArrayList<RecyclerArrivalItem>

        filtered =
                // 검색 취소
                if(str == ""){
                    mArrivalItems
                }
                else {
                    ArrayList(
                            mArrivalItems.filter { item ->
                                if (item.itemType == RecyclerArrivalItem.ItemType.ArrivalInfo){
                                    item.arrivalInfo!!.no.contains(str)
//                                    !Singleton.busInfo.get()!![item.arrivalInfo!!.no]
//                                            ?.nodeList
//                                            ?.find{
//                                                Log.d("0598","it -> ${it}")
//                                                it.contains(str)
//                                            }.isNullOrEmpty()
                                }
                                else true
                            }
                    )
                }

        // 도착 정보를 비교해서 업데이트
        Log.d("0598","filter on")
        val diffUtil = ArrivalInfoDiffUtil(mFilteredItems, filtered)
        val result = DiffUtil.calculateDiff(diffUtil)
        mFilteredItems.clear()
        mFilteredItems.addAll(filtered)
        result.dispatchUpdatesTo(this)
    }




    fun onCheck(data:BusArrivalInfo){



    }

    // 아이템이 화면에 보일때만 Ticker가 작동하도록 설정
    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)
        if(holder is ViewHolderArrivalItem){
            if(isShowing.get()){
                holder.startTick()
            }
        }
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        if(holder is ViewHolderArrivalItem){
            holder.stopTick()
        }
    }
}