package com.laotang.quickdev.mvp.base


interface IView {
    fun onError(code: Int, msg: String)
    fun finishActivity()
}