package com.laotang.quickdev.aretrofit;


import androidx.annotation.Nullable;

import com.laotang.quickdev.aretrofit.interceptor.ARetrofitInterceptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.laotang.quickdev.aretrofit.Utils.checkNotNull;
import static java.util.Collections.unmodifiableList;

public final class ARetrofit {
    private final Map<Method, ServiceMethod<?, ?>> serviceMethodCache = new ConcurrentHashMap<>();

    final List<CallAdapter.Factory> callAdapterFactories;
    final List<Converter.Factory> converterFactories;
    final Converter.Factory routerConverterFactory;
    final RawCall.Factory callFactory;
    final List<ARetrofitInterceptor> interceptors;

    ARetrofit(RawCall.Factory callFactory, Converter.Factory routerConverterFactory,
              List<Converter.Factory> converterFactories, List<CallAdapter.Factory> callAdapterFactories,
              List<ARetrofitInterceptor> interceptors
    ) {
        this.callFactory = callFactory;
        this.routerConverterFactory = routerConverterFactory;
        this.converterFactories = converterFactories; // Copy+unmodifiable at call site.
        this.callAdapterFactories = callAdapterFactories; // Copy+unmodifiable at call site.
        this.interceptors = interceptors; // Copy+unmodifiable at call site.
    }

    public List<CallAdapter.Factory> callAdapterFactories() {
        return this.callAdapterFactories;
    }

    public List<ARetrofitInterceptor> callInterceptors() {
        return this.interceptors;
    }

