package com.laotang.quickdev.libstore.mvvm.vm

import android.app.Application
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.laotang.quickdev.libstore.mvvm.model.entity.User
import com.laotang.quickdev.libstore.mvvm.model.repository.UserRepository
import com.laotang.quickdev.mvvm.BaseViewModel
import com.laotang.quickdevcore.integration.rxsubscriber.RxSubscriber
import com.laotang.quickext.autoDisposable
import org.kodein.di.generic.instance

class UserViewModel(application: Application) : BaseViewModel(application) {
    private val mUserRepository: UserRepository by instance()

    private val getUsersLiveData: MutableLiveData<Unit> = MutableLiveData()
    private val onGetUsersLiveData: MutableLiveData<String> = MutableLiveData()

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        getUsersLiveData.observe(owner, Observer {
            mUserRepository.getUsers(1, 10)
                .autoDisposable(scopeProvider)
                .subscribe(object : RxSubscriber<List<User>>(this, "doNetWork") {
                    override fun _onNext(t: List<User>) {
                        onGetUsersLiveData.postValue(t.toString())
                    }

                    override fun _onError(e: Throwable) {
                        super._onError(e)
                        onGetUsersLiveData.postValue(e.message)
                    }
                })
        })
    }

    fun getUsers() {
        getUsersLiveData.value = Unit
    }

    fun onGetUsersObserver(block: (value: String) -> Unit) {
        onGetUsersLiveData.observe(this, Observer(block))
    }
}