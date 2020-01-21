package com.laotang.quickdev.aretrofit;

public interface RawCall {
    Request execute();
    interface Factory {
        RawCall newCall(Request request);
    }
}
