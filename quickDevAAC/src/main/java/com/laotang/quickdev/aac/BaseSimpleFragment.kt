package com.laotang.quickdev.aac

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.laotang.quickdevcore.utils.rootKodein
import com.uber.autodispose.ScopeProvider
import com.uber.autodispose.android.lifecycle.scope
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import java.lang.AssertionError

abstract class BaseSimpleFragment : Fragment(), IView {
    private var baseView: View? = null
    private var firstViewCreated = false

    override val kodein: Kodein = Kodein.lazy {
        val parentKodein: Kodein = rootKodein()
        extend(parentKodein)
        if (this@BaseSimpleFragment.javaClass.isAnnotationPresent(Module::class.java)) {
            val module = this@BaseSimpleFragment.javaClass.getAnnotation(Module::class.java)
            val clazzArray = module.values
            clazzArray.iterator().forEach { clazz ->
                if (KodeinModuleProvider::class.java.isAssignableFrom(clazz.java)) {
                    val instance = clazz.java.newInstance() as KodeinModuleProvider
                    import(module = instance.providerModule(), allowOverride = true)
                }
            }
        }
        bind<Fragment>() with instance(this@BaseSimpleFragment)
    }

    protected val scopeProvider: ScopeProvider by lazy {
        this.scope(Lifecycle.Event.ON_DESTROY)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (!enableView()) {
            baseView = null
            return super.onCreateView(inflater, container, savedInstanceState)
        }
        if (!initializationOnce()) {
            baseView = null
        }
        if (baseView == null) {
            baseView = onCreateViewEx(inflater, container, savedInstanceState)
            firstViewCreated = true
        }
        return baseView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (firstViewCreated || !initializationOnce()) {
            afterSuperOnActivityCreated(savedInstanceState)
            initParam(savedInstanceState)
            initView(savedInstanceState)
            initViewObservable(savedInstanceState)
            firstViewCreated = false
        }
    }

    open fun enableView(): Boolean = true

    open fun initializationOnce(): Boolean = true

    open fun contentView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return null
    }

    open fun afterSuperOnActivityCreated(savedInstanceState: Bundle?) {}

    override fun initParam(savedInstanceState: Bundle?) {}

    override fun initView(savedInstanceState: Bundle?) {}

    override fun initViewObservable(savedInstanceState: Bundle?) {}

    private fun onCreateViewEx(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val contentView = contentView(inflater, container, savedInstanceState)
        return if (contentView != null) {
            contentView
        } else {
            val layoutId = layout()
            if (layoutId == 0) {
                throw AssertionError("no layout id")
            }
            inflater.inflate(layoutId, container, false)
        }
    }

}