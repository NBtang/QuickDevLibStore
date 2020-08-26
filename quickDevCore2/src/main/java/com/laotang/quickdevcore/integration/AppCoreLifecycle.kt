package com.laotang.quickdevcore.integration

import android.app.Application
import android.content.res.Configuration
import com.laotang.quickdevcore.base.delegate.AppLifecycle
import com.laotang.quickdevcore.integration.http.utl.DefaultUrlParserEx
import es.dmoral.toasty.Toasty
import me.jessyan.retrofiturlmanager.RetrofitUrlManager

class AppCoreLifecycle: AppLifecycle {

    override fun onCreate(application: Application) {
        DefaultUrlParserEx().apply {
            this.init(RetrofitUrlManager.getInstance())
            RetrofitUrlManager.getInstance().setUrlParser(this)
        }
        Toasty.Config.getInstance().allowQueue(false).apply()
    }

    override fun onLowMemory() {
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
    }

    override fun onTrimMemory(level: Int) {
    }
}