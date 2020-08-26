package com.laotang.quickdev.livedata.eventbus;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class LiveDataEventSubscribeDelegate<T> implements DefaultLifecycleObserver {
    MutableLiveData<T> liveData;

    public LiveDataEventSubscribeDelegate(MutableLiveData<T> liveData) {
        this.liveData = liveData;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void LiveDataEventSubscribe(T event) {
        liveData.setValue(event);
    }

    @Override
    public void onCreate(@NonNull LifecycleOwner owner) {
        if (EventBus.getDefault().isRegistered(this)) {
            return;
        }
        EventBus.getDefault().register(this);
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
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        owner.getLifecycle().removeObserver(this);
    }
}
