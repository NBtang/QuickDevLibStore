package com.laotang.quickdev.libstore.app.factory

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.laotang.quickdev.libstore.mvvm.vm.TestFragmentViewModel
import com.laotang.quickdev.libstore.mvvm.vm.UserViewModel
import com.laotang.quickdev.mvvm.BaseViewModel
import com.laotang.quickdev.mvvm.IBaseViewModel
import java.lang.reflect.InvocationTargetException

class CustomViewModelFactory(private val application: Application) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val viewModel: T
        if (TestFragmentViewModel::class.java == modelClass) {
            val userViewModel = create(UserViewModel::class.java)
            return try {
                val testFragmentViewModel =
                    modelClass.getConstructor(Application::class.java, UserViewModel::class.java)
                        .newInstance(application, userViewModel) as BaseViewModel
                bindExistedViewModel(testFragmentViewModel, userViewModel) as T
            } catch (e: Exception) {
                throw java.lang.RuntimeException("Cannot create an instance of $modelClass", e)
            }
        }
        viewModel = if (AndroidViewModel::class.java.isAssignableFrom(modelClass)) {
            try {
                modelClass.getConstructor(Application::class.java).newInstance(application)
            } catch (e: NoSuchMethodException) {
                throw java.lang.RuntimeException("Cannot create an instance of $modelClass", e)
            } catch (e: IllegalAccessException) {
                throw java.lang.RuntimeException("Cannot create an instance of $modelClass", e)
            } catch (e: InstantiationException) {
                throw java.lang.RuntimeException("Cannot create an instance of $modelClass", e)
            } catch (e: InvocationTargetException) {
                throw java.lang.RuntimeException("Cannot create an instance of $modelClass", e)
            }
        } else {
            super.create(modelClass)
        }
        return viewModel
    }

    private fun bindExistedViewModel(
        viewModel: BaseViewModel,
        existedViewModel: BaseViewModel
    ): BaseViewModel {
        viewModel.setExistedViewModelBinder(object : IBaseViewModel.OnExistedViewModelBind {
            override fun bind() {
                existedViewModel.setIntent(viewModel.mIntent)
                existedViewModel.bindLifecycle(viewModel)
            }
        })
        return viewModel
    }
}