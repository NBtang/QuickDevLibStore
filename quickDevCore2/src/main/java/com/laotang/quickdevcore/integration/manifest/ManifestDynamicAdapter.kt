package com.laotang.quickdevcore.integration.manifest

import android.app.Activity
import android.app.Application
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.WindowManager

class ManifestDynamicAdapter(private val adapter: ActivityConfigAdapter? = null) :
    Application.ActivityLifecycleCallbacks {

    private var screenOrientation: Int = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        requestedOrientation(activity)
        adapter?.config(activity, savedInstanceState)
    }

    private fun requestedOrientation(activity: Activity) {
        if (activity.javaClass.isAnnotationPresent(Orientation::class.java)) {
            val orientation = activity.javaClass.getAnnotation(Orientation::class.java)!!
            activity.requestedOrientation = orientation.value
        } else {
            activity.requestedOrientation = screenOrientation
        }
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityResumed(activity: Activity) {
    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityDestroyed(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle?) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    interface ActivityConfigAdapter {
        fun config(activity: Activity, savedInstanceState: Bundle?)
    }
}