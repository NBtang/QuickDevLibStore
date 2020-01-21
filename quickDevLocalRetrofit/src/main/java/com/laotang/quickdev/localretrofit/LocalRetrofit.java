package com.laotang.quickdev.localretrofit;

import com.laotang.quickdev.localretrofit.calladapter.RxJava2CallAdapterFactory;
import com.laotang.quickdev.localretrofit.converter.JsonConverterFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.laotang.quickdev.localretrofit.Utils.checkNotNull;
import static java.util.Collections.unmodifiableList;

public final class LocalRetrofit {
    private final Map<Method, ServiceMethod<?, ?>> serviceMethodCache = new ConcurrentHashMap<>();

    final List<Converter.Factory> converterFactories;
    final List<CallAdapter.Factory> callAdapterFactories;

    LocalRetrofit(List<Converter.Factory> converterFactories, List<CallAdapter.Factory> callAdapterFactories) {
        this.converterFactories = converterFactories; // Copy+unmodifiable at call site.
        this.callAdapterFactories = callAdapterFactories; // Copy+unmodifiable at call site.
    }

    public List<Converter.Factory> converterFactories() {
        return converterFactories;
    }
    public List<CallAdapter.Factory> callAdapterFactories() {
        return this.callAdapterFactories;
    }

    @SuppressWarnings("unchecked")
    public <T> T create(final Class<T> service) {
        Utils.validateServiceInterface(service);
        return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class<?>[] { service },
                new InvocationHandler() {

                    @Override public Object invoke(Object proxy, Method method, Object[] args)
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

    public <T> Converter<String, T> responseBodyConverter(Type type, Annotation[] annotations) {
        return nextResponseBodyConverter(null, type, annotations);
    }

    public <T> Converter<String, T> nextResponseBodyConverter(
            Converter.Factory skipPast, Type type, Annotation[] annotations) {
        checkNotNull(type, "type == null");
        checkNotNull(annotations, "annotations == null");

        int start = converterFactories.indexOf(skipPast) + 1;
        for (int i = start, count = converterFactories.size(); i < count; i++) {
            Converter<String, ?> converter =
                    converterFactories.get(i).responseBodyConverter(type, annotations, this);
            if (converter != null) {
                //noinspection unchecked
                return (Converter<String, T>) converter;
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

    public static final class Builder {
        private final List<Converter.Factory> converterFactories = new ArrayList<>();
        private final List<CallAdapter.Factory> callAdapterFactories = new ArrayList<>();

        public Builder addConverterFactory(Converter.Factory factory) {
            converterFactories.add(checkNotNull(factory, "factory == null"));
            return this;
        }

        public Builder addCallAdapterFactory(CallAdapter.Factory factory) {
            callAdapterFactories.add(checkNotNull(factory, "factory == null"));
            return this;
        }

        public List<CallAdapter.Factory> callAdapterFactories() {
            return this.callAdapterFactories;
        }

        public List<Converter.Factory> converterFactories() {
            return this.converterFactories;
        }

        public LocalRetrofit build() {
            List<CallAdapter.Factory> callAdapterFactories = new ArrayList<>(this.callAdapterFactories);
            callAdapterFactories.add(RxJava2CallAdapterFactory.create());
            callAdapterFactories.add(DefaultCallAdapterFactory.INSTANCE);

            List<Converter.Factory> converterFactories =
                    new ArrayList<>(1 + this.converterFactories.size());

            converterFactories.addAll(this.converterFactories);
            converterFactories.add(JsonConverterFactory.Companion.create());

            return new LocalRetrofit(unmodifiableList(converterFactories), unmodifiableList(callAdapterFactories));
        }
    }
}
