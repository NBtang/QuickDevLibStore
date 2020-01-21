package com.laotang.quickdev.localretrofit;

import java.io.IOException;
import java.io.InputStream;

abstract class ParameterHandler<T> {
    abstract RawCall apply(T value) throws IOException;


    static final class File extends ParameterHandler<String> {

        @Override
        RawCall apply(String value) throws IOException {
            return new FileRawCall(value);
        }
    }

    static final class Raw extends ParameterHandler<Integer> {

        @Override
        RawCall apply(Integer value) throws IOException {
            return new RawResCall(value);
        }
    }

    static final class Assets extends ParameterHandler<String> {

        @Override
        RawCall apply(String value) throws IOException {
            return new AssetsRawCall(value);
        }
    }

    static final class Stream extends ParameterHandler<InputStream> {

        @Override
        RawCall apply(InputStream value) throws IOException {
            return new StreamRawCall(value);
        }
    }
}
