package com.laotang.quickdev.aretrofit;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

final class RequestBuilder {

    private @Nullable
    String relativeUrl;
    private int mFlags;
    boolean isGreenChannel;
    private Bundle mBundle;
    private final Request.Builder requestBuilder;
    private WeakReference<Context> contextWeakReference;

    RequestBuilder(@Nullable String relativeUrl, int flags,boolean isGreenChannel) {
        this.relativeUrl = relativeUrl;
        this.mFlags = flags;
        this.isGreenChannel = isGreenChannel;
        this.mBundle = new Bundle();
        this.requestBuilder = new Request.Builder();
    }

    public void setContext(Context context) {
        this.contextWeakReference = new WeakReference<>(context);
    }

    void setRelativeUrl(@Nullable String relativeUrl) {
        this.relativeUrl = relativeUrl;
    }

    void putExtras(Bundle bundle) {
        mBundle.putAll(bundle);
    }

    void putExtras(@Nullable String key, @Nullable String value) {
        mBundle.putString(key, value);
    }

    void putExtras(@Nullable String key, long value) {
        mBundle.putLong(key, value);
    }

    void putExtras(@Nullable String key, boolean value) {
        mBundle.putBoolean(key, value);
    }

    void putExtras(@Nullable String key, byte value) {
        mBundle.putByte(key, value);
    }

    void putExtras(@Nullable String key, char value) {
        mBundle.putChar(key, value);
    }

    void putExtras(@Nullable String key, short value) {
        mBundle.putShort(key, value);
    }

    void putExtras(@Nullable String key, int value) {
        mBundle.putInt(key, value);
    }

    void putExtras(@Nullable String key, float value) {
        mBundle.putFloat(key, value);
    }

    void putExtras(@Nullable String key, double value) {
        mBundle.putDouble(key, value);
    }

    void putExtras(@Nullable String key, @Nullable CharSequence value) {
        mBundle.putCharSequence(key, value);
    }

    void putIntegerArrayListExtras(@Nullable String key, @Nullable ArrayList<Integer> value) {
        mBundle.putIntegerArrayList(key, value);
    }

    void putStringArrayListExtras(@Nullable String key, @Nullable ArrayList<String> value) {
        mBundle.putStringArrayList(key, value);
    }

    void putCharSequenceArrayListExtras(@Nullable String key, @Nullable ArrayList<CharSequence> value) {
        mBundle.putCharSequenceArrayList(key, value);
    }

    void putExtras(@Nullable String key, @Nullable Serializable value) {
        mBundle.putSerializable(key, value);
    }

    void putExtras(@Nullable String key, @Nullable Bundle value) {
        mBundle.putBundle(key, value);
    }

    void putExtras(@Nullable String key, @Nullable boolean[] value) {
        mBundle.putBooleanArray(key, value);
    }

    void putExtras(@Nullable String key, @Nullable byte[] value) {
        mBundle.putByteArray(key, value);
    }

    void putExtras(@Nullable String key, @Nullable short[] value) {
        mBundle.putShortArray(key, value);
    }

    void putExtras(@Nullable String key, @Nullable char[] value) {
        mBundle.putCharArray(key, value);
    }

    void putExtras(@Nullable String key, @Nullable int[] value) {
        mBundle.putIntArray(key, value);
    }

    void putExtras(@Nullable String key, @Nullable long[] value) {
        mBundle.putLongArray(key, value);
    }

    void putExtras(@Nullable String key, @Nullable float[] value) {
        mBundle.putFloatArray(key, value);
    }

    void putExtras(@Nullable String key, @Nullable double[] value) {
        mBundle.putDoubleArray(key, value);
    }

    void putExtras(@Nullable String key, @Nullable String[] value) {
        mBundle.putStringArray(key, value);
    }

    void putExtras(@Nullable String key, @Nullable CharSequence[] value) {
        mBundle.putCharSequenceArray(key, value);
    }

    void putExtras(@Nullable String key, @Nullable Parcelable value) {
        mBundle.putParcelable(key, value);
    }

    void putExtras(@Nullable String key, @Nullable Parcelable[] value) {
        mBundle.putParcelableArray(key, value);
    }

    void putExtras(@Nullable String key,
                   @Nullable ArrayList<? extends Parcelable> value) {
        mBundle.putParcelableArrayList(key, value);
    }

    Request build() {
        return requestBuilder
                .url(relativeUrl)
                .flags(mFlags)
                .isGreenChannel(isGreenChannel)
                .extras(mBundle)
                .context(contextWeakReference)
                .build();
    }
}
