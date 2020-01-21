package com.laotang.quickdev.aretrofit.interceptor;

import com.laotang.quickdev.aretrofit.Request;
import com.laotang.quickdev.aretrofit.RouterInfo;

import java.util.List;

public class ARetrofitReallyInterceptorChain implements ARetrofitInterceptor.Chain {
    private List<ARetrofitInterceptor> interceptors;
    private int index;

    public ARetrofitReallyInterceptorChain(List<ARetrofitInterceptor> interceptors, int index) {
        this.interceptors = interceptors;
        this.index = index;
    }

    @Override
    public RouterInfo proceed(Request request) {
        if (index >= interceptors.size()) throw new AssertionError("index >= interceptors.size()");
        ARetrofitInterceptor.Chain reallyInterceptorChain = new ARetrofitReallyInterceptorChain(interceptors, index + 1);
        ARetrofitInterceptor interceptor = interceptors.get(index);
        return interceptor.intercept(reallyInterceptorChain, request);
    }

    @Override
    public RouterInfo call(Request request) {
        RouterInfo routerInfo = new RouterInfo();
        routerInfo.setRelativeUrl(request.getRelativeUrl());
        routerInfo.setFlags(request.getFlags());
        routerInfo.setBundle(request.getExtras());
        routerInfo.setGreenChannel(request.isGreenChannel());
        routerInfo.setContextWeakReference(request.getContextWeakReference());
        return routerInfo;
    }
}
