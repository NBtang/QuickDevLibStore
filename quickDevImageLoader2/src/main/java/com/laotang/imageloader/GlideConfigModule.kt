package com.laotang.imageloader

import android.app.Application
import android.content.Context
import com.laotang.quickdevcore.base.delegate.AppLifecycle
import com.laotang.quickdevcore.di.module.GlobalConfigModule
import com.laotang.quickdevcore.integration.ConfigModule
import org.kodein.di.Kodein

class GlideConfigModule: ConfigModule {
    override fun applyOptions(context: Context, builder: GlobalConfigModule.Builder) {

    }

    override fun injectAppLifecycle(context: Context, lifecycles: ArrayList<AppLifecycle>) {
    }

    override fun injectActivityLifecycle(context: Context, lifecycles: ArrayList<Application.ActivityLifecycleCallbacks>) {
    }

    override fun injectKoDeinModule(context: Context, kodeinModules: ArrayList<Kodein.Module>) {
        kodeinModules.add(glideKodeinModule)
    }

    override val priority = Int.MAX_VALUE
}