package com.laotang.quickdev.localretrofit;

import static com.laotang.quickdev.localretrofit.Utils.checkNotNull;

public final class Response<T> {

    public static <T> Response<T> success(T body, String value) {
        checkNotNull(value, "value == null");
        return new Response<>(value, body);
    }

    private final String rawValue;
    private final T body;

    private Response(String value, T body) {
        this.rawValue = value;
        this.body = body;
    }

    public T body() {
        return body;
    }
}
