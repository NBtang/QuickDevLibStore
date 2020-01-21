package com.laotang.quickdev.localretrofit.calladapter;

import com.laotang.quickdev.localretrofit.CallAdapter;
import com.laotang.quickdev.localretrofit.LocalRetrofit;
import com.laotang.quickdev.localretrofit.Response;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;

public final class RxJava2CallAdapterFactory extends CallAdapter.Factory {

    public static RxJava2CallAdapterFactory create() {
        return new RxJava2CallAdapterFactory();
    }

    private RxJava2CallAdapterFactory() {
    }

    @Override
    public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, LocalRetrofit retrofit) {
        Class<?> rawType = getRawType(returnType);
        if (rawType == Completable.class) {
            // Completable is not parameterized (which is what the rest of this method deals with) so it
            // can only be created with a single configuration.
            return new RxJava2CallAdapter(Void.class, true, false, false,
                    false, true);
        }

        boolean isFlowable = rawType == Flowable.class;
        boolean isSingle = rawType == Single.class;
        boolean isMaybe = rawType == Maybe.class;
        if (rawType != Observable.class && !isFlowable && !isSingle && !isMaybe) {
            return null;
        }

        boolean isBody = false;
        Type responseType;
        if (!(returnType instanceof ParameterizedType)) {
            String name = isFlowable ? "Flowable"
                    : isSingle ? "Single"
                    : isMaybe ? "Maybe" : "Observable";
            throw new IllegalStateException(name + " return type must be parameterized"
                    + " as " + name + "<Foo> or " + name + "<? extends Foo>");
        }

        Type observableType = getParameterUpperBound(0, (ParameterizedType) returnType);
        Class<?> rawObservableType = getRawType(observableType);
        if (rawObservableType == Response.class) {
            if (!(observableType instanceof ParameterizedType)) {
                throw new IllegalStateException("Response must be parameterized"
                        + " as Response<Foo> or Response<? extends Foo>");
            }
            responseType = getParameterUpperBound(0, (ParameterizedType) observableType);
        } else {
            responseType = observableType;
            isBody = true;
        }
        return new RxJava2CallAdapter(responseType, isBody, isFlowable, isSingle, isMaybe, false);
    }
}
