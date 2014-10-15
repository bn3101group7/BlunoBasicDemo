package com.example.blunobasicdemo;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.widget.Toast;

public class OneShotAlarm extends BroadcastReceiver {
    NotificationManager nm;

    public OneShotAlarm() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        showNotification(context);
        Toast.makeText(context, "one_shot_received", Toast.LENGTH_SHORT).show();
    }

    /**
     * customizes the type of alarm and notification when alarm is triggered
     */
    private void showNotification(Context context) {
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        long[] pattern = {500,500,500,500,500,500,500,500,500,500};
        Intent contentIntent = new Intent(context, NotificationReceiverActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(context, 1, contentIntent, 0);
        Notification noti = new Notification.Builder(context)
                .setContentTitle("UV Exposure Warning")
                .setContentText("You should head indoors now").setSmallIcon(R.drawable.ic_launcher)
                .setContentIntent(pIntent)
                .setLights(0xff00ff00, 300, 1000)
                .setSound(uri)
                .setVibrate(pattern)
                .setAutoCancel(true)
                .build();
        nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(0, noti);
    }
}
