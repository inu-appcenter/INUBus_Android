package com.inu.bus

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter

class temp(internal var listener: EventListener) : BaseAdapter() {

    interface EventListener {
        fun onEvent(data: Int)
    }

    override fun getCount(): Int {
        return 0
    }

    override fun getItem(i: Int): Any? {
        return null
    }

    override fun getItemId(i: Int): Long {
        return 0
    }

    override fun getView(i: Int, view: View, viewGroup: ViewGroup): View? {
        return null
    }
}