package com.laotang.quickdev.rxactivity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;

public class RxActivityResult {
    static final String TAG = RxActivityResult.class.getSimpleName();
    static final Object TRIGGER = new Object();
    RxActivityResult.Lazy<RxReportFragment> mRxReportFragment;

    public RxActivityResult(@NonNull FragmentActivity activity) {
        this.mRxReportFragment = this.getLazySingleton(activity.getSupportFragmentManager());
    }

    public RxActivityResult(@NonNull Fragment fragment) {
        this.mRxReportFragment = this.getLazySingleton(fragment.getChildFragmentManager());
    }

    @NonNull
    private RxActivityResult.Lazy<RxReportFragment> getLazySingleton(@NonNull final FragmentManager fragmentManager) {
        return new RxActivityResult.Lazy<RxReportFragment>() {
            private RxReportFragment rxReportFragment;

            public synchronized RxReportFragment get() {
                if (this.rxReportFragment == null) {
                    this.rxReportFragment = RxActivityResult.this.getRxReportFragment(fragmentManager);
                }

                return this.rxReportFragment;
            }
        };
    }

    private RxReportFragment getRxReportFragment(@NonNull FragmentManager fragmentManager) {
        RxReportFragment rxReportFragment = this.findRxReportFragment(fragmentManager);
        boolean isNewInstance = rxReportFragment == null;
        if (isNewInstance) {
            rxReportFragment = new RxReportFragment();
            fragmentManager.beginTransaction().add(rxReportFragment, TAG).commitNow();
        }

        return rxReportFragment;
    }

    private RxReportFragment findRxReportFragment(@NonNull FragmentManager fragmentManager) {
        return (RxReportFragment)fragmentManager.findFragmentByTag(TAG);
    }

    public Observable<ActivityResultEntity> rxStartActivityForResult(Intent intent) {
        return Observable.just(TRIGGER).compose(this.ensure(intent,null));
    }

    public Observable<ActivityResultEntity> rxStartActivityForResult(Intent intent, @Nullable Bundle options) {
        return Observable.just(TRIGGER).compose(this.ensure(intent,options));
    }

    private  <T> ObservableTransformer<T, ActivityResultEntity> ensure(Intent intent, @Nullable Bundle options) {
        return upstream -> navigation(intent,options);
    }

    private Observable<ActivityResultEntity> navigation(Intent intent,@Nullable Bundle options) {
        return ((RxReportFragment)this.mRxReportFragment.get()).navigation(intent,options);
    }

    @FunctionalInterface
    public interface Lazy<V> {
        V get();
    }
}
