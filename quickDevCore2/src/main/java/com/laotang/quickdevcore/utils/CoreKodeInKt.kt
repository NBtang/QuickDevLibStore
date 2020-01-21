package com.laotang.quickdevcore.utils

import android.accounts.AccountManager
import android.app.ActivityManager
import android.app.AlarmManager
import android.app.Application
import android.app.NotificationManager
import android.app.usage.NetworkStatsManager
import android.content.ClipboardManager
import android.content.ContentResolver
import android.content.SharedPreferences
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.content.res.Resources
import android.hardware.SensorManager
import android.hardware.camera2.CameraManager
import android.hardware.display.DisplayManager
import android.hardware.input.InputManager
import android.location.LocationManager
import android.net.ConnectivityManager
import android.os.BatteryManager
import android.os.storage.StorageManager
import android.telephony.TelephonyManager
import android.view.LayoutInflater
import android.view.WindowManager
import android.view.accessibility.AccessibilityManager
import android.view.inputmethod.InputMethodManager
import com.f2prateek.rx.preferences2.RxSharedPreferences
import com.google.gson.Gson
import com.laotang.quickdev.localretrofit.LocalRetrofit
import com.laotang.quickdevcore.integration.IRepositoryManager
import com.laotang.quickdevcore.integration.http.response.ResponseTransformerStrategy
import com.laotang.quickdevcore.integration.http.utl.IRetrofitUrlManagerService
import com.laotang.quickdevcore.integration.imageloader.ImageLoader
import io.rx_cache2.internal.RxCache
import me.jessyan.rxerrorhandler.core.RxErrorHandler
import okhttp3.OkHttpClient
import org.kodein.di.generic.instance
import org.kodein.di.generic.on
import retrofit2.Retrofit
import java.io.File

interface KodeInProvider<T>{
    fun get():T
}

object CoreKodeInKt {
    val KApplication by obtainAppKodeinAware().instance<Application>()
    val KodeinOn = obtainAppKodeinAware().kodein.on(KApplication)

    //quick core begin
    val KGson by obtainAppKodeinAware().instance<Gson>()
    val KImageLoader by obtainAppKodeinAware().instance<ImageLoader>()
    val KRxCache by obtainAppKodeinAware().instance<RxCache>()
    val KLocalRetrofit by obtainAppKodeinAware().instance<LocalRetrofit>()
    val KRetrofit by obtainAppKodeinAware().instance<Retrofit>()
    val KOkHttpClient by obtainAppKodeinAware().instance<OkHttpClient>()
    val KIRepositoryManager by obtainAppKodeinAware().instance<IRepositoryManager>()
    val KIRetrofitUrlManagerService by obtainAppKodeinAware().instance<IRetrofitUrlManagerService>()
    val KRxErrorHandler by obtainAppKodeinAware().instance<RxErrorHandler>()
    val KRxSharedPreferences by obtainAppKodeinAware().instance<RxSharedPreferences>()
    val KCacheFile by obtainAppKodeinAware().instance<File>(tag = "cacheFile")

    val KResponseTransformerStrategy:KodeInProvider<ResponseTransformerStrategy> by lazy {
        object :KodeInProvider<ResponseTransformerStrategy>{
            override fun get(): ResponseTransformerStrategy {
                val instance by obtainAppKodeinAware().instance<ResponseTransformerStrategy>()
                return instance
            }
        }
    }
    //quick core end

    //app begin
    val KResources by KodeinOn.instance<Resources>()
    val KPackageManager by KodeinOn.instance<PackageManager>()
    val KApplicationInfo by KodeinOn.instance<ApplicationInfo>()
    val KAssetManager by KodeinOn.instance<AssetManager>()
    val KContentResolver by KodeinOn.instance<ContentResolver>()
    val KSharedPreferences:KodeInProvider<SharedPreferences> by lazy {
        object :KodeInProvider<SharedPreferences>{
            override fun get(): SharedPreferences {
                val instance by KodeinOn.instance<SharedPreferences>()
                return instance
            }
        }
    }
    //app end

    //system service begin
    val KLayoutInflater:KodeInProvider<LayoutInflater> by lazy {
        object :KodeInProvider<LayoutInflater>{
            override fun get(): LayoutInflater {
                val instance by KodeinOn.instance<LayoutInflater>()
                return instance
            }
        }
    }

    val KTelephonyManager:KodeInProvider<TelephonyManager> by lazy {
        object :KodeInProvider<TelephonyManager>{
            override fun get(): TelephonyManager {
                val instance by KodeinOn.instance<TelephonyManager>()
                return instance
            }
        }
    }

