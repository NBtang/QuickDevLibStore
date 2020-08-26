package com.laotang.quickdev.rxtray;

import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;

import net.grandcentrix.tray.TrayPreferences;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

import static com.laotang.quickdev.rxtray.Preconditions.checkNotNull;


final class RealPreference<T> implements Preference<T> {
    /**
     * Stores and retrieves instances of {@code T} in {@link TrayPreferences}.
     */
    interface Adapter<T> {
        /**
         * Retrieve the value for {@code key} from {@code preferences}, or {@code defaultValue}
         * if the preference is unset, or was set to {@code null}.
         */
        @NonNull
        T get(@NonNull String key, @NonNull TrayPreferences preferences,
              @NonNull T defaultValue);

        /**
         * Store non-null {@code value} for {@code key} in {@code editor}.
         * <p>
         * Note: Implementations <b>must not</b> call {@code commit()} or {@code apply()} on
         * {@code editor}.
         */
        void set(@NonNull String key, @NonNull T value, @NonNull TrayPreferences preferences);
    }

    private final TrayPreferences preferences;
    private final String key;
    private final T defaultValue;
    private final Adapter<T> adapter;
    private final Observable<T> values;

    RealPreference(TrayPreferences preferences, final String key, T defaultValue,
                   Adapter<T> adapter, Observable<String> keyChanges) {
        this.preferences = preferences;
        this.key = key;
        this.defaultValue = defaultValue;
        this.adapter = adapter;
        this.values = keyChanges //
                .filter(new Predicate<String>() {
                    @Override
                    public boolean test(String changedKey) {
                        return key.equals(changedKey);
                    }
                }) //
                .startWith("<init>") // Dummy value to trigger initial load.
                .map(new Function<String, T>() {
                    @Override
                    public T apply(String s) {
                        return get();
                    }
                });
    }

    @Override
    @NonNull
    public String key() {
        return key;
    }

    @Override
    @NonNull
    public T defaultValue() {
        return defaultValue;
    }

    @Override
    @NonNull
    public synchronized T get() {
        return adapter.get(key, preferences, defaultValue);
    }

    @Override
    public void set(@NonNull T value) {
        checkNotNull(value, "value == null");
        adapter.set(key, value, preferences);
    }

    @Override
    public boolean isSet() {
        return preferences.contains(key);
    }

    @Override
    public synchronized void delete() {
        preferences.remove(key);
    }

    @Override
    @CheckResult
    @NonNull
    public Observable<T> asObservable() {
        return values;
    }

    @Override
    @CheckResult
    @NonNull
    public Consumer<? super T> asConsumer() {
        return new Consumer<T>() {
            @Override
            public void accept(T value) {
                set(value);
            }
        };
    }
}

