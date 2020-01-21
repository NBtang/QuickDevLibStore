package com.laotang.quickdev.aretrofit;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

final class DefaultCallAdapterFactory extends CallAdapter.Factory{
    static final CallAdapter.Factory INSTANCE = new DefaultCallAdapterFactory();

    @Override
    public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, ARetrofit retrofit) {
        if (getRawType(returnType) != Call.class) {
            return null;
        }
        final Type responseType = Utils.getCallResponseType(returnType);
        if(responseType != RouterInfo.class){
            return null;
        }
        return new CallAdapter<Object, Call<?>>() {
            @Override public Type responseType() {
                return responseType;
            }

            @Override public Call<Object> adapt(Call<Object> call) {
                return call;
            }
        };
    }
}
