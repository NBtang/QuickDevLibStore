package com.laotang.quickdev.aac

import android.os.Bundle
import android.view.View
import org.kodein.di.KodeinAware

interface IView: KodeinAware {
    fun initParam(savedInstanceState: Bundle?)
    fun initView(savedInstanceState: Bundle?)
    fun initViewObservable(savedInstanceState: Bundle?)
    fun layout():Int
}