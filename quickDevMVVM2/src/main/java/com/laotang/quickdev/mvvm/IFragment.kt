package com.laotang.quickdev.mvvm

import android.os.Bundle
import org.kodein.di.KodeinAware

interface IFragment: KodeinAware {
    fun initParam(savedInstanceState: Bundle?)
    fun initView(savedInstanceState: Bundle?)
    fun initViewObservable(savedInstanceState: Bundle?)
    fun layout():Int
}