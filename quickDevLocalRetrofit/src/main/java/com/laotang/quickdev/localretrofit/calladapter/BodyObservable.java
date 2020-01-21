package com.laotang.quickdev.localretrofit.calladapter;

import com.laotang.quickdev.localretrofit.Response;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

final class BodyObservable<T> extends Observable<T> {

    private final Observable<Response<T>> upstream;

    BodyObservable(Observable<Response<T>> upstream) {
        this.upstream = upstream;
    }

    @Override
    protected void subscribeActual(Observer<? super T> observer) {
        upstream.subscribe(new BodyObserver<T>(observer));
    }

    private static class BodyObserver<R> implements Observer<Response<R>> {

        private final Observer<? super R> observer;

        BodyObserver(Observer<? super R> observer) {
            this.observer = observer;
        }

        @Override
        public void onSubscribe(Disposable disposable) {
            observer.onSubscribe(disposable);
        }

        @Override
        public void onNext(Response<R> response) {
            observer.onNext(response.body());
        }

        @Override
        public void onError(Throwable throwable) {
            observer.onError(throwable);
        }

        @Override
        public void onComplete() {
            observer.onComplete();
        }
    }
}
