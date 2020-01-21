package com.laotang.quickdev.aroutercalladapter;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.callback.NavigationCallback;
import com.laotang.quickdev.rxactivity.ActivityResultEntity;

import io.reactivex.Observable;

public abstract class RXNavigationCallback implements NavigationCallback {

    abstract public void onResultSubject(Observable<ActivityResultEntity> observable);

    @Override
    public void onFound(Postcard postcard) {

    }

    @Override
    public void onLost(Postcard postcard) {

    }

    @Override
    public void onArrival(Postcard postcard) {

    }

    @Override
    public void onInterrupt(Postcard postcard) {

    }
}
