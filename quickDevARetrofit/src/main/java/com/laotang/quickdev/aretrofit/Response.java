package com.laotang.quickdev.aretrofit;

public final class Response<T> {
    public static <T> Response<T> success(T body) {
        return new Response<>(body);
    }

    private final T body;

    private Response(T body) {
        this.body = body;
    }

    public T body() {
        return body;
    }
}
