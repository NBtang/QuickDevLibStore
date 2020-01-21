package com.laotang.quickdev.mvp

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProviders
import com.laotang.quickdev.mvp.base.BaseFragment
import com.laotang.quickdev.mvp.base.PresenterViewModel

class FragmentLifecycleImpl: FragmentManager.FragmentLifecycleCallbacks()  {
    override fun onFragmentPreAttached(fm: FragmentManager, f: Fragment, context: Context) {
        if(f is BaseFragment){
            val viewModel = ViewModelProviders.of(f).get(PresenterViewModel::class.java)
            viewModel.bindLifecycle(f)
        }
    }
}