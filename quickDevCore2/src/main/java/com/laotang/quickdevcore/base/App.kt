package com.laotang.quickdevcore.base

import com.laotang.quickdevcore.di.controller.AppController


interface App {
    fun getAppController(): AppController
}