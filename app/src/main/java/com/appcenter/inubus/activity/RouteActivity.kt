package com.appcenter.inubus.activity

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import com.appcenter.inubus.R
import com.appcenter.inubus.databinding.ActivityRouteBinding
import com.appcenter.inubus.model.BusInformation
import com.appcenter.inubus.recycler.RecyclerAdapterRoute
import com.appcenter.inubus.recycler.RecyclerAdapterRoute.Direction
import com.appcenter.inubus.recycler.RecyclerAdapterRoute.RouteType
import com.appcenter.inubus.util.Singleton
import kotlinx.android.synthetic.main.activity_route.*
import kotlin.math.roundToInt

/**
 * Created by Bunga on 2018-02-23.
 * Updated by ByoungMean on 2019-10-17.
 */

class RouteActivity : AppCompatActivity() {

        // Binding과 Recyclerview 객체 생성
    private lateinit var mBinding : ActivityRouteBinding
    private lateinit var mRvRoute : androidx.recyclerview.widget.RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_route)
        mBinding.listener = this
        mRvRoute = mBinding.rvRouteActivityRecycler

        // 상단 정보 설정
        var routeNo = intent.getStringExtra("routeNo")
        val routeInfo : BusInformation
        var lastNo = routeNo

        // 통학버스 일때
        if(routeNo.substring(0,1)=="R")
        {
            routeNo = routeNo.substring(1,3)
            lastNo = lastNo.substring(1)
            route_busno.textSize = 20F
            tv_route_start.text = "출발"
            tv_route_end.text = "도착"
            ll_route_cost.visibility = View.GONE

            val noLp = route_busno.layoutParams as (ConstraintLayout.LayoutParams)
            noLp.topMargin = px(5F)
            route_busno.requestLayout()
            route_busno.layoutParams = noLp

            val endLp = ll_route_end.layoutParams as (ConstraintLayout.LayoutParams)
            endLp.rightMargin = px(23F)
            endLp.endToEnd = ConstraintSet.PARENT_ID
            ll_route_end.requestLayout()
            ll_route_end.layoutParams = endLp
        }
        routeInfo = Singleton.busInfo.get()!![routeNo]!!

        mBinding.no = lastNo
        if(lastNo == "송내"){
            mBinding.startTime = "08:00 / 09:00"
            mBinding.endTime = "08:40 / 09:40"
        }
        else{
            mBinding.startTime = String.format("%02d:%02d", routeInfo.start/100, routeInfo.start%100)
            mBinding.endTime = String.format("%02d:%02d", routeInfo.end/100, routeInfo.end%100)
        }
        var fee : Int? = null

        changestatusBarColor()

        Singleton.busCost.forEach() { (no, cost) ->
            if(routeNo == no){
                fee = cost
            }
        }
        fee = fee?: Singleton.busCost["else"]
        mBinding.fee = "${fee}원"

        // 노선 정보 설정
        val routeList = routeInfo.nodeList
        val turnNode = routeInfo.turnNode
        val adapter = RecyclerAdapterRoute(mRvRoute)
        ViewCompat.setNestedScrollingEnabled(rv_route_activity_recycler, false);

        Singleton.schoolbusGPS.get()?.let{
            it.forEach { gpsData->
                if(gpsData.busTime.routeID.substring(0,2) == routeNo){
                    val location = gpsData.location
                    if(location != 0) adapter.location = 1
                }
            }
        }

        // 회차지가 없는경우
        if(turnNode == ""){
            routeList.forEachIndexed { index, s ->
                when {
                    index != routeList.lastIndex -> {
                        adapter.addStop(s.nodeName, Direction.NONE,RouteType.STOP)
                    }
                    else ->   adapter.addStop(s.nodeName, Direction.NONE,RouteType.STOP)
                }
            }
        }
        // 회차지가 있으면 동일한 String을 찾아가며 추가
        else {
            // TODO 시작 끝 구분 없어짐
            var nameList = arrayListOf<String>()
            for(i in 0 until routeList.size){
                nameList.add(routeList[i].nodeName)
            }
            val turnNodePosition = nameList.indexOf(turnNode)
            routeList.forEachIndexed { index, s ->
                // 시작
                when {
                    index == 0 ->{
                        adapter.addStop(s.nodeName, Direction.START,RouteType.STOP)
                    }
                    // 회차지
                    turnNodePosition - index == 0 -> {
                        adapter.addStop(s.nodeName, Direction.NONE,RouteType.STOP)
                        adapter.addReturn()
                        adapter.addStop(s.nodeName, Direction.NONE,RouteType.STOP)
                    }
                    // 끝
                    index == routeList.size -1 -> {
                        adapter.addStop(s.nodeName, Direction.END,RouteType.STOP)
                        if(lastNo.substring(0,1)!="R") adapter.addLine()
                    }
                    else -> adapter.addStop(s.nodeName, Direction.NONE,RouteType.STOP)
                }
            }
        }

        mRvRoute.adapter = adapter
    }

    private fun px(dpi : Float):Int{
        val dp = resources.displayMetrics.density
        return (dpi * dp).roundToInt()
    }

    private fun changestatusBarColor(){
        // 롤리팝 버전 이상부터 statusBar를 파란색, 아이콘을 밝은색으로 표시
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = ContextCompat.getColor(applicationContext,R.color.colorPrimary)
                window.decorView.systemUiVisibility = 0
        }
    }

    fun btnCloseClicked(){
        finish()
    }
}
