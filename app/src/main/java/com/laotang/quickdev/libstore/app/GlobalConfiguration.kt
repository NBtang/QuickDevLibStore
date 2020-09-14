package com.laotang.quickdev.libstore.app

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.laotang.component.core.ComponentConfig
import com.laotang.quickdev.libstore.app.factory.CustomViewModelFactory
import com.laotang.quickdev.libstore.app.factory.JsonConverterFactory
import com.laotang.quickdev.libstore.mvvm.model.repository.UserRepository
import com.laotang.quickdev.libstore.mvvm.model.service.UserService
import com.laotang.quickdev.logcat.LogcatLogger
import com.laotang.quickdevcore.base.delegate.AppLifecycle
import com.laotang.quickdevcore.di.module.AppModule
import com.laotang.quickdevcore.di.module.ClientModule
import com.laotang.quickdevcore.di.module.GlobalConfigModule
import com.laotang.quickdevcore.integration.ConfigModule
import com.laotang.quickdevcore.integration.IRepositoryManager
import com.laotang.quickdevcore.integration.http.HttpLoggingInterceptor
import com.laotang.quickdevcore.integration.manifest.ManifestDynamicAdapter
import com.laotang.quickdevcore.utils.rootKodein
import okhttp3.OkHttpClient
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton
import org.kodein.di.weakReference
import retrofit2.Retrofit
import timber.log.Timber
import java.util.concurrent.TimeUnit

class GlobalConfiguration : ConfigModule {
    override fun applyOptions(context: Context, builder: GlobalConfigModule.Builder) {
        builder.baseUrl("https://api.github.com/")
            .addGsonConfiguration(object : AppModule.GsonConfiguration {
                override fun configGson(context: Context, gsonBuilder: GsonBuilder) {
                    gsonBuilder
                        .serializeNulls()
                        .enableComplexMapKeySerialization()//支持将序列化key为object的map,默认只能序列化key为string的map
                }
            })
            .addOkHttpConfiguration(object : ClientModule.OkHttpConfiguration {
                override fun configOkHttp(context: Context, builder: OkHttpClient.Builder) {
                    builder.apply {
                        connectTimeout(10, TimeUnit.SECONDS)
                        addInterceptor(HttpLoggingInterceptor(HttpLoggingInterceptor.Logger { url, message ->
                            Timber.d(message)
                        }).setLevel(HttpLoggingInterceptor.Level.BODY))
                    }
                }
            })
            .addRetrofitConfiguration(object : ClientModule.RetrofitConfiguration {
                override fun configRetrofit(context: Context, builder: Retrofit.Builder) {
                    val gson by rootKodein().instance<Gson>()
                    builder.addConverterFactory(JsonConverterFactory.create(gson))//自定义JsonConverter，为加密解密预留入口
                }
            })
    }

    override fun injectAppLifecycle(context: Context, lifecycles: ArrayList<AppLifecycle>) {

    }

    override fun injectActivityLifecycle(
        context: Context,
        lifecycles: ArrayList<Application.ActivityLifecycleCallbacks>
    ) {
        lifecycles.add(ManifestDynamicAdapter())
        lifecycles.add(LogcatLogger(context))
    }

    override fun injectKoDeinModule(context: Context, kodeinModules: ArrayList<Kodein.Module>) {
        kodeinModules.add(Kodein.Module("ComponentConfigModule") {
            bind<ComponentConfig.Builder>() with provider {
                ComponentConfig.Builder().enableDoraemonKit(false)
            }
            bind<UserRepository>() with singleton(ref = weakReference) {
                UserRepository(
                    instance<IRepositoryManager>().obtainRetrofitService(UserService::class.java)
                )
            }
            bind<ViewModelProvider.Factory>() with singleton {
                CustomViewModelFactory(instance())
            }
        })
    }

    override val priority: Int
        get() = ConfigModule.APP_MODULE
}