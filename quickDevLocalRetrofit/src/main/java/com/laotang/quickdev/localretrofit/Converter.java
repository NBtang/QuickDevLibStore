package com.laotang.quickdev.localretrofit;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public interface Converter<F, T> {
    T convert(F value) throws IOException;

    abstract class Factory {
        public Converter<String, ?> responseBodyConverter(Type type, Annotation[] annotations, LocalRetrofit retrofit) {
            return null;
        }

        public Converter<?, String> stringConverter(Type type, Annotation[] annotations, LocalRetrofit retrofit) {
            return null;
        }

        protected static Type getParameterUpperBound(int index, ParameterizedType type) {
            return Utils.getParameterUpperBound(index, type);
        }

        protected static Class<?> getRawType(Type type) {
            return Utils.getRawType(type);
        }
    }
}
