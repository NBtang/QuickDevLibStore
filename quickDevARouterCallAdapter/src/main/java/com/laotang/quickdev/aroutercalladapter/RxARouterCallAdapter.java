package com.laotang.quickdev.aroutercalladapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;

import androidx.fragment.app.FragmentActivity;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.callback.NavigationCallback;
import com.alibaba.android.arouter.launcher.ARouter;
import com.laotang.quickdev.aretrofit.Call;
import com.laotang.quickdev.aretrofit.CallAdapter;
import com.laotang.quickdev.aretrofit.NavigationData;
import com.laotang.quickdev.aretrofit.Response;
import com.laotang.quickdev.aretrofit.RouterInfo;
import com.laotang.quickdev.rxactivity.ActivityResultEntity;
import com.laotang.quickdev.rxactivity.RxActivityResult;

import java.lang.reflect.Type;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;

final class RxARouterCallAdapter<R> implements CallAdapter<R, Object> {
    private final Type responseType;
    private final boolean isFlowable;
    private final boolean isSingle;
    private final boolean isMaybe;
    private final boolean isCompletable;
    private final boolean isPostcard;
    private final boolean isFragment;
    private final boolean isProvider;
    private final boolean isNavigation;
    private final boolean isNavigationForResult;
    private final boolean isNavigationExecute;

    RxARouterCallAdapter(Type responseType, boolean isFlowable, boolean isSingle, boolean isMaybe, boolean isCompletable,
                         boolean isPostcard, boolean isFragment, boolean isProvider, boolean isNavigation, boolean isNavigationForResult,
                         boolean isNavigationExecute) {
        this.responseType = responseType;
        this.isFlowable = isFlowable;
        this.isSingle = isSingle;
        this.isMaybe = isMaybe;
        this.isCompletable = isCompletable;
        this.isPostcard = isPostcard;
        this.isFragment = isFragment;
        this.isProvider = isProvider;
        this.isNavigation = isNavigation;
        this.isNavigationForResult = isNavigationForResult;
        this.isNavigationExecute = isNavigationExecute;
    }

    @Override
    public Type responseType() {
        return responseType;
    }

    @SuppressLint("CheckResult")
    @Override
    public Object adapt(Call<R> call) {
        Observable<Response<R>> responseObservable = new CallExecuteObservable<>(call);
        if (isPostcard) {
            RouterInfo routerInfo = (RouterInfo) responseObservable.blockingFirst().body();
            return buildPostCard(routerInfo);
        }
        Observable<?> observable = new BodyObservable<>(responseObservable);
        if(isFragment){
            RouterInfo routerInfo = (RouterInfo) responseObservable.blockingFirst().body();
            Postcard postcard = buildPostCard(routerInfo);
            return postcard.navigation();
        }
        if(isProvider){
            RouterInfo routerInfo = (RouterInfo) responseObservable.blockingFirst().body();
            return ARouter.getInstance().build(routerInfo.getRelativeUrl()).navigation();
        }
        if (isNavigation) {
            return Observable.create((ObservableOnSubscribe<NavigationData>) e -> {
                RouterInfo routerInfo = (RouterInfo) responseObservable.blockingFirst().body();
                Postcard postcard = buildPostCard(routerInfo);
                Context context = null;
                if(routerInfo.getContextWeakReference() !=null){
                    context = routerInfo.getContextWeakReference().get();
                }
                postcard.navigation(context, new NavigationCallback() {
                    @Override
                    public void onFound(Postcard postcard) {
                        e.onNext(new NavigationData(NavigationData.NAVIGATION_ON_FOUND));
                    }

                    @Override
                    public void onLost(Postcard postcard) {
                        e.onNext(new NavigationData(NavigationData.NAVIGATION_ON_LOST));
                        e.onComplete();
                    }

                    @Override
                    public void onArrival(Postcard postcard) {
                        e.onNext(new NavigationData(NavigationData.NAVIGATION_ON_ARRIVAL));
                        e.onComplete();
                    }

                    @Override
                    public void onInterrupt(Postcard postcard) {
                        e.onNext(new NavigationData(NavigationData.NAVIGATION_ON_INTERRUPT));
                        e.onComplete();
                    }
                });
            });
        }
        if (isNavigationForResult) {
            return Observable.create((ObservableOnSubscribe<ActivityResultEntity>) e -> {
                RouterInfo routerInfo = (RouterInfo) responseObservable.blockingFirst().body();
//                Postcard postcard = buildPostCard(routerInfo);
                Context context = null;
                if(routerInfo.getContextWeakReference() !=null){
                    context = routerInfo.getContextWeakReference().get();
                }
                if(context == null){
                    throw new RuntimeException("context is null");
                }
                if(!(context instanceof Activity)){
                    throw new RuntimeException("context is not instanceof Activity");
                }
                if(!(context instanceof FragmentActivity)){
                    throw new RuntimeException("context is not instanceof FragmentActivity");
                }
                Class<?> clazz;
                if (((FragmentActivity) context).getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    clazz = RxARouterLandscapeActivity.class;
                } else {
                    clazz = RxARouterPortraitActivity.class;
                }
                RxActivityResult rxActivityResult = new RxActivityResult((FragmentActivity)context);
                Intent intent = new Intent(context, clazz);
                intent.putExtra("relativeUrl",routerInfo.getRelativeUrl());
                intent.putExtra("bundle",routerInfo.getBundle());
                intent.putExtra("flags",routerInfo.getFlags());
                intent.putExtra("isGreenChannel",routerInfo.isGreenChannel());
                rxActivityResult.rxStartActivityForResult(intent).observeOn(AndroidSchedulers.mainThread()).subscribe(activityResultEntity -> {
                    if (!e.isDisposed()) {
                        e.onNext(activityResultEntity);
                        e.onComplete();
                    }
                });
            });
        }
        if (isNavigationExecute) {
            RouterInfo routerInfo = (RouterInfo) responseObservable.blockingFirst().body();
            Postcard postcard = buildPostCard(routerInfo);
            Context context = null;
            if (routerInfo.getContextWeakReference() != null) {
                context = routerInfo.getContextWeakReference().get();
            }
            return postcard.navigation(context);
        }
        if (isFlowable) {
            return observable.toFlowable(BackpressureStrategy.LATEST);
        }
        if (isSingle) {
            return observable.singleOrError();
        }
        if (isMaybe) {
            return observable.singleElement();
        }
        if (isCompletable) {
            return observable.ignoreElements();
        }
        return observable;
    }

    private Postcard buildPostCard(RouterInfo routerInfo){
        Postcard postcard = ARouter.getInstance()
                .build(routerInfo.getRelativeUrl())
                .with(routerInfo.getBundle())
                .withFlags(routerInfo.getFlags());
        if(routerInfo.isGreenChannel()){
            return postcard.greenChannel();
        }
        return postcard;
    }
}
