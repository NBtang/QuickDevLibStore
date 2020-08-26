package com.laotang.quickdev.mvvm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import com.laotang.quickdev.mvvm.channel.IMethodChannelProvider
import com.laotang.quickdev.mvvm.channel.MethodChannel
import com.laotang.quickdevcore.utils.rootKodein
import com.uber.autodispose.ScopeProvider
import com.uber.autodispose.android.lifecycle.scope
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance

abstract class BaseSimpleActivity : AppCompatActivity(), IActivity,
    IMethodChannelProvider {

    private var mMethodChannel: MethodChannel? = null

    override val kodein: Kodein = Kodein.lazy {
        val parentKodein: Kodein = rootKodein()
        extend(parentKodein)
        /* activity specific bindings */
        if (this@BaseSimpleActivity.javaClass.isAnnotationPresent(Module::class.java)) {
            val module = this@BaseSimpleActivity.javaClass.getAnnotation(Module::class.java)!!
            val clazzArray = module.values
            clazzArray.iterator().forEach { clazz ->
                if (KodeinModuleProvider::class.java.isAssignableFrom(clazz.java)) {
                    val instance = clazz.java.newInstance() as KodeinModuleProvider
                    import(module = instance.providerModule(), allowOverride = true)
                }
            }
        }
        bind<FragmentActivity>() with instance(this@BaseSimpleActivity)
    }

    protected val scopeProvider: ScopeProvider by lazy {
        this.scope(Lifecycle.Event.ON_DESTROY)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        afterSuperOnCreate(savedInstanceState)
        initParam(savedInstanceState)
        var contentView = contentView()
        if (contentView != null) {
            setContentView(contentView)
        } else {
            val layoutId = layout()
            if (layoutId == 0) {
                throw AssertionError("layout resId ==0")
            }
            contentView = LayoutInflater.from(this).inflate(layoutId, null)
            setContentView(contentView)
        }
        initView(savedInstanceState, contentView)
        initViewObservable(savedInstanceState, contentView)
    }

    override fun initParam(savedInstanceState: Bundle?) {}

    override fun initView(savedInstanceState: Bundle?, contentView: View?) {}

    override fun initViewObservable(savedInstanceState: Bundle?, contentView: View?) {}

    open fun afterSuperOnCreate(savedInstanceState: Bundle?) {}

    open fun contentView(): View? {
        return null
    }

    override fun getMethodChannel(): MethodChannel? {
        if (mMethodChannel == null) {
            mMethodChannel = MethodChannel()
        }
        return mMethodChannel
    }
}