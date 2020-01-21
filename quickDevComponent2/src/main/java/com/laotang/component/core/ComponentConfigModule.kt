package com.laotang.component.core

import android.app.Application
import android.content.Context
import com.laotang.component.di.componentModule
import com.laotang.quickdevcore.base.delegate.AppLifecycle
import com.laotang.quickdevcore.di.module.GlobalConfigModule
import com.laotang.quickdevcore.integration.ConfigModule
import org.kodein.di.Kodein

class ComponentConfigModule: ConfigModule {
    override fun applyOptions(context: Context, builder: GlobalConfigModule.Builder) {
    }

    override fun injectAppLifecycle(context: Context, lifecycles: ArrayList<AppLifecycle>) {
        lifecycles.add(AppLifecycleImpl())
    }

    override fun injectActivityLifecycle(
        context: Context,
        lifecycles: ArrayList<Application.ActivityLifecycleCallbacks>
    ) {
        lifecycles.add(ActivityLifecycleCallbacksImpl())
    }

    override fun injectKoDeinModule(context: Context, kodeinModules: ArrayList<Kodein.Module>) {
        kodeinModules.add(componentModule)
    }

    override val priority= Int.MIN_VALUE
}