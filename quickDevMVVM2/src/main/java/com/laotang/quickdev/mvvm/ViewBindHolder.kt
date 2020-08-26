package com.laotang.quickdev.mvvm

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import com.laotang.quickdevcore.integration.bindview.IViewBindHolder

abstract class ViewBindHolder : IViewBindHolder {
    private var contentView: View? = null
    private var viewBindAdapter: ViewBindAdapter<*, ViewBindHolder>? = null
    abstract fun layout(): Int

    open fun contentView(): View? {
        return contentView
    }

    open fun contentView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return contentView
    }

    internal fun setContentView(contentView: View?) {
        this.contentView = contentView
    }

    override fun requireContentView(): View {
        return contentView!!
    }

    fun requireActivity(): Activity {
        return contentView!!.context as Activity
    }

    fun <VM : BaseViewModel> getViewBindAdapter(): ViewBindAdapter<VM, ViewBindHolder>? {
        if (this.viewBindAdapter == null) {
            return null
        }
        return this.viewBindAdapter as ViewBindAdapter<VM, ViewBindHolder>
    }

    fun <VM : BaseViewModel> setAdapter(
        viewBindAdapter: ViewBindAdapter<VM, ViewBindHolder>?,
        viewModel: VM,
        lifecycleOwner: LifecycleOwner
    ) {
        this.viewBindAdapter = viewBindAdapter
        viewBindAdapter?.converter(this, viewModel, lifecycleOwner)
    }

}