package com.laotang.quickdev.aroutercalladapter;

import com.laotang.quickdev.aretrofit.Call;
import com.laotang.quickdev.aretrofit.Response;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.CompositeException;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.plugins.RxJavaPlugins;

final class CallExecuteObservable<T> extends Observable<Response<T>> {
    private final Call<T> originalCall;

    CallExecuteObservable(Call<T> originalCall) {
        this.originalCall = originalCall;
    }

    @Override
    protected void subscribeActual(Observer<? super Response<T>> observer) {
        Call<T> call = originalCall.clone();
        CallDisposable disposable = new CallDisposable();
        observer.onSubscribe(disposable);

        boolean terminated = false;
        try {
            Response<T> response = call.execute();
            if (!disposable.isDisposed()) {
                observer.onNext(response);
            }
            if (!disposable.isDisposed()) {
                terminated = true;
                observer.onComplete();
            }
        } catch (Throwable t) {
            Exceptions.throwIfFatal(t);
            if (terminated) {
                RxJavaPlugins.onError(t);
            } else if (!disposable.isDisposed()) {
                try {
                    observer.onError(t);
                } catch (Throwable inner) {
                    Exceptions.throwIfFatal(inner);
                    RxJavaPlugins.onError(new CompositeException(t, inner));
                }
            }
        }
    }

    private static final class CallDisposable implements Disposable {
        private volatile boolean disposed;

        @Override public void dispose() {
            disposed = true;
        }

        @Override public boolean isDisposed() {
            return disposed;
        }
    }
}
