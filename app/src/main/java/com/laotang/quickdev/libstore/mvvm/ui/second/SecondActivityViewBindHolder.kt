package com.laotang.quickdev.libstore.mvvm.ui.second

import android.widget.FrameLayout
import android.widget.TextView
import com.laotang.quickdev.libstore.R
import com.laotang.quickdev.mvvm.ViewBindHolder
import kotterknife.bindView

class SecondActivityViewBindHolder : ViewBindHolder() {
    val contentPanel: FrameLayout by bindView(R.id.contentPanel)
    val btnSecond: TextView by bindView(R.id.btn_second)
    val tvSecond: TextView by bindView(R.id.tv_second)

    override fun layout(): Int {
        return R.layout.activity_second
    }
}