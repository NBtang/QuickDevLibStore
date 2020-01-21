package com.laotang.quickdev.rxactivity;

import android.content.Intent;

public class ActivityResultEntity {
    int requestCode;
    int resultCode;
    Intent data;

    public int getRequestCode() {
        return requestCode;
    }

    public int getResultCode() {
        return resultCode;
    }

    public Intent getData() {
        return data;
    }

}
