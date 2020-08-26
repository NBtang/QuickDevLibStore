package com.laotang.quickdevcore.base

import android.content.Context
import android.content.ContextWrapper
import com.laotang.quickdevcore.base.delegate.AppDelegate
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware

class KodeinContextWrapper(base: Context) : ContextWrapper(base), KodeinAware {
    override val kodein: Kodein = Kodein.lazy {
        extend(AppDelegate.instance.kodein)
    }
}