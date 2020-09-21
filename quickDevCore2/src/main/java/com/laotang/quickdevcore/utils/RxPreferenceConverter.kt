package com.laotang.quickdevcore.utils

import com.f2prateek.rx.preferences2.Preference
import com.google.gson.Gson
import java.lang.reflect.Type

class RxPreferenceConverter<T>(private val type: Type, private val gson: Gson) :
    Preference.Converter<T> {

    override fun deserialize(serialized: String): T {
        return gson.fromJson(serialized, type)
    }

    override fun serialize(value: T): String {
        return gson.toJson(value)
    }
}