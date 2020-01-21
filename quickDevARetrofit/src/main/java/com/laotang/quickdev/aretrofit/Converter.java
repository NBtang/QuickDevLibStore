package com.laotang.quickdev.aretrofit;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public interface Converter<F, T> {
    T convert(F value);

    abstract class Factory {
        public Converter<Request, ?> responseBodyConverter(Type type, Annotation[] annotations, ARetrofit retrofit) {
            return null;
        }

        public Converter<?, String> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, ARetrofit retrofit) {
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
