package com.laotang.quickdev.aretrofit;

public interface Call<T> {
    Response<T> execute();
    Call<T> clone();
}
