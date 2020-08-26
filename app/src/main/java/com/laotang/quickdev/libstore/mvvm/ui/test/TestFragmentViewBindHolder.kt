package com.laotang.quickdev.libstore.mvvm.ui.test

import android.widget.TextView
import com.laotang.quickdev.libstore.R
import com.laotang.quickdev.mvvm.ViewBindHolder
import kotterknife.bindView

class TestFragmentViewBindHolder : ViewBindHolder() {
    val btnTest2: TextView by bindView(R.id.btn_test2)
    val btnTest: TextView by bindView(R.id.btn_test)

    override fun layout(): Int {
        return R.layout.fragment_test
    }

}