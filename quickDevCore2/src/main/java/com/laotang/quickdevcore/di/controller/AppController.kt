package com.laotang.quickdevcore.di.controller

import android.app.Application
import android.content.Context
import com.laotang.quickdevcore.di.module.AppModule
import com.laotang.quickdevcore.di.module.ClientModule
import com.laotang.quickdevcore.di.module.GlobalConfigModule
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.androidModule
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.kcontext
import org.kodein.di.generic.singleton

class AppController(application: Application, globalConfigModule: GlobalConfigModule, kodeinModules: ArrayList<Kodein.Module>) : KodeinAware {
    override val kodein: Kodein = Kodein.lazy {
        bind<Context>() with singleton { application}
        bind<GlobalConfigModule>() with singleton { globalConfigModule }
        import(androidModule(application))
        import(androidXModule(application))
        import(AppModule.appModule)
        import(ClientModule.clientModule)
        kodeinModules.forEach {
            import(it)
        }
    }
    override val kodeinContext = kcontext(application)

}

