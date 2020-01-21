package com.laotang.quickdevcore.integration.http.response

import com.laotang.quickdevcore.integration.http.response.entity.ResponseBean
import io.reactivex.ObservableTransformer

interface ResponseTransformerStrategy{
    fun <T> provideObservableTransformer():ObservableTransformer<ResponseBean<T>,T>
}