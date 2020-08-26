package com.laotang.quickdev.hermes.eventbus.help;


final class ConverterAdapter<T> {
    private final IConverter<T> IConverter;

    ConverterAdapter(IConverter<T> IConverter) {
        this.IConverter = IConverter;
    }

    T get(Object eventObject) {
        return IConverter.deserialize(eventObject);
    }
}
