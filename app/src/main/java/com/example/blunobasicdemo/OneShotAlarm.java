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
    private String spfFact;

    public OneShotAlarm() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "gCactus Alarm triggered", Toast.LENGTH_SHORT).show();
        spfFact = intent.getExtras().getString("spfAppl");
        showNotification(context);
    }

    /**
     * customizes the type of alarm and notification when alarm is triggered
     */
    private void showNotification(Context context) {
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        long[] pattern = {500, 500, 500, 500, 500, 500, 500, 500, 500, 500};
        Intent contentIntent = new Intent(context, NotificationReceiverActivity.class);
        contentIntent.putExtra("spfAppl", spfFact);
        PendingIntent pIntent = PendingIntent.getActivity(context, 1, contentIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        Notification noti = new Notification.Builder(context)
                .setContentTitle("gCactus alarm")
                .setContentText("You should head indoors now.").setSmallIcon(R.drawable.logo)
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
