package com.laotang.quickdev.mvvm

import com.laotang.quickdevcore.utils.rootKodein
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware

open class BaseModel : KodeinAware {
    override val kodein: Kodein = rootKodein()

}