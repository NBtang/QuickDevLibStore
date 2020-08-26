package com.laotang.quickdev.libstore.mvvm.ui.second

import androidx.fragment.app.Fragment
import com.laotang.quickdev.libstore.app.router.FragmentRouter
import com.laotang.quickdev.mvvm.KodeinModuleProvider
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.provider

class SecondActivityDiComponent : KodeinModuleProvider {
    override fun providerModule(): Kodein.Module {
        return Kodein.Module(name = "SecondActivityModel", allowSilentOverride = true) {

            bind<SecondActivityViewBindHolder>() with provider {
                SecondActivityViewBindHolder()
            }

            bind<SecondActivityViewBindAdapter>() with provider {
                SecondActivityViewBindAdapter()
            }

            bind<Fragment>(tag = "TestFragment") with provider {
                FragmentRouter.findFragment(FragmentRouter.RouterTestFragment)
            }

            bind<Fragment>(tag = "TestMVVMFragment") with provider {
                FragmentRouter.findFragment(FragmentRouter.RouterTestMVVMFragment)
            }

        }
    }
}