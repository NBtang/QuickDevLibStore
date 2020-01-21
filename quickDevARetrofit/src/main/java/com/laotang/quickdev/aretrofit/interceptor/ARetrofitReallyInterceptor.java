package com.laotang.quickdev.aretrofit.interceptor;

import com.laotang.quickdev.aretrofit.Request;
import com.laotang.quickdev.aretrofit.RouterInfo;

public class ARetrofitReallyInterceptor implements ARetrofitInterceptor {
    @Override
    public RouterInfo intercept(Chain chain, Request request) {
        return chain.call(request);
    }
}
