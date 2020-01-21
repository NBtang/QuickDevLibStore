package com.laotang.quickdev.aretrofit;

import android.content.Context;
import android.os.Bundle;

import java.lang.ref.WeakReference;

public class Request {
    final String relativeUrl;
    final int mFlags;
    final boolean isGreenChannel;
    final Bundle mBundle;
    final WeakReference<Context> contextWeakReference;

    Request(Builder builder) {
        this.relativeUrl = builder.relativeUrl;
        this.mFlags = builder.mFlags;
        this.isGreenChannel = builder.isGreenChannel;
        this.mBundle = builder.mBundle;
        this.contextWeakReference = builder.contextWeakReference;
    }

    public int getFlags() {
        return mFlags;
    }

    public String getRelativeUrl() {
        return relativeUrl;
    }

    public Bundle getExtras() {
        return mBundle;
    }

    public boolean isGreenChannel() {
        return isGreenChannel;
    }

    public WeakReference<Context> getContextWeakReference() {
        return contextWeakReference;
    }

    public Context getContext(){
        return contextWeakReference.get();
    }

    public static class Builder {
        private String relativeUrl;
        private int mFlags;
        private boolean isGreenChannel;
        private Bundle mBundle;
        private WeakReference<Context> contextWeakReference;

        public Builder() {
        }

        Builder(Request request) {
            this.relativeUrl = request.relativeUrl;
            this.mFlags = request.mFlags;
            this.mBundle = request.mBundle;
        }

        public Builder url(String url) {
            if (url == null) throw new NullPointerException("url == null");
            this.relativeUrl = url;
            return this;
        }

        public Builder flags(int flags) {
            this.mFlags = flags;
            return this;
        }

        public Builder extras(Bundle bundle) {
            this.mBundle = bundle;
            return this;
        }

        public Builder isGreenChannel(boolean isGreenChannel) {
            this.isGreenChannel = isGreenChannel;
            return this;
        }

        public Builder context(WeakReference<Context> contextWeakReference) {
            this.contextWeakReference = contextWeakReference;
            return this;
        }

        public Request build() {
            if (relativeUrl == null) throw new IllegalStateException("url == null");
            return new Request(this);
        }
    }
}
