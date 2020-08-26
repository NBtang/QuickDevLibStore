package com.laotang.quickdev.libstore.mvvm.ui.test

import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.laotang.quickdev.libstore.app.router.FragmentRouter
import com.laotang.quickdev.libstore.base.AppBaseFragment
import com.laotang.quickdev.libstore.mvvm.vm.TestFragmentViewModel
import com.laotang.quickdev.mvvm.Module
import com.laotang.quickdev.mvvm.ViewBindHolder
import com.laotang.quickext.observe
import org.kodein.di.generic.instance

@Route(path = FragmentRouter.RouterTestMVVMFragment)
@Module(values = [TestFragmentDiComponent::class])
class TestMVVMFragment : AppBaseFragment<TestFragmentViewModel>() {

    private val mTestFragmentViewBindHolder: TestFragmentViewBindHolder by instance()

    override fun getViewBindHolder(): ViewBindHolder? {
        return mTestFragmentViewBindHolder
    }

    override fun initViewObservable(savedInstanceState: Bundle?, viewModel: TestFragmentViewModel) {
        super.initViewObservable(savedInstanceState, viewModel)
        mTestFragmentViewBindHolder.btnTest.observe(this) {
            viewModel.doNetWork()
        }
        mTestFragmentViewBindHolder.btnTest2.observe(this){
            viewModel.getCache()
        }
        viewModel.netWorkDataObserver {
            mTestFragmentViewBindHolder.btnTest.text = it
            getMethodChannel()?.invoke("doNetWork", it)
        }
        viewModel.cacheDataObserver {
            mTestFragmentViewBindHolder.btnTest2.text = it
        }
        getMethodChannel()?.addMethodCallHandler {
            if ("testLiveData" == it.method) {
                mTestFragmentViewBindHolder.btnTest.text = it.arguments()
            }
        }
    }

}