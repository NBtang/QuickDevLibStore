package com.laotang.quickdevcore.integration.rxsubscriber

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.laotang.quickdevcore.di.module.GlobalConfigModule
import com.laotang.quickdevcore.integration.AppManager
import com.laotang.quickdevcore.utils.rootKodein
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableObserver
import me.jessyan.rxerrorhandler.core.RxErrorHandler
import org.kodein.di.generic.instance

abstract class RxSubscriber<T>(
    private val lifecycleOwner: LifecycleOwner? = null,
    msg: String = "",
    showProgress: Boolean = true,
    cancelable: Boolean = true
) : DisposableObserver<T>() {

    private val rxErrorHandler by rootKodein().instance<RxErrorHandler>()
    private val mGlobalConfigModule by rootKodein().instance<GlobalConfigModule>()
    private var progressObservable: RxProgressObservable? = null
    private val mHandlerFactory = rxErrorHandler.handlerFactory
    private var cancelDisposable: Disposable? = null

    init {
        if (showProgress && msg.isNotEmpty()) {
            progressObservable = mGlobalConfigModule.provideRxProgressConfiguration()
                .provideRxProgressObservable(msg, cancelable)
        }
    }

    override fun onStart() {
        super.onStart()
        val instance = this
        //如果当前的生命周期状态为onCreated，使用getTopActivity获取activity
        //如果是其他生命周期状态，使用getCurrentActivity获取activity
        val activity = if (lifecycleOwner == null) {
            AppManager.instance.getCurrentActivity()
        } else {
            if (lifecycleOwner.lifecycle.currentState == Lifecycle.State.CREATED
                || lifecycleOwner.lifecycle.currentState == Lifecycle.State.INITIALIZED
                || lifecycleOwner.lifecycle.currentState == Lifecycle.State.STARTED
            ) {
                //AppManager.instance还未更新CurrentActivity
                AppManager.instance.getTopActivity()
            } else {
                AppManager.instance.getCurrentActivity()
            }
        }
        if (activity != null) {
            val cancelObservable = progressObservable?.showProgress(activity)
            cancelDisposable = cancelObservable?.let { observable ->
                observable.observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        if (it) {
                            instance.dispose()
                        }
                    }
            }
        }
    }

    override fun onNext(t: T) {
        dismissLoadingDialog()
        _onNext(t)
    }

    override fun onComplete() {
        dismissLoadingDialog()
    }

    final override fun onError(e: Throwable) {
        dismissLoadingDialog()
        e.printStackTrace()
        _onError(e)
    }

    open fun _onError(e: Throwable) {
        //如果你某个地方不想使用全局错误处理,则重写 _onError(Throwable) 并将 super._onError(e); 删掉
        //如果你不仅想使用全局错误处理,还想加入自己的逻辑,则重写 _onError(Throwable) 并在 super._onError(e); 后面加入自己的逻辑
        mHandlerFactory.handleError(e)
    }

    private fun dismissLoadingDialog() {
        cancelDisposable?.apply {
            if (!isDisposed) {
                dispose()
            }
        }
        progressObservable?.apply {
            if (isShowing()) {
                dismiss()
            }
        }
    }

    abstract fun _onNext(t: T)


}