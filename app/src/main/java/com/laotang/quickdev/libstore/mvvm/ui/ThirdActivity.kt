package com.laotang.quickdev.libstore.mvvm.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.alibaba.android.arouter.facade.annotation.Route
import com.laotang.quickdev.libstore.R
import com.laotang.quickdev.libstore.app.router.ActivityRouter
import com.laotang.quickdev.libstore.mvvm.model.repository.UserRepository
import com.laotang.quickdev.mvvm.BaseSimpleActivity
import com.laotang.quickext.observe
import kotterknife.bindView
import org.kodein.di.generic.instance

@Route(path = ActivityRouter.RouterThirdActivity)
class ThirdActivity : BaseSimpleActivity() {
    private val tvContent: TextView by bindView(R.id.tv_content)
    private val mUserRepository: UserRepository by instance()

    override fun layout(): Int {
        return R.layout.activity_third
    }

    override fun initView(savedInstanceState: Bundle?, contentView: View?) {
        super.initView(savedInstanceState, contentView)
        val user = mUserRepository.getUser()
        tvContent.text = user?.login ?: ""
        tvContent.observe(this) {
            val intent = Intent()
            intent.putExtra("user_login", user?.login ?: "")
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }
}