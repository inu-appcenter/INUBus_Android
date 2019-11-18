package com.inu.bus.recycler

/**
 * Created by Bunga on 2018-01-29.
 */

import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import com.inu.bus.R
import com.inu.bus.databinding.RecyclerRouteItemBinding
import java.util.*
import kotlin.math.roundToInt

class RecyclerAdapterRoute(private val mRvRoute : RecyclerView) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    enum class RouteType{
        STOP, LINE, RETURN
    //  정류소  스크롤  회차지
    }

    // direction
    enum class Direction{
        NONE, START, END
    //  양 쪽   위쪽   아래쪽    파란선 invisible
    }

    var location = 0
    private val mDataSet = ArrayList<CustomItem>()

    override fun getItemViewType(position: Int) : Int = mDataSet[position].type.ordinal

    inner class CustomItem(val stopName: String, val direction: Direction, val type: RouteType) {
        constructor( type : RouteType) : this("", Direction.NONE, type)
    }

    // 정류장 표시용 뷰홀더
    class StopHolder(private val binding : RecyclerRouteItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item : CustomItem){
            binding.item = item
        }
    }

    // 회차지 뷰홀더
    class ReturnHolder(v: ConstraintLayout) : RecyclerView.ViewHolder(v)

    // 중간 라인 뷰홀더
    class LineHolder(v: ConstraintLayout) : RecyclerView.ViewHolder(v)

    fun addStop(stopName: String, direction: Direction, type: RouteType) {
        mDataSet.add(CustomItem(stopName, direction, type))
    }

    fun addReturn() {
        mDataSet.add(CustomItem(RouteType.RETURN))
    }

    fun addLine(){
        mDataSet.add(CustomItem(RouteType.LINE))
    }

    // RouteType별 xml 뷰홀더 객체 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v: ConstraintLayout
        return when(RouteType.values()[viewType]){
            RouteType.STOP-> StopHolder(RecyclerRouteItemBinding.inflate(LayoutInflater.from(parent.context)))
            RouteType.LINE -> {
                v = LayoutInflater.from(parent.context)
                        .inflate(R.layout.recycler_route_end, parent, false) as ConstraintLayout
                LineHolder(v)
            }
            RouteType.RETURN -> {
                v = LayoutInflater.from(parent.context)
                        .inflate(R.layout.recycler_route_return, parent, false) as ConstraintLayout
                ReturnHolder(v)
            }
        }
    }
    
    // STOP은 recycler_arrival_item / TextView 데이터베인딩
    // LINE은 recycler_arrival_end  / ImageButton 클릭리스너 추가
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = mDataSet[position]
        if (item.type == RouteType.STOP) {
            (holder as StopHolder).bind(item)
            val icBus = holder.itemView.findViewById<ImageView>(R.id.ic_route_bus)

            if((location != 0) && (position == (location + 1) / 2 - 1)){
                icBus.visibility = View.VISIBLE
                val lp = icBus.layoutParams as (ConstraintLayout.LayoutParams)
                if(location % 2 == 0)
                    lp.setMargins(px(54F), px(41.3F), 0, 0)
                else
                    lp.setMargins(px(54F), 0, 0, 0)
                icBus.layoutParams = lp
            }
        }
        else if (item.type == RouteType.LINE) {
            val btnScrollup = (holder as LineHolder).itemView.findViewById<ImageButton>(R.id.btn_route_end)

            val llm = mRvRoute.layoutManager
            btnScrollup.setOnClickListener {
                llm.smoothScrollToPosition(mRvRoute,null,0)
            }
        }
    }

    // 아이템 개수 리턴
    override fun getItemCount(): Int = mDataSet.size

    private fun px(dpi : Float):Int{
        val dp = mRvRoute.resources.displayMetrics.density
        return (dpi * dp).roundToInt()
    }
}