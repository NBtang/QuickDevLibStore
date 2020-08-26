package com.laotang.quickdev.mvp.base

import android.app.Application
import androidx.lifecycle.*

class PresenterViewModel(application: Application) : AndroidViewModel(application),
    LifecycleObserver, LifecycleOwner {

    private val presentersMap = HashMap<String, IPresenter<*>>()
    private var mLifecycleRegistry: LifecycleRegistry = LifecycleRegistry(this)
    private lateinit var lifecycleOwner: LifecycleOwner

    internal fun bindLifecycle(lifecycleOwner: LifecycleOwner) {
        this.lifecycleOwner = lifecycleOwner
        lifecycleOwner.lifecycle.addObserver(this)
    }

    internal fun get(key: String): IPresenter<*>? {
        if (presentersMap.containsKey(key)) {
            return presentersMap[key]
        }
        return null
    }

    internal fun put(key: String, presenter: IPresenter<*>) {
        presentersMap[key] = presenter
    }

    override fun onCleared() {
        super.onCleared()
        presentersMap.clear()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        lifecycleOwner.lifecycle.removeObserver(this)
    }

    override fun getLifecycle(): Lifecycle {
        return mLifecycleRegistry
    }

}