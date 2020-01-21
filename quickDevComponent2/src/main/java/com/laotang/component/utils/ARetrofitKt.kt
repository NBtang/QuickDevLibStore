package com.laotang.component.utils

import android.annotation.SuppressLint
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.laotang.quickdev.aretrofit.NavigationData
import com.laotang.quickdev.rxactivity.ActivityResultEntity
import com.uber.autodispose.AutoDispose
import com.uber.autodispose.android.lifecycle.scope
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers

fun Observable<NavigationData>.go() {
    this.subscribe()
}

@SuppressLint("CheckResult")
fun Observable<NavigationData>.go(block: (navigationData: NavigationData) -> Unit) {
    this.subscribe {
        block(it)
    }
}

fun Observable<ActivityResultEntity>.goForResult(
    owner: LifecycleOwner,
    block: (activityResultEntity: ActivityResultEntity) -> Unit
) {
    this.observeOn(AndroidSchedulers.mainThread())
        .`as`(AutoDispose.autoDisposable(owner.scope(Lifecycle.Event.ON_DESTROY)))
        .subscribe {
            block(it)
        }
}