package com.laotang.component.utils

import com.laotang.quickdev.aretrofit.ARetrofit
import com.laotang.quickdevcore.utils.obtainAppKodeinAware
import org.kodein.di.generic.instance

object ComponentKodeInKt {
    val KARetrofit by obtainAppKodeinAware().instance<ARetrofit>()
}