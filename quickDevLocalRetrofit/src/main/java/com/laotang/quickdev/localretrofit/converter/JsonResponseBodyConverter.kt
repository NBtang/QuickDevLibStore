package com.laotang.quickdev.localretrofit.converter

import com.google.gson.Gson
import com.google.gson.JsonIOException
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonToken
import com.laotang.quickdev.localretrofit.Converter
import java.io.StringReader

class JsonResponseBodyConverter<T>(private val gson : Gson, private val adapter :TypeAdapter<T>) : Converter<String, T> {
    override fun convert(value: String): T {
        val jsonReader = gson.newJsonReader(StringReader(value))
        val result = adapter.read(jsonReader)
        if (jsonReader.peek() != JsonToken.END_DOCUMENT) {
            throw JsonIOException("JSON document was not fully consumed.")
        }
        return result
    }
}