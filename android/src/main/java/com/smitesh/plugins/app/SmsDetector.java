package com.smitesh.plugins.app;

import android.util.Log;

public class SmsDetector {
    private static final String TAG = "SmsDetector";

    public String echo(String value) {
        Log.i(TAG, "Echo: " + value);
        return value;
    }
}
