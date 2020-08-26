package com.laotang.quickdev.aac

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders

abstract class BaseFragment : BaseSimpleFragment(), IViewModelProvider {
    private var mViewModelFactory: BaseViewModelFactory? = null

    override fun afterSuperOnActivityCreated(savedInstanceState: Bundle?) {
        super.afterSuperOnActivityCreated(savedInstanceState)
        val intent = arguments?.let {
            val intent = Intent()
            intent.putExtras(it)
            intent
        }
        mViewModelFactory = BaseViewModelFactory(requireActivity().application, kodein, intent)
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