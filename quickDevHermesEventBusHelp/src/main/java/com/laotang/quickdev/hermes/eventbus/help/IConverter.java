package com.laotang.quickdev.hermes.eventbus.help;

import androidx.annotation.NonNull;

public interface IConverter<T> {
    @NonNull
    T deserialize(@NonNull Object serialized);
}
