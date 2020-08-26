package com.laotang.quickext

import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.jakewharton.rxbinding3.view.clicks
import com.laotang.quickdevcore.integration.imageloader.ImageConfig
import com.laotang.quickdevcore.integration.imageloader.ImageLoader
import com.laotang.quickdevcore.utils.rootKodein
import com.uber.autodispose.android.lifecycle.scope
import io.reactivex.Observable
import org.kodein.di.generic.instance
import java.util.concurrent.TimeUnit

//2秒内只响应一次点击事件，防抖机制
fun View.throttleFirstClicks(duration: Long = 2): Observable<Unit> {
    return this.clicks().throttleFirst(duration, TimeUnit.SECONDS)
}


fun View.loadImage(imageConfig: ImageConfig) {
    val imageLoader by rootKodein().instance<ImageLoader>()
    imageLoader.loadImage(this, imageConfig)
}

fun View.observe(owner: LifecycleOwner, block: (v: View) -> Unit) {
    this.throttleFirstClicks(1)
        .autoDisposable(owner.scope(Lifecycle.Event.ON_DESTROY))
        .subscribe {
            block(this)
        }
}
