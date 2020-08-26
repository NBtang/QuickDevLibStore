package com.laotang.quickdev.libstore.app

import android.content.Context
import androidx.multidex.MultiDexApplication
import com.laotang.quickdevcore.utils.rootKodein
import com.orhanobut.logger.*
import org.json.JSONArray
import org.json.JSONObject
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import timber.log.Timber

class DemoApplication : MultiDexApplication(), KodeinAware {

    override val kodein: Kodein
        get() = rootKodein()

    override fun onCreate() {
        super.onCreate()
        val formatStrategy = PrettyFormatStrategy.newBuilder()
            .showThreadInfo(false)  // (Optional) Whether to show thread info or not. Default true
            .methodCount(0)         // (Optional) How many method line to show. Default 2
            .methodOffset(7)        // (Optional) Hides internal method calls up to offset. Default 5
            .logStrategy(LogcatLogStrategy()) // (Optional) Changes the log strategy to print out. Default LogCat
            .tag("testDemo")   // (Optional) Global tag for every log. Default PRETTY_LOGGER
            .build()
        Logger.addLogAdapter(AndroidLogAdapter(formatStrategy))

        Timber.plant(object : Timber.DebugTree() {
            override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                try {
                    when {
                        message.startsWith("{") -> {
                            JSONObject(message)
                            Logger.json(message)
                        }
                        message.startsWith("[") -> {
                            JSONArray(message)
                            Logger.json(message)
                        }
                        else -> Logger.log(priority, tag, message, t)
                    }
                } catch (e: Exception) {
                    Logger.log(priority, tag, message, t)
                }
            }
        })
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
    }
}