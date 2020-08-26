package com.laotang.quickdev.hermes.eventbus.help;

import android.os.Bundle;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

public class HermesMultiProcessEvent {

    private HermesMultiProcessEvent() {
        throw new RuntimeException("HermesMultiProcessEvent instance error");
    }

    public static Bundle create(String tag, Serializable eventSerializable) {
        Bundle bundle = new Bundle();
        bundle.putString("tag", tag);
        bundle.putBoolean("sticky", false);
        bundle.putSerializable("event", eventSerializable);
        return bundle;
    }

    public static Bundle create(String tag, Parcelable eventParcelable) {
        Bundle bundle = new Bundle();
        bundle.putString("tag", tag);
        bundle.putBoolean("sticky", false);
        bundle.putParcelable("event", eventParcelable);
        return bundle;
    }

    public static Bundle create(String tag, String event) {
        Bundle bundle = new Bundle();
        bundle.putString("tag", tag);
        bundle.putBoolean("sticky", false);
        bundle.putString("event", event);
        return bundle;
    }

    public static Bundle create(String tag, boolean event) {
        Bundle bundle = new Bundle();
        bundle.putString("tag", tag);
        bundle.putBoolean("sticky", false);
        bundle.putBoolean("event", event);
        return bundle;
    }

    public static Bundle create(String tag, int event) {
        Bundle bundle = new Bundle();
        bundle.putString("tag", tag);
        bundle.putBoolean("sticky", false);
        bundle.putInt("event", event);
        return bundle;
    }

    public static Bundle create(String tag, long event) {
        Bundle bundle = new Bundle();
        bundle.putString("tag", tag);
        bundle.putBoolean("sticky", false);
        bundle.putLong("event", event);
        return bundle;
    }

    public static Bundle create(String tag, ArrayList<String> event) {
        Bundle bundle = new Bundle();
        bundle.putString("tag", tag);
        bundle.putBoolean("sticky", false);
        bundle.putStringArrayList("event", event);
        return bundle;
    }
}
