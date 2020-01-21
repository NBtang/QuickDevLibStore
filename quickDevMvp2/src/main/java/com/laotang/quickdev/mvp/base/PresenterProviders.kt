package com.laotang.quickdev.mvp.base

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders


class PresenterProviders private constructor(private val factory:Factory, private val viewModel: PresenterViewModel) {

    fun <T : BasePresenter<*>> get(modelClass: Class<T>): T {
        val canonicalName = modelClass.canonicalName
            ?: throw IllegalArgumentException("Local and anonymous classes can not be BasePresenters")
        return get("$DEFAULT_KEY:$canonicalName", modelClass)
    }

    operator fun <T : BasePresenter<*>> get(key: String, modelClass: Class<T>): T {
        var presenter: BasePresenter<*>? = viewModel.get(key)
        if(presenter!=null){
            if (modelClass.isInstance(presenter)) {
                presenter.bindLifecycle(viewModel)
                return presenter as T
            } else {
                throw AssertionError("modelClass isInstance BasePresenter  false")
            }
        }
        presenter = factory.create(modelClass)
        viewModel.put(key, presenter)
        presenter.bindLifecycle(viewModel)
        return presenter
    }

    companion object {
        private const val DEFAULT_KEY = "com.laotang.quickdev.mvp.base.PresenterProviders.DefaultKey"

        fun of(activity: FragmentActivity, factory: Factory?=null): PresenterProviders {
            val reallyFactory = factory?:NewInstanceFactory.getInstance()
            val viewModel = ViewModelProviders.of(activity).get(PresenterViewModel::class.java)
            return PresenterProviders(reallyFactory,viewModel)
        }

        fun of(fragment: Fragment, factory: Factory?=null): PresenterProviders {
            val reallyFactory = factory?:NewInstanceFactory.getInstance()
            val viewModel = ViewModelProviders.of(fragment).get(PresenterViewModel::class.java)
            return PresenterProviders(reallyFactory,viewModel)
        }
    }

    class NewInstanceFactory private constructor():Factory{

        override fun <T : BasePresenter<*>> create(modelClass: Class<T>): T {
            try {
                return modelClass.newInstance()
            } catch (e: InstantiationException) {
                throw RuntimeException("Cannot create an instance of $modelClass", e)
            } catch (e: IllegalAccessException) {
                throw RuntimeException("Cannot create an instance of $modelClass", e)
            }
        }

        companion object {
            private var mFactory:Factory? = null

            fun getInstance():Factory{
                if(mFactory == null){
                    mFactory = NewInstanceFactory()
                }
                return mFactory!!
            }
        }
    }
    interface Factory{
        fun <T : BasePresenter<*>> create(modelClass: Class<T>): T
    }
}