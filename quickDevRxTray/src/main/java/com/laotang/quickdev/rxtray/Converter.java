package com.laotang.quickdev.rxtray;

import androidx.annotation.NonNull;

import net.grandcentrix.tray.TrayPreferences;

import java.lang.reflect.Type;

/**
 * Converts instances of {@code T} to be stored and retrieved as Strings in {@link
 * TrayPreferences}.
 */
public interface Converter {
    /**
     * Deserialize to an instance of {@code T}. The input is retrieved from {@link
     * TrayPreferences#getString(String, String)}.
     */
    @NonNull
    Object deserialize(@NonNull String serialized, Type type);

    /**
     * Serialize the {@code value} to a String. The result will be used with {@link
     * TrayPreferences#put(String, String)}.
     */
    @NonNull
    String serialize(@NonNull Object value);
}
