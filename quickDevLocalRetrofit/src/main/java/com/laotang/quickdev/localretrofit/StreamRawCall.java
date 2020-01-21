package com.laotang.quickdev.localretrofit;

import java.io.IOException;
import java.io.InputStream;

import static com.laotang.quickdev.localretrofit.Utils.readString;

final class StreamRawCall implements RawCall {
    private InputStream inputStream;

    StreamRawCall(InputStream inputStream){
        this.inputStream = inputStream;
    }

    @Override
    public String execute() throws IOException {
        return readString(inputStream);
    }
}
