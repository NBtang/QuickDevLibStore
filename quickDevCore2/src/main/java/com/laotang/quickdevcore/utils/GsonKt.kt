package com.laotang.quickdevcore.utils

import com.google.gson.Gson
import org.kodein.di.generic.instance
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

inline fun <reified T> String.toObject(): T {
    val gson by rootKodein().instance<Gson>()
    return gson.fromJson(this, T::class.java)
}

fun Any.toJson(): String {
    val gson by rootKodein().instance<Gson>()
    return gson.toJson(this)
}

inline fun <reified T> List<T>.type(): Type {
    return object : ParameterizedType {
        override fun getRawType(): Type {
            return List::class.java
        }

        override fun getOwnerType(): Type? {
            return null
        }

        override fun getActualTypeArguments(): Array<Type> {
            return arrayOf(T::class.java)
        }

    }
}