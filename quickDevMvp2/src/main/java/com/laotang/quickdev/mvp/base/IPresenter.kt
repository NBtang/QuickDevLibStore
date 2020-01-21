package com.laotang.quickdev.mvp.base

import android.app.Application
import org.kodein.di.KodeinAware

interface IPresenter<V:IView>: KodeinAware {
    fun attach(view:V)
    fun detach()
    fun getApplication():Application
}