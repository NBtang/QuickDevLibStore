package com.laotang.quickdev.mvvm

import androidx.lifecycle.LifecycleOwner

abstract class ViewBindAdapter<VM : BaseViewModel, H : ViewBindHolder> {
    abstract fun converter(viewBindHolder: H, viewModel: VM, lifecycleOwner: LifecycleOwner)
}