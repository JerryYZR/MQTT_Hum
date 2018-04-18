package com.example.mqtt_test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import static android.content.ContentValues.TAG;

public class MyBroadcastReceiver extends BroadcastReceiver {

    public static final String TAG = "MyBroadcastReceiver";
    public static int m = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.w(TAG, "intent:" + intent);
        String name = intent.getStringExtra("name");
        Log.w(TAG, "name:" + name + " m=" + m);
        m++;

        Bundle bundle = intent.getExtras();
    }
}
