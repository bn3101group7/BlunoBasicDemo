package com.example.blunobasicdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class OneShotAlarm extends BroadcastReceiver {
    public OneShotAlarm() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "one_shot_received", Toast.LENGTH_SHORT).show();
    }
}
