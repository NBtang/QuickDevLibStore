package com.laotang.quickdev.rxtray;

import androidx.annotation.NonNull;

import net.grandcentrix.tray.TrayPreferences;

final class FloatAdapter implements RealPreference.Adapter<Float> {
    static final FloatAdapter INSTANCE = new FloatAdapter();

    @NonNull
    @Override
    public Float get(@NonNull String key, @NonNull TrayPreferences preferences,
                     @NonNull Float defaultValue) {
        return preferences.getFloat(key, defaultValue);
    }

    @Override
    public void set(@NonNull String key, @NonNull Float value,
                    @NonNull TrayPreferences preferences) {
        preferences.put(key, value);
    }
}
