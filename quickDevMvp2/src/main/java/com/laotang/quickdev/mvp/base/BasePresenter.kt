package com.laotang.quickdev.mvp.base

import android.app.Application
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.laotang.quickdevcore.utils.obtainAppKodeinAware
import com.uber.autodispose.ScopeProvider
import com.uber.autodispose.android.lifecycle.scope
import org.kodein.di.Kodein
import org.kodein.di.generic.instance

open class BasePresenter<V:IView>: IPresenter<V>,LifecycleObserver {

    private val mApplication:Application by instance()
    protected var mView:V? = null

    override val kodein: Kodein = obtainAppKodeinAware().kodein
    protected lateinit var owner: LifecycleOwner

    protected val scopeProvider: ScopeProvider by lazy {
        owner.scope(Lifecycle.Event.ON_DESTROY)
    }

    override fun getApplication(): Application {
        return mApplication
    }

    internal fun bindLifecycle(owner: LifecycleOwner){
        this.owner = owner
        this.owner.lifecycle.addObserver(this)
    }

    override fun detach() {
        mView = null
    }

    override fun attach(view: V) {
        mView = view
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    open fun onCreate(){

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    open fun onStart(){

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    open fun onResume(){

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    open fun onPause(){

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    open fun onStop(){

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    open fun onDestroy(){
        detach()
        owner.lifecycle.removeObserver(this)
    }
}
