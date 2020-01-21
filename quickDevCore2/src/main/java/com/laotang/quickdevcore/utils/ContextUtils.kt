package com.laotang.quickdevcore.utils

import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.fragment.app.Fragment

fun Context.getStatusBarHeight():Int{
    var statusBarHeight = 0
    //获取status_bar_height资源的ID
    val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
    if (resourceId > 0) {
        //根据资源ID获取响应的尺寸值
        statusBarHeight = resources.getDimensionPixelSize(resourceId)
    }
    return statusBarHeight
}

fun Context.getScreenWidth():Int{
    val wm = this.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val outMetrics = DisplayMetrics()
    wm.defaultDisplay.getMetrics(outMetrics)
    return outMetrics.widthPixels
}

fun Fragment.getStatusBarHeight():Int{
    var statusBarHeight = 0
    //获取status_bar_height资源的ID
    val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
    if (resourceId > 0) {
        //根据资源ID获取响应的尺寸值
        statusBarHeight = resources.getDimensionPixelSize(resourceId)
    }
    return statusBarHeight
}

fun Fragment.getScreenWidth():Int{
    val wm = requireContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val outMetrics = DisplayMetrics()
    wm.defaultDisplay.getMetrics(outMetrics)
    return outMetrics.widthPixels
}