package com.laotang.quickdev.libstore.base

import androidx.lifecycle.ViewModelProvider
import com.laotang.quickdev.mvvm.BaseFragment
import com.laotang.quickdev.mvvm.BaseViewModel
import org.kodein.di.generic.instanceOrNull

abstract class AppBaseFragment<VM : BaseViewModel> : BaseFragment<VM>() {

    private var mFactory: ViewModelProvider.Factory? = null

    override fun initViewModel(factory: ViewModelProvider.Factory?): VM {
        if (mFactory == null) {
            mFactory = getViewModelFactory()
        }
        return super.initViewModel(mFactory)
    }

    open fun getViewModelFactory(): ViewModelProvider.Factory? {
        val factory by instanceOrNull<ViewModelProvider.Factory>()
        return factory
    }
}