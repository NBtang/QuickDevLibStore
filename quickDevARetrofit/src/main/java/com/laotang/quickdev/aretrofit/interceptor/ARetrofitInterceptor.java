package com.laotang.quickdev.aretrofit.interceptor;

import com.laotang.quickdev.aretrofit.Request;
import com.laotang.quickdev.aretrofit.RouterInfo;

public interface ARetrofitInterceptor {
    RouterInfo intercept(Chain chain, Request request);

    interface Chain {
        RouterInfo proceed(Request request);

        RouterInfo call(Request request);
    }
}
