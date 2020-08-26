package com.laotang.quickdev.rxtray;

import androidx.annotation.NonNull;

import net.grandcentrix.tray.TrayPreferences;

final class LongAdapter implements RealPreference.Adapter<Long> {
    static final LongAdapter INSTANCE = new LongAdapter();

    @NonNull
    @Override
    public Long get(@NonNull String key, @NonNull TrayPreferences preferences,
                    @NonNull Long defaultValue) {
        return preferences.getLong(key, defaultValue);
    }

    @Override
    public void set(@NonNull String key, @NonNull Long value,
                    @NonNull TrayPreferences preferences) {
        preferences.put(key, value);
    }
}
