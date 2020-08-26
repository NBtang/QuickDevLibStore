package com.laotang.quickdev.rxtray;

import androidx.annotation.NonNull;

import net.grandcentrix.tray.TrayPreferences;

import java.lang.reflect.Type;

import static com.laotang.quickdev.rxtray.Preconditions.checkNotNull;


final class ConverterAdapter<T> implements RealPreference.Adapter<T> {
    private final Converter converter;
    private final Type type;

    ConverterAdapter(Converter converter, Class<T> type) {
        this.converter = converter;
        this.type = type;
    }

    @NonNull
    @Override
    public T get(@NonNull String key, @NonNull TrayPreferences preferences,
                 @NonNull T defaultValue) {
        String serialized = preferences.getString(key, null);
        if (serialized == null) return defaultValue;

        T value = (T) converter.deserialize(serialized, type);
        checkNotNull(value, "Deserialized value must not be null from string: " + serialized);
        return value;
    }

    @Override
    public void set(@NonNull String key, @NonNull T value, @NonNull TrayPreferences preferences) {
        String serialized = converter.serialize(value);
        checkNotNull(serialized, "Serialized string must not be null from value: " + value);
        preferences.put(key, serialized);
    }
}
