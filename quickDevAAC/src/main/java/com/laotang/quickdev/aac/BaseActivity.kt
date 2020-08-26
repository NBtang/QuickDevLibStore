package com.laotang.quickdev.aac

import android.os.Bundle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders

abstract class BaseActivity : BaseSimpleActivity(), IViewModelProvider {
    private var mViewModelFactory: BaseViewModelFactory? = null

    override fun afterSuperOnCreate(savedInstanceState: Bundle?) {
        super.afterSuperOnCreate(savedInstanceState)
        mViewModelFactory = BaseViewModelFactory(application, kodein, intent)
    }

    override fun onDestroy() {
        mViewModelFactory = null
        super.onDestroy()
    }

    override fun <T : ViewModel> createViewModel(clazz: Class<T>, autoBindLifecycle: Boolean): T {
        val viewModel: T = ViewModelProviders.of(this, mViewModelFactory).get(clazz)
        if (autoBindLifecycle && viewModel is LifecycleObserver) {
            lifecycle.addObserver(viewModel)
        }
        return viewModel
    }
}