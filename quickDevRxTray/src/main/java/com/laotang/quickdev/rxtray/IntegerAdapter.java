package com.laotang.quickdev.rxtray;

import androidx.annotation.NonNull;

import net.grandcentrix.tray.TrayPreferences;

final class IntegerAdapter implements RealPreference.Adapter<Integer> {
    static final IntegerAdapter INSTANCE = new IntegerAdapter();

    @NonNull
    @Override
    public Integer get(@NonNull String key, @NonNull TrayPreferences preferences,
                       @NonNull Integer defaultValue) {
        return preferences.getInt(key, defaultValue);
    }

    @Override
    public void set(@NonNull String key, @NonNull Integer value,
                    @NonNull TrayPreferences preferences) {
        preferences.put(key, value);
    }
}
