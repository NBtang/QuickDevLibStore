package com.laotang.quickdev.aac

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.laotang.quickdevcore.utils.rootKodein
import org.kodein.di.Kodein
import java.lang.reflect.InvocationTargetException

class BaseViewModelFactory(
    private val application: Application,
    private val kodein: Kodein = rootKodein(),
    private val intent: Intent? = null
) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return reallyCreate(modelClass)
    }

    private fun <T : ViewModel?> createAndroidViewModel(modelClass: Class<T>): T {
        return modelClass.getConstructor(Application::class.java).newInstance(application)
    }

    private fun <T : ViewModel?> createBaseViewModel(modelClass: Class<T>): T {
        return modelClass.getConstructor(Application::class.java, Kodein::class.java)
            .newInstance(application, kodein)
    }

    private fun <T : ViewModel?> reallyCreate(modelClass: Class<T>): T {
        var viewModel: T? = null
        try {
            if (BaseViewModel::class.java.isAssignableFrom(modelClass)) {
                viewModel = createBaseViewModel(modelClass)
                (viewModel as BaseViewModel).setIntent(intent)
            } else if (AndroidViewModel::class.java.isAssignableFrom(modelClass)) {
                viewModel = createAndroidViewModel(modelClass)
            }
        } catch (e: NoSuchMethodException) {
            throw java.lang.RuntimeException("Cannot create an instance of $modelClass", e)
        } catch (e: IllegalAccessException) {
            throw java.lang.RuntimeException("Cannot create an instance of $modelClass", e)
        } catch (e: InstantiationException) {
            throw java.lang.RuntimeException("Cannot create an instance of $modelClass", e)
        } catch (e: InvocationTargetException) {
            throw java.lang.RuntimeException("Cannot create an instance of $modelClass", e)
        }
        if (viewModel == null) {
            viewModel = super.create(modelClass)
        }
        return viewModel!!
    }
}