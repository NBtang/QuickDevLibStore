package com.laotang.quickdev.localretrofit.calladapter;

import com.laotang.quickdev.localretrofit.Call;
import com.laotang.quickdev.localretrofit.Response;

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
        CallDisposable disposable = new CallDisposable(call);
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
        private final Call<?> call;
        private volatile boolean disposed;

        CallDisposable(Call<?> call) {
            this.call = call;
        }

        @Override public void dispose() {
            disposed = true;
            call.cancel();
        }

        @Override public boolean isDisposed() {
            return disposed;
        }
    }
}
