package com.laotang.quickdev.mvp

import android.app.Application
import android.content.Context
import com.laotang.quickdevcore.base.delegate.AppLifecycle
import com.laotang.quickdevcore.di.module.GlobalConfigModule
import com.laotang.quickdevcore.integration.ConfigModule
import org.kodein.di.Kodein

class MVPConfigModule: ConfigModule {
    override val priority: Int = ConfigModule.LIB_MODULE

    override fun applyOptions(context: Context, builder: GlobalConfigModule.Builder) {
    }

    override fun injectActivityLifecycle(context: Context, lifecycles: ArrayList<Application.ActivityLifecycleCallbacks>) {
        lifecycles.add(ActivityLifecycleImpl())
    }

    override fun injectAppLifecycle(context: Context, lifecycles: ArrayList<AppLifecycle>) {

    }

    override fun injectKoDeinModule(context: Context, kodeinModules: ArrayList<Kodein.Module>) {
    }
}