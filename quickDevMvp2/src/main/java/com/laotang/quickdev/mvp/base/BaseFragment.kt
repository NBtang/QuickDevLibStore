package com.laotang.quickdev.mvp.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelStoreOwner
import com.laotang.quickdevcore.utils.rootKodein
import com.uber.autodispose.ScopeProvider
import com.uber.autodispose.android.lifecycle.scope
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import java.lang.AssertionError

abstract class BaseFragment : Fragment(), IFragment {
    protected var mContext: Context? = null
    private var baseView: View? = null
    private var firstViewCreated = false

    override val kodein: Kodein = Kodein.lazy {
        val parentKodein: Kodein = rootKodein()
        extend(parentKodein)
        if (this@BaseFragment.javaClass.isAnnotationPresent(Module::class.java)) {
            val module = this@BaseFragment.javaClass.getAnnotation(Module::class.java)
            val clazzArray = module.values
            clazzArray.iterator().forEach { clazz ->
                if (KodeinModuleProvider::class.java.isAssignableFrom(clazz.java)) {
                    val instance = clazz.java.newInstance() as KodeinModuleProvider
                    import(module = instance.providerModule(), allowOverride = true)
                }
            }
        }
        bind<BaseFragment>() with instance(this@BaseFragment)
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
            return super.onCreateView(inflater, container, savedInstanceState)
        }
        if (baseView == null) {
            val layoutId = layout()
            if (layoutId == 0) {
                throw AssertionError("no layout id")
            }
            baseView = inflater.inflate(layoutId, container, false)
            firstViewCreated = true
        }
        return baseView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (firstViewCreated) {
            initParam(savedInstanceState)
            initView(savedInstanceState)
            initViewObservable(savedInstanceState)
            initPresenter(savedInstanceState, arguments)
            firstViewCreated = false
        }
    }

    override fun initParam(savedInstanceState: Bundle?) {}

    override fun initView(savedInstanceState: Bundle?) {}

    override fun initViewObservable(savedInstanceState: Bundle?) {}

    override fun initPresenter(savedInstanceState: Bundle?, arg: Bundle?) {}

    protected fun finishHostActivity() {
        requireActivity().finish()
    }

    open fun enableView(): Boolean = true

    open fun <P : IPresenter<*>> providePresenter(
        clazz: Class<P>,
        viewModelStoreOwner: ViewModelStoreOwner? = null,
        factory: PresenterProviders.Factory? = null
    ): P {
        return PresenterProviders.of(viewModelStoreOwner ?: this, factory).get(clazz)
    }
}