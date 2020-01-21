package com.laotang.quickdev.localretrofit;

import java.io.IOException;
import java.io.InputStream;

import static com.laotang.quickdev.localretrofit.Utils.readString;

final class AssetsRawCall implements RawCall{
    private final String fileName;

    AssetsRawCall(String fileName){
        this.fileName = fileName;
    }

    @Override
    public String execute() throws IOException {
        InputStream in = InitProvider.getApplicationContext().getAssets().open(fileName);
        return readString(in);
    }
}
