package com.laotang.quickdev.aac

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.laotang.quickdevcore.utils.rootKodein
import com.uber.autodispose.ScopeProvider
import com.uber.autodispose.android.lifecycle.scope
import org.kodein.di.Kodein

open class ShareViewModel(application: Application) : AndroidViewModel(application),
    IBaseViewModel {
    private var mLifecycleRegistry: LifecycleRegistry? = null

    init {
        init()
    }

    protected val scopeProvider: ScopeProvider by lazy {
        lifecycle.scope(Lifecycle.Event.ON_DESTROY)
    }

    override fun onCreate(owner: LifecycleOwner) {
        mLifecycleRegistry?.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        mLifecycleRegistry?.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        owner.lifecycle.removeObserver(this)
    }

    override fun onStart(owner: LifecycleOwner) {
        mLifecycleRegistry?.handleLifecycleEvent(Lifecycle.Event.ON_START)
    }

    override fun onStop(owner: LifecycleOwner) {
        mLifecycleRegistry?.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
    }

    override fun onResume(owner: LifecycleOwner) {
        mLifecycleRegistry?.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    override fun onPause(owner: LifecycleOwner) {
        mLifecycleRegistry?.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    }

    override fun getLifecycle(): Lifecycle {
        return mLifecycleRegistry!!
    }

    override fun onCleared() {
        super.onCleared()
        mLifecycleRegistry = null
    }

    private fun init() {
        mLifecycleRegistry = LifecycleRegistry(this)
    }

    override val kodein: Kodein
        get() = rootKodein()
}