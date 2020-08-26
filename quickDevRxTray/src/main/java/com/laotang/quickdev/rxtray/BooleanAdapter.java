package com.laotang.quickdev.rxtray;

import androidx.annotation.NonNull;

import net.grandcentrix.tray.TrayPreferences;

final class BooleanAdapter implements RealPreference.Adapter<Boolean> {
    static final BooleanAdapter INSTANCE = new BooleanAdapter();

    @NonNull
    @Override
    public Boolean get(@NonNull String key, @NonNull TrayPreferences preferences,
                       @NonNull Boolean defaultValue) {
        return preferences.getBoolean(key, defaultValue);
    }

    @Override
    public void set(@NonNull String key, @NonNull Boolean value,
                    @NonNull TrayPreferences preferences) {
        preferences.put(key, value);
    }
}
