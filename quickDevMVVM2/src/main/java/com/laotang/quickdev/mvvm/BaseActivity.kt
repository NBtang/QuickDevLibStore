package com.laotang.quickdev.mvvm

import android.os.Bundle
import android.view.View
import androidx.lifecycle.*
import java.lang.reflect.ParameterizedType

abstract class BaseActivity<VM : BaseViewModel> : BaseSimpleActivity() {

    protected lateinit var mViewModel: VM
    private var mViewBindHolder: ViewBindHolder? = null

    override fun afterSuperOnCreate(savedInstanceState: Bundle?) {
        super.afterSuperOnCreate(savedInstanceState)
        mViewModel = initViewModel()
        mViewModel.setIntent(intent)
        mViewModel.bindLifecycle(this)
        mViewBindHolder = getViewBindHolder()
    }

    override fun layout(): Int {
        return mViewBindHolder?.layout() ?: 0
    }

    override fun contentView(): View? {
        return mViewBindHolder?.contentView()
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
            finish()
        })
        initViewObservable(savedInstanceState, mViewModel)
    }

    override fun onDestroy() {
        super.onDestroy()
        mViewBindHolder?.setContentView(null)
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