    @SuppressWarnings("unchecked")
    public <T> T create(final Class<T> service) {
        Utils.validateServiceInterface(service);
        return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class<?>[]{service},
                new InvocationHandler() {

                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args)
                            throws Throwable {
                        // If the method is a method from Object then defer to normal invocation.
                        if (method.getDeclaringClass() == Object.class) {
                            return method.invoke(this, args);
                        }
                        ServiceMethod<Object, Object> serviceMethod =
                                (ServiceMethod<Object, Object>) loadServiceMethod(method);
                        ServiceCall<Object> serviceCall = new ServiceCall<>(serviceMethod, args);
                        return serviceMethod.adapt(serviceCall);
                    }
                });
    }

    ServiceMethod<?, ?> loadServiceMethod(Method method) {
        ServiceMethod<?, ?> result = serviceMethodCache.get(method);
        if (result != null) return result;

        synchronized (serviceMethodCache) {
            result = serviceMethodCache.get(method);
            if (result == null) {
                result = new ServiceMethod.Builder<>(this, method).build();
                serviceMethodCache.put(method, result);
            }
        }
        return result;
    }

    public CallAdapter<?, ?> callAdapter(Type returnType, Annotation[] annotations) {
        return nextCallAdapter(null, returnType, annotations);
    }

    public CallAdapter<?, ?> nextCallAdapter(CallAdapter.Factory skipPast, Type returnType,
                                             Annotation[] annotations) {
        checkNotNull(returnType, "returnType == null");
        checkNotNull(annotations, "annotations == null");

        int start = callAdapterFactories.indexOf(skipPast) + 1;
        for (int i = start, count = callAdapterFactories.size(); i < count; i++) {
            CallAdapter<?, ?> adapter = callAdapterFactories.get(i).get(returnType, annotations, this);
            if (adapter != null) {
                return adapter;
            }
        }
        StringBuilder builder = new StringBuilder("Could not locate call adapter for ")
                .append(returnType)
                .append(".\n");
        if (skipPast != null) {
            builder.append("  Skipped:");
            for (int i = 0; i < start; i++) {
                builder.append("\n   * ").append(callAdapterFactories.get(i).getClass().getName());
            }
            builder.append('\n');
        }
        builder.append("  Tried:");
        for (int i = start, count = callAdapterFactories.size(); i < count; i++) {
            builder.append("\n   * ").append(callAdapterFactories.get(i).getClass().getName());
        }
        throw new IllegalArgumentException(builder.toString());
    }

    public <T> Converter<T, String> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations) {
        return nextRequestBodyConverter(null, type, parameterAnnotations, methodAnnotations);
    }

    public <T> Converter<T, String> nextRequestBodyConverter(
            @Nullable Converter.Factory skipPast, Type type, Annotation[] parameterAnnotations,
            Annotation[] methodAnnotations) {
        checkNotNull(type, "type == null");
        checkNotNull(parameterAnnotations, "parameterAnnotations == null");
        checkNotNull(methodAnnotations, "methodAnnotations == null");

        int start = converterFactories.indexOf(skipPast) + 1;
        for (int i = start, count = converterFactories.size(); i < count; i++) {
            Converter.Factory factory = converterFactories.get(i);
            Converter<?, String> converter =
                    factory.requestBodyConverter(type, parameterAnnotations, methodAnnotations, this);
            if (converter != null) {
                //noinspection unchecked
                return (Converter<T, String>) converter;
            }
        }

        StringBuilder builder = new StringBuilder("Could not locate RequestBody converter for ")
                .append(type)
                .append(".\n");
        if (skipPast != null) {
            builder.append("  Skipped:");
            for (int i = 0; i < start; i++) {
                builder.append("\n   * ").append(converterFactories.get(i).getClass().getName());
            }
            builder.append('\n');
        }
        builder.append("  Tried:");
        for (int i = start, count = converterFactories.size(); i < count; i++) {
            builder.append("\n   * ").append(converterFactories.get(i).getClass().getName());
        }
        throw new IllegalArgumentException(builder.toString());
    }

    public <T> Converter<Request, T> responseBodyConverter(Type type, Annotation[] annotations) {
        return nextResponseBodyConverter(null, type, annotations);
    }

    public <T> Converter<Request, T> routerConverter(Type type, Annotation[] annotations) {
        return (Converter<Request, T>) routerConverterFactory.responseBodyConverter(type, annotations, this);
    }

    public <T> Converter<Request, T> nextResponseBodyConverter(
            Converter.Factory skipPast, Type type, Annotation[] annotations) {
        checkNotNull(type, "type == null");
        checkNotNull(annotations, "annotations == null");

        int start = converterFactories.indexOf(skipPast) + 1;
        for (int i = start, count = converterFactories.size(); i < count; i++) {
            Converter<Request, ?> converter =
                    converterFactories.get(i).responseBodyConverter(type, annotations, this);
            if (converter != null) {
                //noinspection unchecked
                return (Converter<Request, T>) converter;
            }
        }

        StringBuilder builder = new StringBuilder("Could not locate ResponseBody converter for ")
                .append(type)
                .append(".\n");
        if (skipPast != null) {
            builder.append("  Skipped:");
            for (int i = 0; i < start; i++) {
                builder.append("\n   * ").append(converterFactories.get(i).getClass().getName());
            }
            builder.append('\n');
        }
        builder.append("  Tried:");
        for (int i = start, count = converterFactories.size(); i < count; i++) {
            builder.append("\n   * ").append(converterFactories.get(i).getClass().getName());
        }
        throw new IllegalArgumentException(builder.toString());
    }

    public RawCall.Factory callFactory() {
        return callFactory;
    }

    public static final class Builder {
        private @Nullable
        RawCall.Factory callFactory;
        private final List<Converter.Factory> converterFactories = new ArrayList<>();
        private final List<CallAdapter.Factory> callAdapterFactories = new ArrayList<>();
        private final List<ARetrofitInterceptor> interceptors = new ArrayList<>();

        public Builder addCallAdapterFactory(CallAdapter.Factory factory) {
            callAdapterFactories.add(checkNotNull(factory, "factory == null"));
            return this;
        }

        public Builder addConverterFactory(Converter.Factory factory) {
            converterFactories.add(checkNotNull(factory, "factory == null"));
            return this;
        }

        public Builder addInterceptor(ARetrofitInterceptor interceptor) {
            this.interceptors.add(interceptor);
            return this;
        }

        public List<CallAdapter.Factory> callAdapterFactories() {
            return this.callAdapterFactories;
        }

        public Builder callFactory(RawCall.Factory factory) {
            this.callFactory = checkNotNull(factory, "factory == null");
            return this;
        }

        public ARetrofit build() {
            RawCall.Factory callFactory = this.callFactory;
            if (callFactory == null) {
                callFactory = new RawCallFactory();
            }
            List<CallAdapter.Factory> callAdapterFactories = new ArrayList<>(this.callAdapterFactories);
            callAdapterFactories.add(DefaultCallAdapterFactory.INSTANCE);

            List<Converter.Factory> converterFactories =
                    new ArrayList<>(1 + this.converterFactories.size());
            converterFactories.addAll(this.converterFactories);

            return new ARetrofit(
                    callFactory,
                    new DefaultConverterFactory(),
                    unmodifiableList(converterFactories),
                    unmodifiableList(callAdapterFactories),
                    unmodifiableList(interceptors)
            );
        }
    }
}
