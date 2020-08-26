package com.laotang.quickdev.rxtray;

import androidx.annotation.NonNull;

import net.grandcentrix.tray.TrayPreferences;

final class StringAdapter implements RealPreference.Adapter<String> {
    static final StringAdapter INSTANCE = new StringAdapter();

    @NonNull
    @Override
    public String get(@NonNull String key, @NonNull TrayPreferences preferences,
                      @NonNull String defaultValue) {
        //noinspection ConstantConditions
        return preferences.getString(key, defaultValue);
    }

    @Override
    public void set(@NonNull String key, @NonNull String value,
                    @NonNull TrayPreferences preferences) {
        preferences.put(key, value);
    }
}