    val KInputManager:KodeInProvider<InputManager> by lazy {
        object :KodeInProvider<InputManager>{
            override fun get(): InputManager {
                val instance by KodeinOn.instance<InputManager>()
                return instance
            }
        }
    }

    val KLocationManager:KodeInProvider<LocationManager> by lazy {
        object :KodeInProvider<LocationManager>{
            override fun get(): LocationManager {
                val instance by KodeinOn.instance<LocationManager>()
                return instance
            }
        }
    }

    val KInputMethodManager:KodeInProvider<InputMethodManager> by lazy {
        object :KodeInProvider<InputMethodManager>{
            override fun get(): InputMethodManager {
                val instance by KodeinOn.instance<InputMethodManager>()
                return instance
            }
        }
    }

    val KActivityManager:KodeInProvider<ActivityManager> by lazy {
        object :KodeInProvider<ActivityManager>{
            override fun get(): ActivityManager {
                val instance by KodeinOn.instance<ActivityManager>()
                return instance
            }
        }
    }

    val KDisplayManager:KodeInProvider<DisplayManager> by lazy {
        object :KodeInProvider<DisplayManager>{
            override fun get(): DisplayManager {
                val instance by KodeinOn.instance<DisplayManager>()
                return instance
            }
        }
    }

    val KCameraManager:KodeInProvider<CameraManager> by lazy {
        object :KodeInProvider<CameraManager>{
            override fun get(): CameraManager {
                val instance by KodeinOn.instance<CameraManager>()
                return instance
            }
        }
    }

    val KNetworkStatsManager:KodeInProvider<NetworkStatsManager> by lazy {
        object :KodeInProvider<NetworkStatsManager>{
            override fun get(): NetworkStatsManager {
                val instance by KodeinOn.instance<NetworkStatsManager>()
                return instance
            }
        }
    }

    val KBatteryManager:KodeInProvider<BatteryManager> by lazy {
        object :KodeInProvider<BatteryManager>{
            override fun get(): BatteryManager {
                val instance by KodeinOn.instance<BatteryManager>()
                return instance
            }
        }
    }

    val KConnectivityManager:KodeInProvider<ConnectivityManager> by lazy {
        object :KodeInProvider<ConnectivityManager>{
            override fun get(): ConnectivityManager {
                val instance by KodeinOn.instance<ConnectivityManager>()
                return instance
            }
        }
    }

    val KNotificationManager:KodeInProvider<NotificationManager> by lazy {
        object :KodeInProvider<NotificationManager>{
            override fun get(): NotificationManager {
                val instance by KodeinOn.instance<NotificationManager>()
                return instance
            }
        }
    }

    val KWindowManager:KodeInProvider<WindowManager> by lazy {
        object :KodeInProvider<WindowManager>{
            override fun get(): WindowManager {
                val instance by KodeinOn.instance<WindowManager>()
                return instance
            }
        }
    }

    val KAlarmManager:KodeInProvider<AlarmManager> by lazy {
        object :KodeInProvider<AlarmManager>{
            override fun get(): AlarmManager {
                val instance by KodeinOn.instance<AlarmManager>()
                return instance
            }
        }
    }

    val KSensorManager:KodeInProvider<SensorManager> by lazy {
        object :KodeInProvider<SensorManager>{
            override fun get(): SensorManager {
                val instance by KodeinOn.instance<SensorManager>()
                return instance
            }
        }
    }

    val KStorageManager:KodeInProvider<StorageManager> by lazy {
        object :KodeInProvider<StorageManager>{
            override fun get(): StorageManager {
                val instance by KodeinOn.instance<StorageManager>()
                return instance
            }
        }
    }

    val KAccessibilityManager:KodeInProvider<AccessibilityManager> by lazy {
        object :KodeInProvider<AccessibilityManager>{
            override fun get(): AccessibilityManager {
                val instance by KodeinOn.instance<AccessibilityManager>()
                return instance
            }
        }
    }

    val KClipboardManager:KodeInProvider<ClipboardManager> by lazy {
        object :KodeInProvider<ClipboardManager>{
            override fun get(): ClipboardManager {
                val instance by KodeinOn.instance<ClipboardManager>()
                return instance
            }
        }
    }

    val KAccountManager:KodeInProvider<AccountManager> by lazy {
        object :KodeInProvider<AccountManager>{
            override fun get(): AccountManager {
                val instance by KodeinOn.instance<AccountManager>()
                return instance
            }
        }
    }
    //system service end
}
