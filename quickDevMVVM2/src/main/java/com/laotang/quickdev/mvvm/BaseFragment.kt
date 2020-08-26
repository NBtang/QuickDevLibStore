package com.laotang.quickdev.mvvm

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.*
import java.lang.reflect.ParameterizedType

abstract class BaseFragment<VM : BaseViewModel> : BaseSimpleFragment() {

    protected lateinit var mViewModel: VM
    private var mViewBindHolder: ViewBindHolder? = null

    override fun afterSuperOnActivityCreated(savedInstanceState: Bundle?) {
        super.afterSuperOnActivityCreated(savedInstanceState)
        mViewModel = initViewModel()
        arguments?.apply {
            val intent = Intent()
            intent.putExtras(this)
            mViewModel.setIntent(intent)
        }
        mViewModel.bindLifecycle(this)
    }

    override fun layout(): Int {
        return mViewBindHolder?.layout() ?: 0
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mViewBindHolder = getViewBindHolder()
    }

    override fun contentView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return mViewBindHolder?.contentView(inflater, container, savedInstanceState)
    }

    override fun initView(savedInstanceState: Bundle?, contentView: View?) {
        super.initView(savedInstanceState, contentView)
        mViewBindHolder?.setContentView(contentView)
    }

    open fun initViewObservable(savedInstanceState: Bundle?, viewModel: VM) {
        mViewBindHolder?.setAdapter(getViewBindAdapter(), viewModel, this)
    }

    override fun initViewObservable(savedInstanceState: Bundle?, contentView: View?) {
        super.initViewObservable(savedInstanceState, contentView)
        mViewModel.finishActivityLiveData.observe(this, Observer {
            finishHostActivity()
        })
        initViewObservable(savedInstanceState, mViewModel)
    }

    override fun onDestroy() {
        mViewBindHolder?.setContentView(null)
        super.onDestroy()
    }

    private fun finishHostActivity() {
        requireActivity().finish()
    }

    protected open fun initViewModel(factory: ViewModelProvider.Factory? = null): VM {
        val genericSuperclass = javaClass.genericSuperclass
        return if (genericSuperclass is ParameterizedType) {
            val actualTypeArguments = genericSuperclass.actualTypeArguments
            val genericClass = actualTypeArguments.singleOrNull {
                try {
                    AndroidViewModel::class.java.isAssignableFrom(it as Class<VM>)
                } catch (e: Exception) {
                    e.printStackTrace()
                    false
                }
            }
            ViewModelProviders.of(this, factory).get(genericClass as Class<VM>)
        } else {
            ViewModelProviders.of(this, factory).get(BaseViewModel::class.java) as VM
        }
    }

    abstract fun getViewBindHolder(): ViewBindHolder?
    open fun getViewBindAdapter(): ViewBindAdapter<VM, ViewBindHolder>? {
        return null
    }
}