package com.laotang.quickdev.aretrofit;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;

import com.laotang.quickdev.aretrofit.router.Bundles;
import com.laotang.quickdev.aretrofit.router.Extra;
import com.laotang.quickdev.aretrofit.router.Flags;
import com.laotang.quickdev.aretrofit.router.From;
import com.laotang.quickdev.aretrofit.router.Go;
import com.laotang.quickdev.aretrofit.router.GreenChannel;
import com.laotang.quickdev.aretrofit.router.IsGreenChannel;
import com.laotang.quickdev.aretrofit.router.JsonBody;
import com.laotang.quickdev.aretrofit.router.Url;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

final class ServiceMethod<R, T> {
    private final RawCall.Factory callFactory;
    private final CallAdapter<R, T> callAdapter;
    private final Converter<Request, R> routerInfoConverter;
    private final ParameterHandler<?>[] parameterHandlers;
    String relativeUrl;
    int mFlags;
    boolean isGreenChannel;

    ServiceMethod(Builder<R, T> builder) {
        this.callFactory = builder.retrofit.callFactory();
        this.callAdapter = builder.callAdapter;
        this.routerInfoConverter = builder.routerInfoConverter;
        this.parameterHandlers = builder.parameterHandlers;
        this.relativeUrl = builder.relativeUrl;
        this.mFlags = builder.mFlags;
        this.isGreenChannel = builder.isGreenChannel;
    }

    RawCall toCall(Object... args) {
        RequestBuilder requestBuilder = new RequestBuilder(relativeUrl, mFlags, isGreenChannel);
        ParameterHandler<Object>[] handlers = (ParameterHandler<Object>[]) parameterHandlers;
        int argumentCount = args != null ? args.length : 0;
        if (argumentCount != handlers.length) {
            throw new IllegalArgumentException("Argument count (" + argumentCount
                    + ") doesn't match expected count (" + handlers.length + ")");
        }
        for (int p = 0; p < argumentCount; p++) {
            handlers[p].apply(requestBuilder, args[p]);
        }
        return callFactory.newCall(requestBuilder.build());
    }

    T adapt(Call<R> call) {
        return callAdapter.adapt(call);
    }

    R toResponse(Request request) {
        return routerInfoConverter.convert(request);
    }

    static final class Builder<T, R> {
        final ARetrofit retrofit;
        final Method method;
        final Annotation[] methodAnnotations;
        final Annotation[][] parameterAnnotationsArray;
        final Type[] parameterTypes;

        boolean gotUrl;
        String relativeUrl;
        int mFlags = -1;
        boolean isGreenChannel;

        ParameterHandler<?>[] parameterHandlers;
        Type responseType;
        CallAdapter<T, R> callAdapter;
        Converter<Request, T> routerInfoConverter;

        Builder(ARetrofit retrofit, Method method) {
            this.retrofit = retrofit;
            this.method = method;
            this.methodAnnotations = method.getAnnotations();
            this.parameterTypes = method.getGenericParameterTypes();
            this.parameterAnnotationsArray = method.getParameterAnnotations();
        }

        public ServiceMethod build() {
            callAdapter = createCallAdapter();
            responseType = callAdapter.responseType();
            if (responseType == Response.class) {
                throw methodError("'"
                        + Utils.getRawType(responseType).getName()
                        + "' is not a valid response body type. Did you mean ResponseBody?");
            }
            routerInfoConverter = createRouterInfoConverter();

            for (Annotation annotation : methodAnnotations) {
                parseMethodAnnotation(annotation);
            }

            int parameterCount = parameterAnnotationsArray.length;
            parameterHandlers = new ParameterHandler<?>[parameterCount];

            for (int p = 0; p < parameterCount; p++) {
                Type parameterType = parameterTypes[p];
                if (Utils.hasUnresolvableType(parameterType)) {
                    throw parameterError(p, "Parameter type must not include a type variable or wildcard: %s",
                            parameterType);
                }

                Annotation[] parameterAnnotations = parameterAnnotationsArray[p];
                if (parameterAnnotations == null) {
                    throw parameterError(p, "No Retrofit annotation found.");
                }

                parameterHandlers[p] = parseParameter(p, parameterType, parameterAnnotations);
            }
            if (relativeUrl == null && !gotUrl) {
                throw methodError("Missing either URL or @Url parameter.");
            }
            return new ServiceMethod<>(this);
        }

        private void parseMethodAnnotation(Annotation annotation) {
            if (annotation instanceof Go) {
                parseRelativeUrl(((Go) annotation).value());
            } else if (annotation instanceof Flags) {
                parseIntentFlags(((Flags) annotation).value());
            } else if (annotation instanceof GreenChannel) {
                parseGreenChannel(((GreenChannel) annotation).value());
            }
        }

        private void parseRelativeUrl(String value) {
            relativeUrl = value;
        }

        private void parseIntentFlags(int flags) {
            this.mFlags |= flags;
        }

        private void parseGreenChannel(boolean isGreenChannel) {
            this.isGreenChannel = isGreenChannel;
        }

        private ParameterHandler<?> parseParameter(
                int p, Type parameterType, Annotation[] annotations) {
            ParameterHandler<?> result = null;
            for (Annotation annotation : annotations) {
                ParameterHandler<?> annotationAction = parseParameterAnnotation(
                        p, parameterType, annotations, annotation);
                if (annotationAction == null) {
                    continue;
                }
                if (result != null) {
                    throw parameterError(p, "Multiple ARetrofit annotations found, only one allowed.");
                }
                result = annotationAction;
            }
            if (result == null) {
                throw parameterError(p, "No ARetrofit annotation found.");
            }
            return result;
        }

