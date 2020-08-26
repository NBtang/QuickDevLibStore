package com.laotang.quickdev.libstore.mvvm.vm

import android.app.Application
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.laotang.quickdev.mvvm.BaseViewModel
import kotlin.random.Random

class SecondActivityViewModel(application: Application) : BaseViewModel(application) {
    private val mTestLiveData: MutableLiveData<Unit> = MutableLiveData()
    private val mOnTestLiveData: MutableLiveData<String> = MutableLiveData()

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        mTestLiveData.observe(owner, Observer {
            mOnTestLiveData.value =
                "testLiveData${Random(System.currentTimeMillis()).nextInt(1000)}"
        })
    }

    fun test() {
        mTestLiveData.value = Unit
    }

    fun testLiveDataObserve(block: (value: String) -> Unit) {
        mOnTestLiveData.observe(this, Observer {
            block(it)
        })
    }


}