package com.laotang.quickdev.libstore.mvvm.ui.second

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.facade.annotation.Route
import com.laotang.quickdev.libstore.base.AppBaseActivity
import com.laotang.quickdev.libstore.app.router.ActivityRouter
import com.laotang.quickdev.libstore.mvvm.vm.SecondActivityViewModel
import com.laotang.quickdev.mvvm.*
import com.laotang.quickext.autoDisposable
import com.laotang.quickext.observe
import org.kodein.di.Kodein
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance

@Route(path = ActivityRouter.RouterSecondActivity)
@Module(values = [SecondActivityDiComponent::class])
class SecondActivity : AppBaseActivity<SecondActivityViewModel>() {

    private val mViewBindHolder: SecondActivityViewBindHolder by instance()
    private val mViewBindAdapter: SecondActivityViewBindAdapter by instance()

    override fun getViewBindHolder(): ViewBindHolder? {
        return mViewBindHolder
    }

    override fun getViewBindAdapter(): ViewBindAdapter<SecondActivityViewModel, ViewBindHolder>? {
        return mViewBindAdapter as ViewBindAdapter<SecondActivityViewModel, ViewBindHolder>
    }

    override fun initView(savedInstanceState: Bundle?, contentView: View?) {
        super.initView(savedInstanceState, contentView)
        val testFragment by instance<Fragment>(tag = "TestFragment")
        val testMVVMFragment by instance<Fragment>(tag = "TestMVVMFragment")
        supportFragmentManager
            .beginTransaction()
            .add(testFragment, "TestFragment")
            .replace(mViewBindHolder.contentPanel.id, testMVVMFragment)
            .commit()
    }

    override fun initViewObservable(
        savedInstanceState: Bundle?,
        viewModel: SecondActivityViewModel
    ) {
        super.initViewObservable(savedInstanceState, viewModel)
        mViewBindHolder.btnSecond
            .observe(this) {
                viewModel.test()
            }
        mViewBindHolder.tvSecond
            .observe(this) {
                ActivityRouter.startThirdActivity(this)
                    .autoDisposable(scopeProvider)
                    .subscribe {
                        if (it.resultCode == Activity.RESULT_OK && it.data != null) {
                            mViewBindHolder.tvSecond.text =
                                "${it.data.getStringExtra("user_login")}"
                            return@subscribe
                        }
                        mViewBindHolder.tvSecond.text = "${it.requestCode}"
                    }
            }
        getMethodChannel()?.addMethodCallHandler {
            if ("doNetWork" == it.method) {
                mViewBindHolder.tvSecond.text = it.arguments()
            }
        }
    }

}