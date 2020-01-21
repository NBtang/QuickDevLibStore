package com.laotang.quickdev.mvp.base

import android.content.Intent
import android.os.Bundle
import org.kodein.di.KodeinAware

interface IActivity: KodeinAware {
    fun initParam(savedInstanceState: Bundle?)
    fun initView(savedInstanceState: Bundle?)
    fun initViewObservable(savedInstanceState: Bundle?)
    fun initPresenter(savedInstanceState: Bundle?,intent: Intent)
    fun layout():Int
}