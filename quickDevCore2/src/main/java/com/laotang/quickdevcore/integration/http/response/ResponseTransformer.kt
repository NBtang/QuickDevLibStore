package com.laotang.quickdevcore.integration.http.response

import com.laotang.quickdevcore.integration.http.ServerException
import com.laotang.quickdevcore.integration.http.response.entity.BaseResponseBean
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import io.reactivex.functions.Function

class ResponseTransformer<T> : ObservableTransformer<BaseResponseBean<T>, T> {

    override fun apply(upstream: Observable<BaseResponseBean<T>>): ObservableSource<T> {
        return upstream.flatMap { t ->
            if (t.success) {
                Observable.just(t.data)
            } else {
                Observable.error(ServerException(t.message, t.code))
            }
        }
    }
}