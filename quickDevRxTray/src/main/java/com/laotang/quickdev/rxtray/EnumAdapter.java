package com.laotang.quickdev.rxtray;

import androidx.annotation.NonNull;

import net.grandcentrix.tray.TrayPreferences;

final class EnumAdapter<T extends Enum<T>> implements RealPreference.Adapter<T> {
    private final Class<T> enumClass;

    EnumAdapter(Class<T> enumClass) {
        this.enumClass = enumClass;
    }

    @NonNull
    @Override
    public T get(@NonNull String key, @NonNull TrayPreferences preferences,
                 @NonNull T defaultValue) {
        String value = preferences.getString(key, null);
        if (value == null) return defaultValue;
        return Enum.valueOf(enumClass, value);
    }

    @Override
    public void set(@NonNull String key, @NonNull T value, @NonNull TrayPreferences preferences) {
        preferences.put(key, value.name());
    }
}
