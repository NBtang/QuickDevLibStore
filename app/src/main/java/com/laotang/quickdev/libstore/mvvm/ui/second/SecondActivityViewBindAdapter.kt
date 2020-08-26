package com.laotang.quickdev.libstore.mvvm.ui.second

import androidx.lifecycle.LifecycleOwner
import com.laotang.quickdev.libstore.mvvm.vm.SecondActivityViewModel
import com.laotang.quickdev.mvvm.ViewBindAdapter

class SecondActivityViewBindAdapter :
    ViewBindAdapter<SecondActivityViewModel, SecondActivityViewBindHolder>() {

    override fun converter(
        viewBindHolder: SecondActivityViewBindHolder,
        viewModel: SecondActivityViewModel,
        lifecycleOwner: LifecycleOwner
    ) {
        viewModel.testLiveDataObserve {
            viewBindHolder.tvSecond.text = it
        }
    }

}