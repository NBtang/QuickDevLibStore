package com.laotang.quickdev.localretrofit.converter

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.laotang.quickdev.localretrofit.Converter
import com.laotang.quickdev.localretrofit.LocalRetrofit
import java.lang.reflect.Type

class JsonConverterFactory(val gson: Gson): Converter.Factory(){

    override fun responseBodyConverter(type: Type, annotations: Array<Annotation>, retrofit: LocalRetrofit): Converter<String, *> {
        val adapter = gson.getAdapter(TypeToken.get(type))
        return JsonResponseBodyConverter(gson, adapter)
    }

    companion object {
        fun create(): JsonConverterFactory {
            return create(Gson())
        }

        fun create(gson: Gson): JsonConverterFactory {
            return JsonConverterFactory(gson)
        }
    }

}