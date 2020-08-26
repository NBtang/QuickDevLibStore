package com.laotang.quickdev.libstore.mvvm.ui.test

import com.laotang.quickdev.mvvm.KodeinModuleProvider
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.provider

class TestFragmentDiComponent : KodeinModuleProvider {
    override fun providerModule(): Kodein.Module {
        return Kodein.Module(name = "TestFragmentDiComponent", allowSilentOverride = true) {

            bind<TestFragmentViewBindHolder>() with provider {
                TestFragmentViewBindHolder()
            }

        }
    }
}