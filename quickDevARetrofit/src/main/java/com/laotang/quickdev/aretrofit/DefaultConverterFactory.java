package com.laotang.quickdev.aretrofit;


import com.laotang.quickdev.aretrofit.interceptor.ARetrofitInterceptor;
import com.laotang.quickdev.aretrofit.interceptor.ARetrofitReallyInterceptor;
import com.laotang.quickdev.aretrofit.interceptor.ARetrofitReallyInterceptorChain;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DefaultConverterFactory extends Converter.Factory {
    @Override
    public Converter<Request, ?> responseBodyConverter(Type type, Annotation[] annotations, ARetrofit retrofit) {
        return new DefaultResponseBodyConverter(retrofit.interceptors);
    }

    public class DefaultResponseBodyConverter implements Converter<Request, RouterInfo> {
        private ARetrofitReallyInterceptorChain mARetrofitReallyInterceptorChain;

        DefaultResponseBodyConverter(List<ARetrofitInterceptor> aRetrofitInterceptorList) {
            List<ARetrofitInterceptor> mARetrofitInterceptorList = new ArrayList<>(aRetrofitInterceptorList);
            mARetrofitInterceptorList.add(new ARetrofitReallyInterceptor());
            mARetrofitReallyInterceptorChain = new ARetrofitReallyInterceptorChain(mARetrofitInterceptorList, 0);
        }

        @Override
        public RouterInfo convert(Request value) {
            return mARetrofitReallyInterceptorChain.proceed(value);
        }
    }
}
