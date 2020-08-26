package com.laotang.quickdev.rxtray;

import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

/**
 * A preference of type {@link T}. Instances can be created from {@link RxTray}.
 */
public interface Preference<T> {
    /**
     * The key for which this preference will store and retrieve values.
     */
    @NonNull
    String key();

    /**
     * The value used if none is stored.
     */
    @NonNull
    T defaultValue();

    /**
     * Retrieve the current value for this preference. Returns {@link #defaultValue()} if no value is
     * set.
     */
    @NonNull
    T get();

    /**
     * Change this preference's stored value to {@code value}.
     */
    void set(@NonNull T value);

    /**
     * Returns true if this preference has a stored value.
     */
    boolean isSet();

    /**
     * Delete the stored value for this preference, if any.
     */
    void delete();

    /**
     * Observe changes to this preference. The current value or {@link #defaultValue()} will be
     * emitted on first subscribe.
     */
    @CheckResult
    @NonNull
    Observable<T> asObservable();

    /**
     * An action which stores a new value for this preference.
     */
    @CheckResult
    @NonNull
    Consumer<? super T> asConsumer();
}
