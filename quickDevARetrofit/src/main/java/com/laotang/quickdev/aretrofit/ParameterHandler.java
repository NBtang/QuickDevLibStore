package com.laotang.quickdev.aretrofit;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;

import static com.laotang.quickdev.aretrofit.Utils.checkNotNull;

abstract class ParameterHandler<T> {
    abstract void apply(RequestBuilder builder, @Nullable T value);

    static final class From extends ParameterHandler<Context> {

        @Override
        void apply(RequestBuilder builder, @Nullable Context value) {
            checkNotNull(value, "@From parameter is null.");
            builder.setContext(value);
        }
    }

    static final class Bundles extends ParameterHandler<Bundle> {

        @Override
        void apply(RequestBuilder builder, @Nullable Bundle value) {
            checkNotNull(value, "@Bundles parameter is null.");
            builder.putExtras(value);
        }
    }

    static final class IsGreenChannel extends ParameterHandler<Boolean> {

        @Override
        void apply(RequestBuilder builder, @Nullable Boolean value) {
            checkNotNull(value, "@Bundles parameter is null.");
            builder.isGreenChannel = value;
        }
    }

    static final class RelativeUrl extends ParameterHandler<String> {

        @Override
        void apply(RequestBuilder builder, @Nullable String value) {
            checkNotNull(value, "@Url parameter is null.");
            builder.setRelativeUrl(value);
        }
    }


    static final class ExtraIterable extends ParameterHandler<Iterable<Object>> {
        private final String name;
        private final Type iterableType;

        ExtraIterable(String name, Type iterableType) {
            this.name = name;
            this.iterableType = iterableType;
        }


        @Override
        void apply(RequestBuilder builder, @Nullable Iterable<Object> values) {
            if (values == null) return; // Skip null values.

            if (Integer.class == iterableType) {
                ArrayList<Integer> list = new ArrayList<>();
                for (Object value : values) {
                    list.add((Integer) value);
                }
                builder.putIntegerArrayListExtras(name, list);
            } else if (CharSequence.class == iterableType) {
                ArrayList<CharSequence> list = new ArrayList<>();
                for (Object value : values) {
                    list.add((CharSequence) value);
                }
                builder.putCharSequenceArrayListExtras(name, list);
            } else if (String.class == iterableType) {
                ArrayList<String> list = new ArrayList<>();
                for (Object value : values) {
                    list.add((String) value);
                }
                builder.putStringArrayListExtras(name, list);
            } else if (Parcelable.class == iterableType) {
                ArrayList<Parcelable> list = new ArrayList<>();
                for (Object value : values) {
                    list.add((Parcelable) value);
                }
                builder.putExtras(name, list);
            }
        }
    }

    static final class ExtraArray extends ParameterHandler<Object> {

        private final String name;
        private final Type arrayComponentType;

        ExtraArray(String name, Type arrayComponentType) {
            this.name = name;
            this.arrayComponentType = arrayComponentType;
        }

        @Override
        void apply(RequestBuilder builder, @Nullable Object values) {
            if (values == null) return; // Skip null values.

            if (Integer.class == arrayComponentType) {
                builder.putExtras(name, (int[]) values);
            } else if (Long.class == arrayComponentType) {
                builder.putExtras(name, (long[]) values);
            } else if (Boolean.class == arrayComponentType) {
                builder.putExtras(name, (boolean[]) values);
            } else if (Byte.class == arrayComponentType) {
                builder.putExtras(name, (byte[]) values);
            } else if (Character.class == arrayComponentType) {
                builder.putExtras(name, (char[]) values);
            } else if (Short.class == arrayComponentType) {
                builder.putExtras(name, (short[]) values);
            } else if (Float.class == arrayComponentType) {
                builder.putExtras(name, (float[]) values);
            } else if (Double.class == arrayComponentType) {
                builder.putExtras(name, (double[]) values);
            } else if (CharSequence.class == arrayComponentType) {
                builder.putExtras(name, (CharSequence[]) values);
            } else if (String.class == arrayComponentType) {
                builder.putExtras(name, (String[]) values);
            } else if (Parcelable.class == arrayComponentType) {
                builder.putExtras(name, (Parcelable[]) values);
            } else if (Serializable.class == arrayComponentType) {
                builder.putExtras(name, (Serializable[]) values);
            }
        }
    }

    static final class JsonBody<T> extends ParameterHandler<T> {
        private final String name;
        private final Converter<T, String> converter;

        JsonBody(String name,Converter<T, String> converter){
            this.name = name;
            this.converter = converter;
        }

        @Override
        void apply(RequestBuilder builder, @Nullable T value) {
            if (value == null) return; // Skip null values.
            String jsonBody;
            try {
                jsonBody = converter.convert(value);
            } catch (Exception e) {
                throw new RuntimeException("Unable to convert " + value + " to String", e);
            }
            builder.putExtras(name,jsonBody);
        }
    }

    static final class Extra extends ParameterHandler<Object> {

        private final String name;
        private final Type rawParameterType;

        Extra(String name, Type rawParameterType) {
            this.name = name;
            this.rawParameterType = rawParameterType;
        }

        @Override
        void apply(RequestBuilder builder, @Nullable Object value) {
            if (value == null) return; // Skip null values.

            if (Integer.class == rawParameterType) {
                builder.putExtras(name, (int) value);
            } else if (Long.class == rawParameterType) {
                builder.putExtras(name, (long) value);
            } else if (Boolean.class == rawParameterType) {
                builder.putExtras(name, (boolean) value);
            } else if (Byte.class == rawParameterType) {
                builder.putExtras(name, (byte) value);
            } else if (Character.class == rawParameterType) {
                builder.putExtras(name, (char) value);
            } else if (Short.class == rawParameterType) {
                builder.putExtras(name, (short) value);
            } else if (Float.class == rawParameterType) {
                builder.putExtras(name, (float) value);
            } else if (Double.class == rawParameterType) {
                builder.putExtras(name, (double) value);
            } else if (CharSequence.class == rawParameterType) {
                builder.putExtras(name, (CharSequence) value);
            } else if (String.class == rawParameterType) {
                builder.putExtras(name, (String) value);
            } else if (Parcelable.class == rawParameterType) {
                builder.putExtras(name, (Parcelable) value);
            } else if (Serializable.class == rawParameterType) {
                builder.putExtras(name, (Serializable) value);
            } else if (Bundle.class == rawParameterType) {
                builder.putExtras(name, (Bundle) value);
            }
        }
    }

    static final class ExtraParcelable extends ParameterHandler<Parcelable> {

        private final String name;

        ExtraParcelable(String name) {
            this.name = name;
        }

        @Override
        void apply(RequestBuilder builder, @Nullable Parcelable value) {
            if (value == null) return; // Skip null values.
            builder.putExtras(name, (Parcelable) value);
        }
    }

    static final class ExtraSerializable extends ParameterHandler<Serializable> {

        private final String name;

        ExtraSerializable(String name) {
            this.name = name;
        }

        @Override
        void apply(RequestBuilder builder, @Nullable Serializable value) {
            if (value == null) return; // Skip null values.
            builder.putExtras(name, (Serializable) value);
        }
    }
}
