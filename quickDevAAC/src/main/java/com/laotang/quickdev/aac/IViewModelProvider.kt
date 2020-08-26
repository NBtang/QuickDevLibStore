package com.laotang.quickdev.aac

import androidx.lifecycle.ViewModel

interface IViewModelProvider {
    fun <T : ViewModel> createViewModel(clazz: Class<T>, autoBindLifecycle: Boolean = true): T
}