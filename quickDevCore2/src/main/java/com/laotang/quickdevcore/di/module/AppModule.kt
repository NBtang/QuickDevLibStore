package com.laotang.quickdevcore.di.module

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.f2prateek.rx.preferences2.RxSharedPreferences
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.laotang.quickdevcore.integration.IRepositoryManager
import com.laotang.quickdevcore.integration.RepositoryManager
import com.laotang.quickdevcore.integration.cache.Cache
import com.laotang.quickdevcore.integration.cache.CacheType
import com.laotang.quickdevcore.integration.http.utl.IRetrofitUrlManagerService
import com.laotang.quickdevcore.integration.http.response.ResponseTransformerStrategy
import com.laotang.quickdevcore.integration.http.utl.RetrofitUrlManagerServiceImpl
import com.laotang.quickdevcore.integration.imageloader.ImageConfig
import com.laotang.quickdevcore.integration.imageloader.ImageLoader
import com.laotang.quickdevcore.integration.imageloader.ImageLoaderStrategy
import com.laotang.quickdevcore.integration.rxsubscriber.RxProgressObservable
import com.laotang.quickdevcore.utils.ACache
import org.kodein.di.Kodein
import org.kodein.di.generic.*
import java.io.File

class AppModule {

    companion object {
        private const val APP_MODULE_TAG = "appModule"
        val appModule by lazy {
            Kodein.Module(APP_MODULE_TAG) {
                bind<GsonBuilder>() with provider { GsonBuilder() }

                bind<IRepositoryManager>() with singleton {
                    RepositoryManager(instance(),instance())
                }

                bind<Gson>() with singleton {
                    instance<GsonBuilder>()
                            .apply {
                                instance<GlobalConfigModule>().provideGsonConfigurations()?.run {
                                    forEach {
                                        it.configGson(instance<Application>(),this@apply)
                                    }
                                }
                            }.create()
                }


                bind<ImageLoader>() with singleton ImageLoader@{
                    return@ImageLoader if(instance<GlobalConfigModule>().provideImageLoaderStrategy()==null){
                        ImageLoader(instanceOrNull<ImageLoaderStrategy<ImageConfig>>(tag = "quickImageLoader")
                                ,instance<GlobalConfigModule>().provideImageLoaderInterceptors())
                    }else{
                        ImageLoader(instance<GlobalConfigModule>().provideImageLoaderStrategy()
                                ,instance<GlobalConfigModule>().provideImageLoaderInterceptors())
                    }
                }

                bind<File>(tag = "cacheFile") with singleton {
                    instance<GlobalConfigModule>().provideCacheFile(instance())
                }

                bind<Cache.Factory>() with singleton {
                    instance<GlobalConfigModule>().provideCacheFactory(instance())
                }

                bind<Cache<String, Any>>() with singleton {
                    instance<Cache.Factory>().build<String, Any>(CacheType.EXTRAS)
                }

                bind<IRetrofitUrlManagerService>() with singleton {
                    RetrofitUrlManagerServiceImpl()
                }

                bind<SharedPreferences>(tag = "quickDev defaultSharedPreferences") with singleton {
                    instance<Application>().getSharedPreferences(instance<Application>().packageName + "_core_preferences",Context.MODE_PRIVATE)
                }

                bind<RxSharedPreferences>() with singleton {
                    RxSharedPreferences.create(instance<SharedPreferences>(tag = "quickDev defaultSharedPreferences"))
                }

                bind<ResponseTransformerStrategy>() with provider {
                    instance<GlobalConfigModule>().provideResponseTransformerStrategy()
                }

                bind<ACache>() with singleton {
                    val aCacheDirPath = instance<File>(tag = "cacheFile").absolutePath
                    ACache.get(File(aCacheDirPath,"ACache"))
                }
            }
        }
    }

    interface GsonConfiguration{
        fun configGson(context: Context, gsonBuilder: GsonBuilder)
    }

    interface RxProgressConfiguration{
        fun provideRxProgressObservable(msg:String,cancelable:Boolean): RxProgressObservable
    }

}