package com.laotang.quickdev.libstore.mvvm.ui.test

import com.alibaba.android.arouter.facade.annotation.Route
import com.laotang.quickdev.libstore.app.router.FragmentRouter
import com.laotang.quickdev.mvvm.BaseSimpleFragment

@Route(path = FragmentRouter.RouterTestFragment)
class TestFragment : BaseSimpleFragment() {

    override fun layout(): Int {
        return 0
    }

    override fun enableView(): Boolean {
        return false
    }

}