package com.laotang.quickdevcore.utils

import com.laotang.quickdevcore.base.delegate.AppDelegate
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware

fun rootKodein(): Kodein {
    return (AppDelegate.instance.kodein)
}