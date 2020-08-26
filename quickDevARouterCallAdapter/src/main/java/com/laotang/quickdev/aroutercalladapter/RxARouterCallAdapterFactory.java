package com.laotang.quickdev.aroutercalladapter;


import androidx.fragment.app.Fragment;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.template.IProvider;
import com.laotang.quickdev.aretrofit.ARetrofit;
import com.laotang.quickdev.aretrofit.CallAdapter;
import com.laotang.quickdev.aretrofit.NavigationData;
import com.laotang.quickdev.aretrofit.RouterInfo;
import com.laotang.quickdev.rxactivity.ActivityResultEntity;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;

public class RxARouterCallAdapterFactory extends CallAdapter.Factory {

    public static RxARouterCallAdapterFactory create() {
        return new RxARouterCallAdapterFactory();
    }

    private RxARouterCallAdapterFactory() {

    }

    @Override
    public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, ARetrofit retrofit) {
        Class<?> rawType = getRawType(returnType);
        if (rawType == Postcard.class) {
            return new RxARouterCallAdapter(Void.class, false, false, false, false,
                    true, false,false,false, false,false);
        } else if (rawType == Completable.class) {
            return new RxARouterCallAdapter(Void.class, false, false, false, true,
                    false, false,false,false, false,false);
        } else if (rawType == Fragment.class){
            return new RxARouterCallAdapter(Void.class, false, false, false, false,
                    false, true,false,false, false,false);
        } else if (IProvider.class.isAssignableFrom(rawType)) {
            return new RxARouterCallAdapter(Void.class, false, false, false, false,
                    false, false, true, false, false,false);
        } else if (rawType == void.class || rawType == Void.class) {
            return new RxARouterCallAdapter(Void.class, false, false, false, false,
                    false, false, false, false, false,true);
        }
        boolean isFlowable = rawType == Flowable.class;
        boolean isSingle = rawType == Single.class;
        boolean isMaybe = rawType == Maybe.class;
        if (rawType != Observable.class && !isFlowable && !isSingle && !isMaybe) {
            return null;
        }
        boolean isNavigationForResult = false;
        boolean isNavigation = false;
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

        if (rawObservableType == RouterInfo.class) {
            responseType = observableType;
        } else if (rawObservableType == ActivityResultEntity.class) {
            responseType = observableType;
            isNavigationForResult = true;
        } else if (rawObservableType == NavigationData.class) {
            responseType = observableType;
            isNavigation = true;
        } else {
            responseType = null;
        }
        if (responseType == null) {
            return null;
        }
        return new RxARouterCallAdapter(responseType, isFlowable, isSingle, isMaybe, false, false,
                false,false,isNavigation, isNavigationForResult,false);
    }
}
