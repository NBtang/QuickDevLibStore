package com.laotang.quickdev.mvvm

import android.app.Application
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.lifecycle.*
import com.laotang.quickdevcore.utils.obtainAppKodeinAware
import com.uber.autodispose.ScopeProvider
import com.uber.autodispose.android.lifecycle.scope
import org.kodein.di.Kodein

open class BaseViewModel(application: Application) : AndroidViewModel(application), IBaseViewModel {

    private lateinit var hostKodein: Kodein
    override val kodein: Kodein by lazy {
        hostKodein
    }

    private val cacheDataMap = HashMap<String, Any>()
    internal val finishActivityLiveData: MutableLiveData<Unit> = MutableLiveData()
    private lateinit var mLifecycleRegistry: LifecycleRegistry
    private lateinit var lifecycleOwner: LifecycleOwner
    private var extras: Bundle? = null

    init {
        init()
    }

    protected val resources: Resources by lazy {
        getApplication<Application>().resources
    }

    protected val scopeProvider: ScopeProvider by lazy {
        lifecycleOwner.scope(Lifecycle.Event.ON_DESTROY)
    }

    internal fun bindLifecycle(lifecycleOwner: LifecycleOwner) {
        this.lifecycleOwner = lifecycleOwner
        lifecycleOwner.lifecycle.addObserver(this)
    }

    internal fun bindHostKodein(hostKodein: Kodein) {
        this.hostKodein = hostKodein
    }

    internal fun setExtraDatas(extras: Bundle?){
        this.extras = extras
    }

    protected fun finishActivity() {
        finishActivityLiveData.value = Unit
    }

    protected fun getString(@StringRes id: Int): String {
        return resources.getString(id)
    }

    protected fun getColor(@ColorRes id: Int): Int {
        return resources.getColor(id)
    }

    fun saveData(key: String, data: Any) {
        cacheDataMap[key] = data
    }

    fun <T> getData(key: String): T {
        return cacheDataMap[key] as T
    }

    fun removeData(key: String) {
        cacheDataMap.remove(key)
    }

    override fun onCleared() {
        super.onCleared()
        cacheDataMap.clear()
    }

    override fun onCreate(owner: LifecycleOwner) {
        mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        lifecycleOwner.lifecycle.removeObserver(this)
    }

    override fun onStart(owner: LifecycleOwner) {
        mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
    }

    override fun onStop(owner: LifecycleOwner) {
        mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
    }

    override fun onResume(owner: LifecycleOwner) {
        mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    override fun onPause(owner: LifecycleOwner) {
        mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    }

    override fun getLifecycle(): Lifecycle {
        return mLifecycleRegistry
    }

    private fun init() {
        mLifecycleRegistry = LifecycleRegistry(this)
    }
}