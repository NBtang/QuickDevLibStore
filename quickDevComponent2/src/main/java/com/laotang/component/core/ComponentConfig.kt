package com.laotang.component.core

import android.content.Context
import com.laotang.quickdev.aretrofit.ARetrofit

class ComponentConfig private constructor(builder: Builder) {

    private var enableLeakCanary = true
    private var enableDoraemonKit = true
    private var aRetrofitConfiguration: ARetrofitConfiguration? = null

    init {
        this.enableLeakCanary = builder.enableLeakCanary
        this.enableDoraemonKit = builder.enableDoraemonKit
        this.aRetrofitConfiguration = builder.aRetrofitConfiguration
    }

    fun enableLeakCanary(): Boolean {
        return this.enableLeakCanary
    }

    fun enableDoraemonKit(): Boolean {
        return this.enableDoraemonKit
    }

    fun provideARetrofitConfiguration(): ARetrofitConfiguration? {
        return this.aRetrofitConfiguration
    }

    class Builder {
        internal var enableLeakCanary = true
        internal var enableDoraemonKit = true
        internal var aRetrofitConfiguration: ARetrofitConfiguration? = null

        fun enableLeakCanary(enable: Boolean) {
            this.enableLeakCanary = enable
        }

        fun enableDoraemonKit(enable: Boolean) {
            this.enableDoraemonKit = enable
        }

        fun setARetrofitConfiguration(aRetrofitConfiguration: ARetrofitConfiguration) {
            this.aRetrofitConfiguration = aRetrofitConfiguration
        }

        internal fun build(): ComponentConfig {
            return ComponentConfig(this)
        }
    }

    interface ARetrofitConfiguration {
        fun configARetrofit(context: Context, builder: ARetrofit.Builder)
    }
}