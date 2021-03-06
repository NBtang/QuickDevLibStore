package com.laotang.quickdevcore.utils

import com.f2prateek.rx.preferences2.Preference
import com.google.gson.Gson
import org.kodein.di.generic.instance
import java.lang.reflect.Type

class RxPreferenceConverter<T>(private val type: Type) : Preference.Converter<T> {

    private val gson by rootKodein().instance<Gson>()

    override fun deserialize(serialized: String): T {
        return gson.fromJson(serialized, type)
    }

    override fun serialize(value: T): String {
        return gson.toJson(value)
    }
}