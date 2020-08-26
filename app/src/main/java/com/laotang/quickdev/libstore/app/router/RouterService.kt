package com.laotang.quickdev.libstore.app.router

import android.content.Context
import androidx.fragment.app.Fragment
import com.laotang.quickdev.aretrofit.ARetrofit
import com.laotang.quickdev.aretrofit.router.From
import com.laotang.quickdev.aretrofit.router.Go
import com.laotang.quickdev.aretrofit.router.Url
import com.laotang.quickdev.rxactivity.ActivityResultEntity
import com.laotang.quickdevcore.utils.rootKodein
import io.reactivex.Observable
import org.kodein.di.generic.instance

interface ActivityRouterService {

    @Go(value = ActivityRouter.RouterSecondActivity)
    fun startSecondActivity(@From context: Context)

    @Go(value = ActivityRouter.RouterThirdActivity)
    fun startThirdActivity(@From context: Context): Observable<ActivityResultEntity>

}

interface FragmentRouterService {
    fun findFragment(@Url path: String): Fragment
}

object ActivityRouter : RouterServiceRouterImpl(), ActivityRouterService {

    const val RouterSecondActivity: String = "/app/SecondActivity"
    const val RouterThirdActivity: String = "/app/ThirdActivity"

    override fun startSecondActivity(context: Context) {
        getRouterService<ActivityRouterService>().startSecondActivity(context)
    }

    override fun startThirdActivity(context: Context): Observable<ActivityResultEntity> {
        return getRouterService<ActivityRouterService>().startThirdActivity(context)
    }


}

object FragmentRouter : RouterServiceRouterImpl(), FragmentRouterService {

    const val RouterTestFragment: String = "/app/TestFragment"
    const val RouterTestMVVMFragment: String = "/app/TestMVVMFragment"

    override fun findFragment(path: String): Fragment {
        return getRouterService<FragmentRouterService>().findFragment(path)
    }

}

abstract class RouterServiceRouterImpl {

    val mARetrofit: ARetrofit by rootKodein().instance()
    var service: Any? = null

    inline fun <reified T> getRouterService(): T {
        if (service == null) {
            service = mARetrofit.create(T::class.java)
        }
        return service!! as T
    }

}
