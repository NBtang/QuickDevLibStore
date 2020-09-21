package com.laotang.quickdevcore.utils

import com.f2prateek.rx.preferences2.Preference
import com.f2prateek.rx.preferences2.RxSharedPreferences
import com.google.gson.Gson
import org.kodein.di.generic.instance
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

object RxPreferences {
    private var rxPreferences: RxSharedPreferences? = null

    fun setBoolean(key: String, value: Boolean) {
        getBoolean(key).set(value)
    }

    fun setFloat(key: String, value: Float) {
        getFloat(key).set(value)
    }

    fun setInteger(key: String, value: Int) {
        getInteger(key).set(value)
    }

    fun setLong(key: String, value: Long) {
        getLong(key).set(value)
    }

    fun setString(key: String, value: String) {
        getString(key).set(value)
    }

    fun getBoolean(key: String, defaultValue: Boolean = false): Preference<Boolean> {
        return getRxPreferences().getBoolean(key, defaultValue)
    }

    fun getFloat(key: String, defaultValue: Float = 0f): Preference<Float> {
        return getRxPreferences().getFloat(key, defaultValue)
    }

    fun getInteger(key: String, defaultValue: Int = 0): Preference<Int> {
        return getRxPreferences().getInteger(key, defaultValue)
    }

    fun getLong(key: String, defaultValue: Long = 0L): Preference<Long> {
        return getRxPreferences().getLong(key, defaultValue)
    }

    fun getString(key: String, defaultValue: String = ""): Preference<String> {
        return getRxPreferences().getString(key, defaultValue)
    }

    fun getRxPreferences(): RxSharedPreferences {
        if (rxPreferences == null) {
            val rxPreferences by rootKodein().instance<RxSharedPreferences>()
            this.rxPreferences = rxPreferences
        }
        return this.rxPreferences!!
    }
}

inline fun <reified T> RxPreferences.setObject(key: String, value: T) {
    getObject<T>(key).set(value)
}

inline fun <reified T> RxPreferences.getObject(
    key: String,
    defaultValue: T? = null
): Preference<T> {
    val gson by rootKodein().instance<Gson>()
    val default = defaultValue ?: gson.fromJson<T>("{}", T::class.java)
    return getRxPreferences().getObject<T>(key, default, RxPreferenceConverter(T::class.java, gson))
}