        private ParameterHandler<?> parseParameterAnnotation(
                int p, Type type, Annotation[] annotations, Annotation annotation) {

            if (annotation instanceof Url) {
                if (gotUrl) {
                    throw parameterError(p, "Multiple @Url method annotations found.");
                }
                if (relativeUrl != null) {
                    throw parameterError(p, "@Url cannot be used with Go annotations");
                }
                gotUrl = true;
                if (type == String.class) {
                    return new ParameterHandler.RelativeUrl();
                } else {
                    throw parameterError(p,
                            "@Url must be String");
                }
            } else if (annotation instanceof JsonBody) {
                JsonBody jsonBody = (JsonBody) annotation;
                String name = jsonBody.value();
                Converter<?, String> converter = retrofit.requestBodyConverter(type, annotations, methodAnnotations);
                return new ParameterHandler.JsonBody(name, converter);
            } else if (annotation instanceof Extra) {
                Extra extra = (Extra) annotation;
                String name = extra.value();
                Class<?> rawParameterType = Utils.getRawType(type);
                if (Iterable.class.isAssignableFrom(rawParameterType)) {
                    if (!(type instanceof ParameterizedType)) {
                        throw parameterError(p, rawParameterType.getSimpleName()
                                + " must include generic type (e.g., "
                                + rawParameterType.getSimpleName()
                                + "<String>)");
                    }
                    ParameterizedType parameterizedType = (ParameterizedType) type;
                    Type iterableType = Utils.getParameterUpperBound(0, parameterizedType);
                    return new ParameterHandler.ExtraIterable(name, iterableType);
                } else if (rawParameterType.isArray()) {
                    Class<?> arrayComponentType = boxIfPrimitive(rawParameterType.getComponentType());
                    return new ParameterHandler.ExtraArray(name, arrayComponentType);
                } else if (Parcelable.class.isAssignableFrom(rawParameterType)) {
                    return new ParameterHandler.ExtraParcelable(name);
                } else if (Serializable.class.isAssignableFrom(rawParameterType)) {
                    return new ParameterHandler.ExtraSerializable(name);
                } else {
                    Class<?> boxIfPrimitive = boxIfPrimitive(rawParameterType);
                    return new ParameterHandler.Extra(name, boxIfPrimitive);
                }
            } else if (annotation instanceof From) {
                if (type == Context.class) {
                    return new ParameterHandler.From();
                } else {
                    throw parameterError(p,
                            "@From must be Context");
                }
            } else if (annotation instanceof Bundles) {
                if (type == Bundle.class) {
                    return new ParameterHandler.Bundles();
                } else {
                    throw parameterError(p,
                            "@From must be Bundle");
                }
            } else if (annotation instanceof IsGreenChannel) {
                Class<?> rawParameterType = Utils.getRawType(type);
                if (boxIfPrimitive(rawParameterType) == Boolean.class) {
                    return new ParameterHandler.IsGreenChannel();
                } else {
                    throw parameterError(p,
                            "@IsGreenChannel must be Boolean");
                }
            }
            return null; // Not a ARetrofit annotation.
        }

        private CallAdapter<T, R> createCallAdapter() {
            Type returnType = method.getGenericReturnType();
            if (Utils.hasUnresolvableType(returnType)) {
                throw methodError(
                        "Method return type must not include a type variable or wildcard: %s", returnType);
            }
//            if (returnType == void.class) {
//                throw methodError("Service methods cannot return void.");
//            }
            Annotation[] annotations = method.getAnnotations();
            try {
                //noinspection unchecked
                return (CallAdapter<T, R>) retrofit.callAdapter(returnType, annotations);
            } catch (RuntimeException e) { // Wide exception range because factories are user code.
                throw methodError(e, "Unable to create call adapter for %s", returnType);
            }
        }

        private Converter<Request, T> createResponseConverter() {
            Annotation[] annotations = method.getAnnotations();
            try {
                return retrofit.responseBodyConverter(responseType, annotations);
            } catch (RuntimeException e) { // Wide exception range because factories are user code.
                throw methodError(e, "Unable to create converter for %s", responseType);
            }
        }

        private Converter<Request, T> createRouterInfoConverter() {
            Annotation[] annotations = method.getAnnotations();
            try {
                return retrofit.routerConverter(responseType, annotations);
            } catch (RuntimeException e) { // Wide exception range because factories are user code.
                throw methodError(e, "Unable to create converter for %s", responseType);
            }
        }

        private RuntimeException methodError(String message, Object... args) {
            return methodError(null, message, args);
        }

        private RuntimeException methodError(Throwable cause, String message, Object... args) {
            message = String.format(message, args);
            return new IllegalArgumentException(message
                    + "\n    for method "
                    + method.getDeclaringClass().getSimpleName()
                    + "."
                    + method.getName(), cause);
        }

        private RuntimeException parameterError(
                Throwable cause, int p, String message, Object... args) {
            return methodError(cause, message + " (parameter #" + (p + 1) + ")", args);
        }

        private RuntimeException parameterError(int p, String message, Object... args) {
            return methodError(message + " (parameter #" + (p + 1) + ")", args);
        }
    }

    static Class<?> boxIfPrimitive(Class<?> type) {
        if (boolean.class == type) return Boolean.class;
        if (byte.class == type) return Byte.class;
        if (char.class == type) return Character.class;
        if (double.class == type) return Double.class;
        if (float.class == type) return Float.class;
        if (int.class == type) return Integer.class;
        if (long.class == type) return Long.class;
        if (short.class == type) return Short.class;
        return type;
    }
}
