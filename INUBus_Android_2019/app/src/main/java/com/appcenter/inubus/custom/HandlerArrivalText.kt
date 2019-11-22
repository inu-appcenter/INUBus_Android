package com.appcenter.inubus.custom

import android.os.Handler
import android.os.Message
import android.widget.TextView

/**
 * Created by Minjae Son on 2018-08-25.
 */

class HandlerArrivalText(val textView : TextView) : Handler(){

    // 메세지를 받아서 TextView에 전달
    override fun handleMessage(msg: Message?) {
        super.handleMessage(msg)
        textView.text = "${msg!!.obj}"
    }
}