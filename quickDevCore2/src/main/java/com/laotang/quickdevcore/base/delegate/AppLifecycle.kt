package com.laotang.quickdevcore.base.delegate

import android.app.Application
import android.content.ComponentCallbacks2

/**
 * ================================================
 * 用于代理 [Application] 的生命周期
 * ================================================
 */
interface AppLifecycle : ComponentCallbacks2 {
    fun onCreate(application: Application)
}
