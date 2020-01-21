package com.laotang.quickdev.localretrofit;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

final class ServiceMethod<R, T> {

    private final CallAdapter<R, T> callAdapter;
    private final ParameterHandler<?>[] parameterHandlers;
    private final Converter<String, R> responseConverter;

    ServiceMethod(Builder<R, T> builder) {
        this.callAdapter = builder.callAdapter;
        this.responseConverter = builder.responseConverter;
        this.parameterHandlers = builder.parameterHandlers;
    }

    RawCall toCall(Object... args) throws IOException {
        ParameterHandler<Object>[] handlers = (ParameterHandler<Object>[]) parameterHandlers;
        int argumentCount = args != null ? args.length : 0;
        if (argumentCount != handlers.length) {
            throw new IllegalArgumentException("Argument count (" + argumentCount
                    + ") doesn't match expected count (" + handlers.length + ")");
        }
        return handlers[0].apply(args[0]);
    }

    T adapt(Call<R> call) {
        return callAdapter.adapt(call);
    }

    R toResponse(String body) throws IOException {
        return responseConverter.convert(body);
    }

    static final class Builder<T, R> {
        final LocalRetrofit retrofit;
        final Method method;
        final Annotation[] methodAnnotations;
        final Annotation[][] parameterAnnotationsArray;
        final Type[] parameterTypes;

        ParameterHandler<?>[] parameterHandlers;
        Type responseType;
        Converter<String, T> responseConverter;
        CallAdapter<T, R> callAdapter;

        Builder(LocalRetrofit retrofit, Method method) {
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
            responseConverter = createResponseConverter();

            int parameterCount = parameterAnnotationsArray.length;
            if(parameterCount!=1){
                throw methodError(
                        "parameterCount !=1");
            }
            parameterHandlers = new ParameterHandler<?>[parameterCount];
            Type parameterType = parameterTypes[0];
            if (Utils.hasUnresolvableType(parameterType)) {
                throw parameterError(0, "Parameter type must not include a type variable or wildcard: %s",
                        parameterType);
            }
            Annotation[] parameterAnnotations = parameterAnnotationsArray[0];
            if (parameterAnnotations == null) {
                throw parameterError(0, "No LocalRetrofit annotation found.");
            }
            parameterHandlers[0] = parseParameter(0, parameterType, parameterAnnotations);

            return new ServiceMethod<>(this);
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
                    throw parameterError(p, "Multiple LocalRetrofit annotations found, only one allowed.");
                }

                result = annotationAction;
            }

            if (result == null) {
                throw parameterError(p, "No LocalRetrofit annotation found.");
            }

            return result;
        }

        private ParameterHandler<?> parseParameterAnnotation(
                int p, Type type, Annotation[] annotations, Annotation annotation) {
            if (annotation instanceof File) {
                if (type == String.class) {
                    return new ParameterHandler.File();
                } else {
                    throw parameterError(p,
                            "@File must be String");
                }
            }else if(annotation instanceof Assets){
                if (type == String.class) {
                    return new ParameterHandler.Assets();
                } else {
                    throw parameterError(p,
                            "@File must be String");
                }
            }else if(annotation instanceof Raw){
                if (type == int.class) {
                    return new ParameterHandler.Raw();
                } else {
                    throw parameterError(p,
                            "@File must be int");
                }
            }else if(annotation instanceof Stream){
                if (type == InputStream.class) {
                    return new ParameterHandler.Stream();
                } else {
                    throw parameterError(p,
                            "@File must be int");
                }
            }
            return null; // Not a Retrofit annotation.
        }

        private CallAdapter<T, R> createCallAdapter() {
            Type returnType = method.getGenericReturnType();
            if (Utils.hasUnresolvableType(returnType)) {
                throw methodError(
                        "Method return type must not include a type variable or wildcard: %s", returnType);
            }
            if (returnType == void.class) {
                throw methodError("Service methods cannot return void.");
            }
            Annotation[] annotations = method.getAnnotations();
            try {
                //noinspection unchecked
                return (CallAdapter<T, R>) retrofit.callAdapter(returnType, annotations);
            } catch (RuntimeException e) { // Wide exception range because factories are user code.
                throw methodError(e, "Unable to create call adapter for %s", returnType);
            }
        }

        private Converter<String, T> createResponseConverter() {
            Annotation[] annotations = method.getAnnotations();
            try {
                return retrofit.responseBodyConverter(responseType, annotations);
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
}
