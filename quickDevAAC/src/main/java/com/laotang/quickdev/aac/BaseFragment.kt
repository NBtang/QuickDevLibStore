package com.laotang.quickdev.aac

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders

abstract class BaseFragment : BaseSimpleFragment(), IViewModelProvider {
    private var mViewModelFactory: BaseViewModelFactory? = null
    protected var mIntent: Intent? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mIntent = arguments?.let {
            val intent = Intent()
            intent.putExtras(it)
            return@let intent
        }
    }

    override fun onDestroy() {
        mViewModelFactory = null
        super.onDestroy()
    }

    override fun <T : ViewModel> createViewModel(clazz: Class<T>, autoBindLifecycle: Boolean): T {
        if (mViewModelFactory == null) {
            mViewModelFactory = BaseViewModelFactory(requireActivity().application, kodein, mIntent)
        }
        val viewModel: T = ViewModelProviders.of(this, mViewModelFactory).get(clazz)
        if (autoBindLifecycle && viewModel is LifecycleObserver) {
            lifecycle.addObserver(viewModel)
        }
        return viewModel
    }
}