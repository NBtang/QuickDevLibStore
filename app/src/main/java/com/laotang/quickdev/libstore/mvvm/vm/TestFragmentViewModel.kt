package com.laotang.quickdev.libstore.mvvm.vm

import android.app.Application
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.laotang.quickdev.mvvm.BaseViewModel
import com.laotang.quickdevcore.integration.rxsubscriber.RxSubscriber
import com.laotang.quickext.autoDisposable
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

class TestFragmentViewModel(application: Application, private val userViewModel: UserViewModel) :
    BaseViewModel(application) {
    private val netWorkLiveData: MutableLiveData<Unit> = MutableLiveData()
    private val onNetWorkLiveData: MutableLiveData<String> = MutableLiveData()

    private val cacheLiveData: MutableLiveData<Unit> = MutableLiveData()
    private val onCacheLiveData: MutableLiveData<String> = MutableLiveData()

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
//        userViewModel.apply {
//            setIntent(this@TestFragmentViewModel.mIntent)
//            bindHostKodein(this@TestFragmentViewModel.kodein)
//            bindLifecycle(owner)
//        }
        netWorkLiveData.observe(owner, Observer {
            userViewModel.getUsers()
        })
        userViewModel.onGetUsersObserver {
            onNetWorkLiveData.postValue(it)
        }
        cacheLiveData.observe(owner, Observer {
            Observable.just("from cache")
                .delay(3,TimeUnit.SECONDS)
                .autoDisposable(scopeProvider)
                .subscribe(object :RxSubscriber<String>(){
                    override fun _onNext(t: String) {
                        onCacheLiveData.postValue(t)
                    }
                })
        })
    }

    fun doNetWork() {
        netWorkLiveData.value = Unit
    }

    fun netWorkDataObserver(block: (value: String) -> Unit) {
        onNetWorkLiveData.observe(this, Observer(block))
    }

    fun getCache() {
        cacheLiveData.value = Unit
    }

    fun cacheDataObserver(block: (value: String) -> Unit) {
        onCacheLiveData.observe(this, Observer(block))
    }
}