package com.laotang.quickdev.mvp

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import com.laotang.quickdev.mvp.base.PresenterViewModel

class ActivityLifecycleImpl: Application.ActivityLifecycleCallbacks {

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        if(activity is FragmentActivity){
            val viewModel = ViewModelProviders.of(activity).get(PresenterViewModel::class.java)
            viewModel.bindLifecycle(activity)
            activity.supportFragmentManager.registerFragmentLifecycleCallbacks(FragmentLifecycleImpl(), true)
        }
    }

    override fun onActivityDestroyed(activity: Activity) {

    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityResumed(activity: Activity) {
    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle?) {
    }

    override fun onActivityStopped(activity: Activity) {
    }
}