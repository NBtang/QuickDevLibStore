package com.laotang.component.core

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import com.alibaba.android.arouter.launcher.ARouter
import com.didichuxing.doraemonkit.DoraemonKit
import com.laotang.component.utils.isApkInDebug
import com.laotang.quickdevcore.base.delegate.AppLifecycle
import com.laotang.quickdevcore.integration.cache.Cache
import com.laotang.quickdevcore.integration.cache.IntelligentCache
import com.laotang.quickdevcore.utils.rootKodein
import com.squareup.leakcanary.LeakCanary
import com.squareup.leakcanary.RefWatcher
import org.kodein.di.generic.instance

class AppLifecycleImpl : AppLifecycle {

    override fun onCreate(application: Application) {
        if (LeakCanary.isInAnalyzerProcess(application)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }

        if (application.isApkInDebug()) {           // 这两行必须写在init之前，否则这些配置在init过程中将无效
            ARouter.openLog()    // 打印日志
            ARouter.openDebug()  // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        }
        ARouter.init(application) // 尽可能早，推荐在Application中初始化
        val componentConfig by rootKodein().instance<ComponentConfig>()
        val enableLeakCanary = componentConfig.enableLeakCanary()
        val enableDoraemonKit = componentConfig.enableDoraemonKit()

        val mRefWatcher =
            if (enableLeakCanary) LeakCanary.install(application) else RefWatcher.DISABLED
        val cache: Cache<String, Any> by rootKodein().instance()
        cache.put(IntelligentCache.getKeyOfKeep(RefWatcher::class.java.name), mRefWatcher as Any)

        if (enableDoraemonKit)
            DoraemonKit.install(application)
    }

    override fun onLowMemory() {
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
    }

    override fun onTrimMemory(level: Int) {
    }
}