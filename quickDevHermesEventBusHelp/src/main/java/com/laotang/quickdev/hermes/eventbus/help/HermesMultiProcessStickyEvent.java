package com.laotang.quickdev.hermes.eventbus.help;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

public class HermesMultiProcessStickyEvent {

    private HermesMultiProcessStickyEvent() {
        throw new RuntimeException("HermesMultiProcessStickyEvent instance error");
    }

    public static Intent create(String tag, Serializable eventSerializable) {
        Intent intent = new Intent();
        intent.putExtra("tag", tag);
        intent.putExtra("sticky", true);
        intent.putExtra("event", eventSerializable);
        return intent;
    }

    public static Intent create(String tag, Parcelable eventParcelable) {
        Intent intent = new Intent();
        intent.putExtra("tag", tag);
        intent.putExtra("sticky", true);
        intent.putExtra("event", eventParcelable);
        return intent;
    }

    public static Intent create(String tag, String event) {
        Intent intent = new Intent();
        intent.putExtra("tag", tag);
        intent.putExtra("sticky", true);
        intent.putExtra("event", event);
        return intent;
    }

    public static Intent create(String tag, boolean event) {
        Intent intent = new Intent();
        intent.putExtra("tag", tag);
        intent.putExtra("sticky", true);
        intent.putExtra("event", event);
        return intent;
    }

    public static Intent create(String tag, int event) {
        Intent intent = new Intent();
        intent.putExtra("tag", tag);
        intent.putExtra("sticky", true);
        intent.putExtra("event", event);
        return intent;
    }

    public static Intent create(String tag, long event) {
        Intent intent = new Intent();
        intent.putExtra("tag", tag);
        intent.putExtra("sticky", true);
        intent.putExtra("event", event);
        return intent;
    }

    public static Intent create(String tag, ArrayList<String> event) {
        Intent intent = new Intent();
        intent.putExtra("tag", tag);
        intent.putExtra("sticky", true);
        intent.putExtra("event", event);
        return intent;
    }
}
