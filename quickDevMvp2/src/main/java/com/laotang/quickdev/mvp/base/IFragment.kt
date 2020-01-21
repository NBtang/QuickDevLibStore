package com.laotang.quickdev.mvp.base

import android.os.Bundle
import org.kodein.di.KodeinAware

interface IFragment: KodeinAware {
    fun initParam(savedInstanceState: Bundle?)
    fun initView(savedInstanceState: Bundle?)
    fun initViewObservable(savedInstanceState: Bundle?)
    fun initPresenter(savedInstanceState: Bundle?,arg: Bundle?)
    fun layout():Int
}