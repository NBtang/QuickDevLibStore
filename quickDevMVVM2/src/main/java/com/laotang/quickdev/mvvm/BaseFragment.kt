package com.laotang.quickdev.mvvm

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.laotang.quickdevcore.utils.obtainAppKodeinAware
import com.uber.autodispose.ScopeProvider
import com.uber.autodispose.android.lifecycle.scope
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import java.lang.AssertionError
import java.lang.reflect.ParameterizedType

abstract class BaseFragment<VM : BaseViewModel> : Fragment(), IFragment {
    protected var mContext: Context? = null
    private var baseView: View? = null
    private var firstViewCreated = false
    protected lateinit var mViewModel: VM
    private val parentKodein: Kodein = obtainAppKodeinAware().kodein

    override val kodein: Kodein = Kodein {
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
        bind<Fragment>() with instance(this@BaseFragment)
        bind<FragmentActivity>() with provider {
            this@BaseFragment.requireActivity()
        }
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
        mViewModel = initViewModel()
        mViewModel.bindHostKodein(kodein)
        arguments?.apply {
            mViewModel.setExtraDatas(this)
        }
        mViewModel.bindLifecycle(this)
        if (firstViewCreated) {
            initParam(savedInstanceState)
            initView(savedInstanceState)
            initViewObservable(savedInstanceState)
            firstViewCreated = false
        }
    }

    override fun initParam(savedInstanceState: Bundle?) {}

    override fun initView(savedInstanceState: Bundle?) {}

    open fun initViewObservable(savedInstanceState: Bundle?, viewModel: VM) {}

    override fun initViewObservable(savedInstanceState: Bundle?) {
        mViewModel.finishActivityLiveData.observe(this, Observer {
            finishHostActivity()
        })
        initViewObservable(savedInstanceState, mViewModel)
    }

    fun finishHostActivity() {
        requireActivity().finish()
    }

    open fun enableView(): Boolean = true

    open fun contentView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return null
    }

    protected open fun initViewModel(): VM {
        val genericSuperclass = javaClass.genericSuperclass
        return if (genericSuperclass is ParameterizedType) {
            val actualTypeArguments = genericSuperclass.actualTypeArguments
            val genericClass = actualTypeArguments.singleOrNull {
                try {
                    AndroidViewModel::class.java.isAssignableFrom(it as Class<VM>)
                } catch (e: Exception) {
                    e.printStackTrace()
                    false
                }
            }
            ViewModelProviders.of(this).get(genericClass as Class<VM>)
        } else {
            ViewModelProviders.of(this).get(BaseViewModel::class.java) as VM
        }
    }
}