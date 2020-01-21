package com.laotang.quickdev.aretrofit;


import static com.laotang.quickdev.aretrofit.Utils.throwIfFatal;

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
    public Response<T> execute() {
        RawCall call;
        synchronized (this) {
            if (executed) throw new IllegalStateException("Already executed.");
            executed = true;

            if (creationFailure != null) {
                if (creationFailure instanceof RuntimeException) {
                    throw (RuntimeException) creationFailure;
                } else {
                    throw (Error) creationFailure;
                }
            }

            try {
                call  = createRawCall();
            } catch (RuntimeException | Error e) {
                throwIfFatal(e); //  Do not assign a fatal error to creationFailure.
                creationFailure = e;
                throw e;
            }

            return parseResponse(call.execute());
        }
    }

    @Override
    public Call<T> clone() {
        return new ServiceCall<>(serviceMethod,args);
    }

    private RawCall createRawCall(){
        RawCall call = serviceMethod.toCall(args);
        if (call == null) {
            throw new NullPointerException("Call.Factory returned null.");
        }
        return call;
    }

    Response<T> parseResponse(Request request){
        T body = serviceMethod.toResponse(request);
        return Response.success(body);
    }
}
