package com.laotang.quickdev.localretrofit;

import java.io.IOException;

public interface Call<T> extends Cloneable {
    Response<T> execute() throws IOException;
    boolean isExecuted();
    void cancel();
    boolean isCanceled();
    Call<T> clone();
}
