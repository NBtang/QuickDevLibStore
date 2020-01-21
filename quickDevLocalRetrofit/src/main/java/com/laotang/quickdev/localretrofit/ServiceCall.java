package com.laotang.quickdev.localretrofit;

import java.io.IOException;

import static com.laotang.quickdev.localretrofit.Utils.throwIfFatal;

final class ServiceCall<T> implements Call<T> {

    private final ServiceMethod<T, ?> serviceMethod;
    private final Object[] args;
    private boolean executed;
    private Throwable creationFailure;

    ServiceCall(ServiceMethod<T, ?> serviceMethod, Object[] args) {
        this.serviceMethod = serviceMethod;
        this.args = args;
    }

    @Override
    public Response<T> execute() throws IOException {
        RawCall call;
        synchronized (this) {
            if (executed) throw new IllegalStateException("Already executed.");
            executed = true;

            if (creationFailure != null) {
                if (creationFailure instanceof IOException) {
                    throw (IOException) creationFailure;
                } else if (creationFailure instanceof RuntimeException) {
                    throw (RuntimeException) creationFailure;
                } else {
                    throw (Error) creationFailure;
                }
            }

            try {
                call  = createRawCall();
            } catch (IOException | RuntimeException | Error e) {
                throwIfFatal(e); //  Do not assign a fatal error to creationFailure.
                creationFailure = e;
                throw e;
            }

            return parseResponse(call.execute());
        }
    }

    @Override
    public boolean isExecuted() {
        return executed;
    }

    @Override
    public void cancel() {

    }

    @Override
    public boolean isCanceled() {
        return false;
    }


    @Override
    public Call<T> clone() {
        return new ServiceCall<>(serviceMethod, args);
    }

    private RawCall createRawCall() throws IOException {
        RawCall call = serviceMethod.toCall(args);
        if (call == null) {
            throw new NullPointerException("Call.Factory returned null.");
        }
        return call;
    }

    Response<T> parseResponse(String value) throws IOException {
        T body = serviceMethod.toResponse(value);
        return Response.success(body, value);
    }
}
