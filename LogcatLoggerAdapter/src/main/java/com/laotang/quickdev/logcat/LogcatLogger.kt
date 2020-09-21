package com.laotang.quickdev.logcat

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.annotation.ColorRes
import com.orhanobut.logger.LogAdapter
import com.orhanobut.logger.Logger
import java.io.File
import java.lang.ref.WeakReference

class LogcatLogger(context: Context, logAdapter: LogAdapter? = null) :
    Application.ActivityLifecycleCallbacks {

    private var topActivityWeakReference: WeakReference<Activity>? = null
    private var sensorManagerHelper: SensorManagerHelper? = null
    private var applicationContext: Context = context.applicationContext

    private var logcatTextColorRes: Int = 0
    private var logcatTextSize: Int = 0
    private var logcatBgColorRes: Int = 0

    init {
        val adapter: LogAdapter =
            logAdapter ?: LogcatLoggerAdapter.Builder(applicationContext).build()
        //如果缓存文件存在，清空缓存
        LogcatLogDiskStrategy.getLogcatLoggerFilePath().let {
            if (it.isNotEmpty()) {
                File(it).let { logCacheDir ->
                    if (logCacheDir.exists() && logCacheDir.listFiles().isNotEmpty()) {
                        logCacheDir.list().forEach { fileName ->
                            File(logCacheDir,fileName).delete()
                        }
                    }
                }
            }
        }
        //添加新的策略
        Logger.addLogAdapter(adapter)
    }

    fun setLogcatTextColor(@ColorRes logcatTextColorRes: Int): LogcatLogger {
        this.logcatTextColorRes = logcatTextColorRes
        return this
    }

    fun setLogcatBgColorRes(@ColorRes logcatBgColorRes: Int): LogcatLogger {
        this.logcatBgColorRes = logcatBgColorRes
        return this
    }

    fun setLogcatTextSize(logcatTextSize: Int): LogcatLogger {
        this.logcatTextSize = logcatTextSize
        return this
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        if (topActivityWeakReference == null && sensorManagerHelper == null) {
            applicationContext = activity.applicationContext
            sensorManagerHelper = SensorManagerHelper(applicationContext)
            sensorManagerHelper!!.setOnShakeListener(object : SensorManagerHelper.OnShakeListener {
                override fun onShake() {
                    val context = topActivityWeakReference?.get() ?: applicationContext
                    val topActivity = topActivityWeakReference?.get()
                    //根据前一个activity的横竖屏状态，启动相应的横竖屏Activity
                    val requestedOrientation = if (topActivity != null) {
                        if (topActivity.requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                            || topActivity.requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
                        ) {
                            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                        } else if (topActivity.requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                            || topActivity.requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                        ) {
                            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                        } else {
                            ActivityInfo.SCREEN_ORIENTATION_BEHIND
                        }
                    } else {
                        ActivityInfo.SCREEN_ORIENTATION_BEHIND
                    }
                    val intent = LogcatActivity.LogcatActivityIntentBuilder()
                        .setRequestedOrientation(requestedOrientation)
                        .setLogcatTextColor(logcatTextColorRes)
                        .setLogcatTextSize(logcatTextSize)
                        .setLogcatBgColorRes(logcatBgColorRes)
                        .build(context)
                    if (context !is Activity) {
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                }
            })
        }
        topActivityWeakReference = WeakReference(activity)
    }

    override fun onActivityStarted(activity: Activity) {

    }

    override fun onActivityResumed(activity: Activity) {

    }

    override fun onActivityPaused(activity: Activity) {

    }

    override fun onActivityStopped(activity: Activity) {

    }

    override fun onActivityDestroyed(activity: Activity) {

    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle?) {

    }

}