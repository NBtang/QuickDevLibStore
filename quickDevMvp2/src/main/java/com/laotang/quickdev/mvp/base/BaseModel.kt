package com.laotang.quickdev.mvp.base

import com.laotang.quickdevcore.utils.obtainAppKodeinAware
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware

open class BaseModel : KodeinAware {
    override val kodein: Kodein = obtainAppKodeinAware().kodein

}