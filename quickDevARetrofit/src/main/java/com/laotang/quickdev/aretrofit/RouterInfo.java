package com.laotang.quickdev.aretrofit;

import android.content.Context;
import android.os.Bundle;

import java.lang.ref.WeakReference;

public class RouterInfo {
    private String relativeUrl;
    private int mFlags;
    private boolean isGreenChannel;
    private Bundle mBundle;
    private WeakReference<Context> contextWeakReference;

    public String getRelativeUrl() {
        return relativeUrl;
    }

    public int getFlags() {
        return mFlags;
    }

    public Bundle getBundle() {
        return mBundle;
    }

    public boolean isGreenChannel() {
        return isGreenChannel;
    }

    public void setGreenChannel(boolean greenChannel) {
        isGreenChannel = greenChannel;
    }

    public WeakReference<Context> getContextWeakReference() {
        return contextWeakReference;
    }

    public void setRelativeUrl(String relativeUrl) {
        this.relativeUrl = relativeUrl;
    }

    public void setFlags(int mFlags) {
        this.mFlags = mFlags;
    }

    public void setBundle(Bundle bundle) {
        this.mBundle = bundle;
    }

    public void setContextWeakReference(WeakReference<Context> contextWeakReference) {
        this.contextWeakReference = contextWeakReference;
    }
}
