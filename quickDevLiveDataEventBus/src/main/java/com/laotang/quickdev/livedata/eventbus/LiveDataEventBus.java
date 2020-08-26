package com.laotang.quickdev.livedata.eventbus;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;

public class LiveDataEventBus {
    private LiveDataEventBus() {
    }

    public static <T> MutableLiveData<T> toLiveData(@NonNull LifecycleOwner owner, MutableLiveData<T> liveData) {
        owner.getLifecycle().addObserver(new LiveDataEventSubscribeDelegate<>(liveData));
        return liveData;
    }
}
