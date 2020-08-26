package com.laotang.quickdev.mvvm

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.laotang.quickdev.mvvm.channel.IMethodChannelProvider
import com.laotang.quickdev.mvvm.channel.MethodChannel
import com.laotang.quickdevcore.utils.rootKodein
import com.uber.autodispose.ScopeProvider
import com.uber.autodispose.android.lifecycle.scope
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import java.lang.AssertionError

abstract class BaseSimpleFragment : Fragment(), IFragment, IMethodChannelProvider {
    protected var mContext: Context? = null
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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onDetach() {
        super.onDetach()
        mContext = null
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
        if (baseView == null) {
            val contentView = contentView(inflater, container, savedInstanceState)
            baseView = if (contentView != null) {
                contentView
            } else {
                val layoutId = layout()
                if (layoutId == 0) {
                    throw AssertionError("no layout id")
                }
                inflater.inflate(layoutId, container, false)
            }
            firstViewCreated = true
        }
        return baseView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        afterSuperOnActivityCreated(savedInstanceState)
        if (firstViewCreated) {
            initParam(savedInstanceState)
            initView(savedInstanceState,baseView)
            initViewObservable(savedInstanceState,baseView)
            firstViewCreated = false
        }
    }

    override fun initParam(savedInstanceState: Bundle?) {}

    override fun initView(savedInstanceState: Bundle?, contentView: View?) {}

    override fun initViewObservable(savedInstanceState: Bundle?, contentView: View?) {}

    open fun enableView(): Boolean = true

    open fun contentView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return null
    }

    open fun afterSuperOnActivityCreated(savedInstanceState: Bundle?) {}

    override fun getMethodChannel(): MethodChannel? {
        val instance = requireActivity()
        if (instance is IMethodChannelProvider) {
            return instance.getMethodChannel()
        }
        return null
    }
}