package com.laotang.quickdev.mvp.base

import androidx.lifecycle.MutableLiveData

open class BaseViewDelegate : IView {
    val finishActivityLiveData by lazy {
        MutableLiveData<Boolean>()
    }

    val errorLiveData by lazy {
        MutableLiveData<ErrorReportBean>()
    }

    override fun finishActivity() {
        finishActivityLiveData.value = true
    }

    override fun onError(code: Int, msg: String) {
        errorLiveData.value = ErrorReportBean(code, msg)
    }

}