package com.laotang.quickdev.mvp.base

import android.app.Application
import androidx.lifecycle.LifecycleOwner
import org.kodein.di.KodeinAware

interface IPresenter<V:IView>: KodeinAware {
    fun attach(view:V)
    fun detach()
    fun getView(): V?
    fun bindLifecycle(owner: LifecycleOwner)
    fun getApplication():Application
}