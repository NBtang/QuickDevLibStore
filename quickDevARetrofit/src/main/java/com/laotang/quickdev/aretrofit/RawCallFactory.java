package com.laotang.quickdev.aretrofit;

final class RawCallFactory implements RawCall.Factory {

    @Override
    public RawCall newCall(Request request) {
        return new RawCallImpl(request);
    }

    class RawCallImpl implements RawCall{
        Request request;
        RawCallImpl(Request request){
            this.request = request;
        }
        @Override
        public Request execute() {
            return request;
        }
    }
}
