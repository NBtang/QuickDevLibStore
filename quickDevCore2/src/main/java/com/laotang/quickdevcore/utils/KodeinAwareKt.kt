package com.laotang.quickdevcore.utils

import com.laotang.quickdevcore.base.delegate.AppDelegate
import org.kodein.di.KodeinAware

fun Any.obtainAppKodeinAware(): KodeinAware {
    return (AppDelegate.instance)
}