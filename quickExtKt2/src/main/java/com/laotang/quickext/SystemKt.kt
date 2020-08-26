package com.laotang.quickext

import android.content.Context
import android.content.res.Resources
import com.laotang.quickdevcore.utils.rootKodein
import org.kodein.di.generic.instance
import java.lang.Exception

/**
 * 正常编码中一般只会用到 [dp]/[sp] ;
 * 其中[dp]/[sp] 会根据系统分辨率将输入的dp/sp值转换为对应的px
 */

val appResources: Resources
    get() {
        return try {
            val applicationContext: Context by rootKodein().instance(tag = "applicationContext")
            applicationContext.resources
        } catch (e: Exception) {
            e.printStackTrace()
            Resources.getSystem()
        }
    }

val Float.dp: Float                 // [xxhdpi](360 -> 1080)
    get() = android.util.TypedValue.applyDimension(
        android.util.TypedValue.COMPLEX_UNIT_DIP, this, appResources.displayMetrics
    )

val Int.dp: Int
    get() = android.util.TypedValue.applyDimension(
        android.util.TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), appResources.displayMetrics
    ).toInt()


val Float.sp: Float                 // [xxhdpi](360 -> 1080)
    get() = android.util.TypedValue.applyDimension(
        android.util.TypedValue.COMPLEX_UNIT_SP, this, appResources.displayMetrics
    )


val Int.sp: Int
    get() = android.util.TypedValue.applyDimension(
        android.util.TypedValue.COMPLEX_UNIT_SP, this.toFloat(), appResources.displayMetrics
    ).toInt()