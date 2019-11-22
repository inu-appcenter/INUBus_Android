package com.appcenter.inubus.model

import android.databinding.BaseObservable
import android.databinding.Bindable
import android.databinding.ObservableBoolean
import android.os.Build
import com.appcenter.inubus.BR
import com.appcenter.inubus.util.Singleton

/**
 * Created by Minjae Son on 2018-08-11.
 */

// 문의 내용을 담을 객체
data class InquireModel(
//        val device : String =  "${Build.MODEL} : ${Build.VERSION.RELEASE} (${Build.VERSION.SDK_INT})",
        val device : String =  "${Build.MODEL}",
        val service : String = Singleton.myPackageName) : BaseObservable(){

    @Bindable
    var title = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.title)
        }
    @Bindable
    var contact = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.contact)
        }
    @Bindable
    var message = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.message)
        }

    var enabled  : ObservableBoolean  = ObservableBoolean(false)
}