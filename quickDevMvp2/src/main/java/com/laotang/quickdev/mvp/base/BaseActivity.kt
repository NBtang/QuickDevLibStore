package com.laotang.quickdev.mvp.base

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelStoreOwner
import com.laotang.quickdevcore.utils.rootKodein
import com.uber.autodispose.ScopeProvider
import com.uber.autodispose.android.lifecycle.scope
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance

abstract class BaseActivity : AppCompatActivity(), IActivity {

    override val kodein: Kodein = Kodein.lazy {
        val parentKodein: Kodein = rootKodein()
        extend(parentKodein)
        /* activity specific bindings */
        if (this@BaseActivity.javaClass.isAnnotationPresent(Module::class.java)) {
            val module = this@BaseActivity.javaClass.getAnnotation(Module::class.java)
            val clazzArray = module.values
            clazzArray.iterator().forEach { clazz ->
                if (KodeinModuleProvider::class.java.isAssignableFrom(clazz.java)) {
                    val instance = clazz.java.newInstance() as KodeinModuleProvider
                    import(module = instance.providerModule(), allowOverride = true)
                }
            }
        }
        bind<BaseActivity>() with instance(this@BaseActivity)
    }

    protected val scopeProvider: ScopeProvider by lazy {
        this.scope(Lifecycle.Event.ON_DESTROY)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        initPresenter(savedInstanceState, intent)
    }

    override fun initParam(savedInstanceState: Bundle?) {}

    override fun initView(savedInstanceState: Bundle?) {}

    override fun initViewObservable(savedInstanceState: Bundle?) {}

    override fun initPresenter(savedInstanceState: Bundle?, intent: Intent) {}

    open fun contentView(): View? {
        return null
    }

    open fun <P : IPresenter<*>> providePresenter(
        clazz: Class<P>,
        viewModelStoreOwner: ViewModelStoreOwner? = null,
        factory: PresenterProviders.Factory? = null
    ): P {
        return PresenterProviders.of(viewModelStoreOwner ?: this, factory).get(clazz)
    }
}