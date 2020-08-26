package com.laotang.quickdev.hermes.eventbus.help;

import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.laotang.quickdev.hermes.eventbus.HermesEventBus;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.subjects.PublishSubject;

public class HermesMultiProcessObservable implements DefaultLifecycleObserver {

    private final PublishSubject<Bundle> publishSubject;
    private final boolean sticky;

    public static HermesMultiProcessObservable create(@NonNull boolean sticky) {
        return new HermesMultiProcessObservable(sticky);
    }

    public static HermesMultiProcessObservable create() {
        return new HermesMultiProcessObservable(false);
    }

    private HermesMultiProcessObservable(boolean sticky) {
        this.publishSubject = PublishSubject.create();
        this.sticky = sticky;
    }

    @Override
    public void onCreate(@NonNull LifecycleOwner owner) {
        if (!HermesEventBus.getDefault().isRegistered(this)) {
            HermesEventBus.getDefault().register(this);
        }
        owner.getLifecycle().addObserver(this);
    }

    @Override
    public void onStart(@NonNull LifecycleOwner owner) {

    }

    @Override
    public void onResume(@NonNull LifecycleOwner owner) {

    }

    @Override
    public void onPause(@NonNull LifecycleOwner owner) {

    }

    @Override
    public void onStop(@NonNull LifecycleOwner owner) {

    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        publishSubject.onComplete();
        if (HermesEventBus.getDefault().isRegistered(this)) {
            HermesEventBus.getDefault().unregister(this);
        }
        owner.getLifecycle().removeObserver(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void hermesEventBusSubscribe(Bundle bundle) {
        boolean reallySticky = bundle.getBoolean("sticky", false);
        if (sticky && reallySticky) {
            //事件发送方式为sticky，且接受方式支持sticky，才通知订阅者
            publishSubject.onNext(bundle);
        } else if (sticky) {
            //事件发送方式为sticky，但是接受方式不支持sticky，不通知订阅者
            return;
        } else if (reallySticky) {
            //事件发送方式不是sticky，但是接受方式支持sticky，不通知订阅者
            return;
        } else {
            //事件发送方式不是sticky，接受方式也不支持sticky，通知订阅者
            publishSubject.onNext(bundle);
        }
    }

    public Observable<String> asStringObservable(@NonNull LifecycleOwner owner, @NonNull String tag) {
        owner.getLifecycle().addObserver(this);
        return publishSubject.filter(new Predicate<Bundle>() {
            @Override
            public boolean test(Bundle bundle) throws Exception {
                String tagExtra = bundle.getString("tag", "");
                if (TextUtils.isEmpty(tagExtra)) {
                    return false;
                }
                return tagExtra.equals(tag);
            }
        }).map(new Function<Bundle, String>() {
            @Override
            public String apply(Bundle bundle) throws Exception {
                return bundle.getString("event", "");
            }
        });
    }

    public Observable<Boolean> asBooleanObservable(@NonNull LifecycleOwner owner, @NonNull String tag) {
        owner.getLifecycle().addObserver(this);
        return publishSubject.filter(new Predicate<Bundle>() {
            @Override
            public boolean test(Bundle bundle) throws Exception {
                String tagExtra = bundle.getString("tag", "");
                if (TextUtils.isEmpty(tagExtra)) {
                    return false;
                }
                return tagExtra.equals(tag);
            }
        }).map(new Function<Bundle, Boolean>() {
            @Override
            public Boolean apply(Bundle bundle) throws Exception {
                return bundle.getBoolean("event", false);
            }
        });
    }

    public Observable<Integer> asIntegerObservable(@NonNull LifecycleOwner owner, @NonNull String tag) {
        owner.getLifecycle().addObserver(this);
        return publishSubject.filter(new Predicate<Bundle>() {
            @Override
            public boolean test(Bundle bundle) throws Exception {
                String tagExtra = bundle.getString("tag");
                if (TextUtils.isEmpty(tagExtra)) {
                    return false;
                }
                return tagExtra.equals(tag);
            }
        }).map(new Function<Bundle, Integer>() {
            @Override
            public Integer apply(Bundle bundle) throws Exception {
                return bundle.getInt("event", 0);
            }
        });
    }

    public <T> Observable<T> asObjectObservable(@NonNull LifecycleOwner owner, @NonNull String tag, @NonNull IConverter<T> converter) {
        owner.getLifecycle().addObserver(this);
        return publishSubject.filter(new Predicate<Bundle>() {
            @Override
            public boolean test(Bundle bundle) throws Exception {
                String tagExtra = bundle.getString("tag", "");
                if (TextUtils.isEmpty(tagExtra)) {
                    return false;
                }
                return tagExtra.equals(tag);
            }
        }).map(new Function<Bundle, T>() {
            @Override
            public T apply(Bundle bundle) throws Exception {
                Object eventObject = bundle.get("event");
                return new ConverterAdapter<T>(converter).get(eventObject);
            }
        });
    }
}
