package com.laotang.quickdevcore.utils

import android.content.Context
import es.dmoral.toasty.Toasty
import org.kodein.di.generic.instance

fun String.toasty(duration: Int = Toasty.LENGTH_LONG) {
    val context by rootKodein().instance<Context>(tag = "applicationContext")
    Toasty.normal(context, this, duration).show()
}