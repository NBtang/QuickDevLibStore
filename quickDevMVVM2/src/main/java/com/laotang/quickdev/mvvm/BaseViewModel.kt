package com.laotang.quickdev.mvvm

import android.app.Application
import android.content.Intent
import android.content.res.Resources
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.lifecycle.*
import com.laotang.quickdev.mvvm.channel.MethodChannel
import com.laotang.quickdevcore.utils.rootKodein
import com.uber.autodispose.ScopeProvider
import com.uber.autodispose.android.lifecycle.scope
import org.kodein.di.Kodein

open class BaseViewModel(application: Application) : AndroidViewModel(application), IBaseViewModel {

    override val kodein: Kodein = rootKodein()

    private val cacheDataMap = HashMap<String, Any>()
    internal val finishActivityLiveData: MutableLiveData<Unit> = MutableLiveData()
    private lateinit var mLifecycleRegistry: LifecycleRegistry
    private lateinit var lifecycleOwner: LifecycleOwner
    var mIntent: Intent? = null
    private var existedViewModelBind: IBaseViewModel.OnExistedViewModelBind? = null

    init {
        init()
    }

    protected val resources: Resources by lazy {
        getApplication<Application>().resources
    }

    protected val scopeProvider: ScopeProvider by lazy {
        lifecycleOwner.scope(Lifecycle.Event.ON_DESTROY)
    }

    fun bindLifecycle(lifecycleOwner: LifecycleOwner) {
        this.lifecycleOwner = lifecycleOwner
        lifecycleOwner.lifecycle.addObserver(this)
    }

    fun setIntent(intent: Intent?) {
        this.mIntent = intent
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
        existedViewModelBind?.bind()
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

    override fun setExistedViewModelBinder(bind: IBaseViewModel.OnExistedViewModelBind) {
        this.existedViewModelBind = bind
    }

    override fun getLifecycle(): Lifecycle {
        return mLifecycleRegistry
    }

    private fun init() {
        mLifecycleRegistry = LifecycleRegistry(this)
    }
}