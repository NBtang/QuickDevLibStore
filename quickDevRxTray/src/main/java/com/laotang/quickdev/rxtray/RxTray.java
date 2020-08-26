package com.laotang.quickdev.rxtray;

import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;

import net.grandcentrix.tray.TrayPreferences;
import net.grandcentrix.tray.core.OnTrayPreferenceChangeListener;
import net.grandcentrix.tray.core.TrayItem;

import java.util.Collection;
import java.util.Iterator;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Cancellable;

import static com.laotang.quickdev.rxtray.Preconditions.checkNotNull;

/**
 * A factory for reactive {@link Preference} objects.
 */
public final class RxTray {
    private static final Float DEFAULT_FLOAT = 0f;
    private static final Integer DEFAULT_INTEGER = 0;
    private static final Boolean DEFAULT_BOOLEAN = false;
    private static final Long DEFAULT_LONG = 0L;
    private static final String DEFAULT_STRING = "";

    /**
     * Create an instance of {@link RxTray} for {@code preferences}.
     */
    @CheckResult
    @NonNull
    public static RxTray create(@NonNull TrayPreferences preferences, @NonNull Converter converter) {
        checkNotNull(preferences, "preferences == null");
        return new RxTray(preferences, converter);
    }

    private final TrayPreferences preferences;
    private final Converter converter;
    private final Observable<String> keyChanges;

    private RxTray(final TrayPreferences preferences, final Converter converter) {
        this.preferences = preferences;
        this.converter = converter;
        this.keyChanges = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> emitter) {
                final OnTrayPreferenceChangeListener listener = new OnTrayPreferenceChangeListener() {

                    @Override
                    public void onTrayPreferenceChanged(Collection<TrayItem> items) {
                        Iterator<TrayItem> itemIterator = items.iterator();
                        if (itemIterator.hasNext()) {
                            emitter.onNext(itemIterator.next().key());
                        }
                    }
                };
                emitter.setCancellable(new Cancellable() {
                    @Override
                    public void cancel() {
                        preferences.unregisterOnTrayPreferenceChangeListener(listener);
                    }
                });

                preferences.registerOnTrayPreferenceChangeListener(listener);
            }
        }).share();
    }

    /**
     * Create a boolean preference for {@code key}. Default is {@code false}.
     */
    @CheckResult
    @NonNull
    public Preference<Boolean> getBoolean(@NonNull String key) {
        return getBoolean(key, DEFAULT_BOOLEAN);
    }

    /**
     * Create a boolean preference for {@code key} with a default of {@code defaultValue}.
     */
    @CheckResult
    @NonNull
    public Preference<Boolean> getBoolean(@NonNull String key, @NonNull Boolean defaultValue) {
        checkNotNull(key, "key == null");
        checkNotNull(defaultValue, "defaultValue == null");
        return new RealPreference<>(preferences, key, defaultValue, BooleanAdapter.INSTANCE, keyChanges);
    }

    /**
     * Create an enum preference for {@code key} with a default of {@code defaultValue}.
     */
    @CheckResult
    @NonNull
    public <T extends Enum<T>> Preference<T> getEnum(@NonNull String key, @NonNull T defaultValue,
                                                     @NonNull Class<T> enumClass) {
        checkNotNull(key, "key == null");
        checkNotNull(defaultValue, "defaultValue == null");
        checkNotNull(enumClass, "enumClass == null");
        return new RealPreference<>(preferences, key, defaultValue, new EnumAdapter<>(enumClass), keyChanges);
    }

    /**
     * Create a float preference for {@code key}. Default is {@code 0}.
     */
    @CheckResult
    @NonNull
    public Preference<Float> getFloat(@NonNull String key) {
        return getFloat(key, DEFAULT_FLOAT);
    }

    /**
     * Create a float preference for {@code key} with a default of {@code defaultValue}.
     */
    @CheckResult
    @NonNull
    public Preference<Float> getFloat(@NonNull String key, @NonNull Float defaultValue) {
        checkNotNull(key, "key == null");
        checkNotNull(defaultValue, "defaultValue == null");
        return new RealPreference<>(preferences, key, defaultValue, FloatAdapter.INSTANCE, keyChanges);
    }

    /**
     * Create an integer preference for {@code key}. Default is {@code 0}.
     */
    @CheckResult
    @NonNull
    public Preference<Integer> getInteger(@NonNull String key) {
        return getInteger(key, DEFAULT_INTEGER);
    }

    /**
     * Create an integer preference for {@code key} with a default of {@code defaultValue}.
     */
    @CheckResult
    @NonNull
    public Preference<Integer> getInteger(@NonNull String key, @NonNull Integer defaultValue) {
        checkNotNull(key, "key == null");
        checkNotNull(defaultValue, "defaultValue == null");
        return new RealPreference<>(preferences, key, defaultValue, IntegerAdapter.INSTANCE, keyChanges);
    }

    /**
     * Create a long preference for {@code key}. Default is {@code 0}.
     */
    @CheckResult
    @NonNull
    public Preference<Long> getLong(@NonNull String key) {
        return getLong(key, DEFAULT_LONG);
    }

    /**
     * Create a long preference for {@code key} with a default of {@code defaultValue}.
     */
    @CheckResult
    @NonNull
    public Preference<Long> getLong(@NonNull String key, @NonNull Long defaultValue) {
        checkNotNull(key, "key == null");
        checkNotNull(defaultValue, "defaultValue == null");
        return new RealPreference<>(preferences, key, defaultValue, LongAdapter.INSTANCE, keyChanges);
    }

    /**
     * Create a preference for type {@code T} for {@code key} with a default of {@code defaultValue}.
     */
    @CheckResult
    @NonNull
    public <T> Preference<T> getObject(@NonNull String key,
                                       @NonNull T defaultValue, @NonNull Class<T> type) {
        checkNotNull(key, "key == null");
        checkNotNull(defaultValue, "defaultValue == null");
        checkNotNull(converter, "converter == null");
        return new RealPreference<>(preferences, key, defaultValue,
                new ConverterAdapter<>(converter, type), keyChanges);
    }

    /**
     * Create a string preference for {@code key}. Default is {@code ""}.
     */
    @CheckResult
    @NonNull
    public Preference<String> getString(@NonNull String key) {
        return getString(key, DEFAULT_STRING);
    }

    /**
     * Create a string preference for {@code key} with a default of {@code defaultValue}.
     */
    @CheckResult
    @NonNull
    public Preference<String> getString(@NonNull String key, @NonNull String defaultValue) {
        checkNotNull(key, "key == null");
        checkNotNull(defaultValue, "defaultValue == null");
        return new RealPreference<>(preferences, key, defaultValue, StringAdapter.INSTANCE, keyChanges);
    }

    public void clear() {
        preferences.clear();
    }
}
