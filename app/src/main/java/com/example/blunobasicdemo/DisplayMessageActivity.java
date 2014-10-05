package com.example.blunobasicdemo;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Calendar;


public class DisplayMessageActivity extends Activity {
    private char[] uvTime = new char[3];
    private RadioButton swimChoice;
    private char[] skinScore = new char[2];
    private char[] uvIndex = new char[2];

    Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);
        Intent intent = getIntent();
        String msg = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        receiveResult(msg);
        getSkinScore(msg);
        getUvIndex(msg);
        getAlarmTime(msg);
    }

    public void receiveResult(String msg) {
        TextView resultDisplay;
        resultDisplay = (TextView) findViewById(R.id.resultDisplay);
        resultDisplay.append(msg);

    }

    public void getSkinScore(String msg) {
        TextView skinView;
        String skinType;
        int score;
        skinScore[0] = msg.charAt(0);
        skinScore[1] = msg.charAt(1);
        score = Integer.parseInt(new String(skinScore));
        if(score>-1) {
            skinType = "I";
        }
        else if(score>6) {
            skinType = "II";
        }
        else if(score>13) {
            skinType = "III";
        }
        else if(score>20) {
            skinType = "IV";
        }
        else if(score>27) {
            skinType = "V";
        }
        else {
            skinType = "VI";
        }
        skinView = (TextView) findViewById(R.id.skinType);
        skinView.append("Type "+skinType);
    }

    public void getUvIndex(String msg) {
        TextView uvView;
        String uvString;
        uvIndex[0] = msg.charAt(2);
        uvIndex[1] = msg.charAt(3);
        uvView = (TextView) findViewById(R.id.uvIndex);
        uvString = new String(uvIndex);
        uvView.append(uvString);
    }

    public void getAlarmTime(String msg) {
        int msgLength;
        final RadioGroup swimGroup;
        Button alarmBtn;

        msgLength = msg.length();
        uvTime[0] = msg.charAt(msgLength - 13);
        uvTime[1] = msg.charAt(msgLength - 12);
        uvTime[2] = msg.charAt(msgLength - 11);
        Toast.makeText(this, String.valueOf(uvTime), Toast.LENGTH_SHORT).show();

        swimGroup = (RadioGroup) findViewById(R.id.isSwimming);


        alarmBtn = (Button) findViewById(R.id.makeAlarm);
        alarmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedId = swimGroup.getCheckedRadioButtonId();
                swimChoice = (RadioButton) findViewById(selectedId);
                double multiplier;
                if(swimChoice.getText().equals("No")) {
                    multiplier = 1.0;
                }
                else {
                    multiplier = 1.5;
                }
                int uvExp = Integer.parseInt(new String(uvTime));
                double alarmDur = uvExp * multiplier + 0.5;
                setAlarm((int)alarmDur);
            }
        });
    }

     public void setAlarm(int time) {
         // When the alarm goes off, we want to broadcast an Intent to our
         // BroadcastReceiver. Here we make an Intent with an explicit class
         // name to have our own receiver (which has been published in
         // AndroidManifest.xml) instantiated and called, and then create an
         // IntentSender to have the intent executed as a broadcast.
         Intent intent = new Intent(DisplayMessageActivity.this, OneShotAlarm.class);
         PendingIntent sender = PendingIntent.getBroadcast(DisplayMessageActivity.this, 0,
                intent, 0);
         // We want the alarm to go off 30 seconds from now.
         Calendar calendar = Calendar.getInstance();
         calendar.setTimeInMillis(System.currentTimeMillis());
         calendar.add(Calendar.SECOND, time);
         // Schedule the alarm!
         AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
         am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
         // Tell the user about what we did.
         if (mToast != null) {
             mToast.cancel();
         }
         mToast = Toast.makeText(DisplayMessageActivity.this, String.valueOf(time),
                 Toast.LENGTH_SHORT);
         mToast.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.display_message, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }
}
