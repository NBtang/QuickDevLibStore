package com.laotang.quickdev.mvvm

import android.os.Bundle
import android.view.View
import org.kodein.di.KodeinAware

interface IFragment: KodeinAware {
    fun initParam(savedInstanceState: Bundle?)
    fun initView(savedInstanceState: Bundle?, contentView: View?)
    fun initViewObservable(savedInstanceState: Bundle?, contentView: View?)
    fun layout():Int
}