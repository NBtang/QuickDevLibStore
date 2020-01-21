package com.laotang.quickdev.mvvm

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.laotang.quickdevcore.utils.obtainAppKodeinAware
import com.uber.autodispose.ScopeProvider
import com.uber.autodispose.android.lifecycle.scope
import org.kodein.di.Kodein
import org.kodein.di.android.retainedKodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import java.lang.reflect.ParameterizedType

abstract class BaseActivity<VM : BaseViewModel> : AppCompatActivity(), IActivity {

    protected lateinit var mViewModel: VM
    private val parentKodein: Kodein = obtainAppKodeinAware().kodein

    override val kodein: Kodein by retainedKodein {
        extend(parentKodein)
        /* activity specific bindings */
        if (this@BaseActivity.javaClass.isAnnotationPresent(Module::class.java)) {
            val module = this@BaseActivity.javaClass.getAnnotation(Module::class.java)!!
            val clazzArray = module.values
            clazzArray.iterator().forEach { clazz ->
                if (KodeinModuleProvider::class.java.isAssignableFrom(clazz.java)) {
                    val instance = clazz.java.newInstance() as KodeinModuleProvider
                    import(module = instance.providerModule(), allowOverride = true)
                }
            }
        }
        bind<FragmentActivity>() with instance(this@BaseActivity)
    }

    protected val scopeProvider: ScopeProvider by lazy {
        this.scope(Lifecycle.Event.ON_DESTROY)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = initViewModel()
        mViewModel.bindHostKodein(kodein)
        intent?.apply {
            mViewModel.setExtraDatas(extras)
        }
        mViewModel.bindLifecycle(this)
        initParam(savedInstanceState)
        val contentView = contentView()
        if (contentView != null) {
            setContentView(contentView)
        } else {
            val layoutId = layout()
            if (layoutId == 0) {
                throw AssertionError("layout resId ==0")
            }
            setContentView(layoutId)
        }
        initView(savedInstanceState)
        initViewObservable(savedInstanceState)
    }

    override fun initParam(savedInstanceState: Bundle?) {}

    override fun initView(savedInstanceState: Bundle?) {}

    open fun initViewObservable(savedInstanceState: Bundle?, viewModel: VM) {}

    override fun initViewObservable(savedInstanceState: Bundle?) {
        mViewModel.finishActivityLiveData.observe(this, Observer {
            finish()
        })
        initViewObservable(savedInstanceState, mViewModel)
    }

    open fun contentView(): View? {
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