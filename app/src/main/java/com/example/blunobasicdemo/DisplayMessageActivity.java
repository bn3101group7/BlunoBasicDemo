package com.example.blunobasicdemo;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class DisplayMessageActivity extends Activity {
    private char[] uvTime = new char[3];
    private char[] skinScore = new char[2];
    private char[] uvIndex = new char[2];
    private char psiLvl;
    private Spinner spfSpinner;

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
        getPsiLvl(msg);
        addItemsToSpfSpinner();
        addListenerToSpfSpinner();
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
        skinView.setText("Type "+skinType);
    }

    public void getUvIndex(String msg) {
        TextView uvView;
        String uvString;
        int uvInt;
        uvIndex[0] = msg.charAt(2);
        uvIndex[1] = msg.charAt(3);
        uvView = (TextView) findViewById(R.id.uvIndex);
        uvString = new String(uvIndex);
        uvInt = Integer.parseInt(uvString);
        uvView.setText(String.valueOf(uvInt));
    }

    public void getAlarmTime(String msg) {
        final Switch swimSwitch;
        LinearLayout alarmBtn;
        String uvTimeStr;
        final int uvTimeInt;
        TextView uvTimeView;

        uvTime[0] = msg.charAt(4);
        uvTime[1] = msg.charAt(5);
        uvTime[2] = msg.charAt(6);
        uvTimeStr = new String(uvTime);
        uvTimeInt = Integer.parseInt(uvTimeStr);
        uvTimeView = (TextView)findViewById(R.id.uvExp);
        uvTimeView.setText(String.valueOf(uvTimeInt)+" minutes");

        Toast.makeText(this, String.valueOf(uvTime), Toast.LENGTH_SHORT).show();

        swimSwitch = (Switch) findViewById(R.id.isSwim);
        swimSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                TextView uvTimeView = (TextView) findViewById(R.id.uvExp);
                if(isChecked) {
                    uvTimeView.setText(String.valueOf((int)(uvTimeInt/1.5))+" minutes");
                    uvTimeView.setTextColor(Color.RED);
                }
                else {
                    uvTimeView.setText(String.valueOf(uvTimeInt)+" minutes");
                    uvTimeView.setTextColor(Color.BLACK);
                }
            }
        });

        alarmBtn = (LinearLayout) findViewById(R.id.makeAlarm);
        alarmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double multiplier;
                if(swimSwitch.isChecked()) {
                    multiplier = 1.5;
                }
                else {
                    multiplier = 1.0;
                }
                int uvExp = Integer.parseInt(new String(uvTime));
                double alarmDur = uvExp / multiplier + 0.5;
                setAlarm((int)alarmDur);
            }
        });
    }

    public void getPsiLvl(String msg) {
        TextView psiView;
        psiLvl = msg.charAt(7);
        String psiRange;
        switch(Character.getNumericValue(psiLvl)) {
            case 1:
                psiRange = "Low";
                break;
            case 2:
                psiRange = "Moderate";
                break;
            case 3:
                psiRange = "Dangerous";
                break;
            default:
                psiRange = "Error";
                break;
        }
        psiView = (TextView) findViewById(R.id.psiLvl);
        psiView.setText(psiRange);
    }

    public void addItemsToSpfSpinner() {
        spfSpinner = (Spinner) findViewById(R.id.spfSpinner);
        List<String> list = new ArrayList<String>();
        list.add("Didn't apply");
        list.add("SPF 15");
        list.add("SPF 30");
        list.add("SPF 50");
        list.add("SPF 100");
        list.add("Others");
        ArrayAdapter<String> spfDataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        spfDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spfSpinner.setAdapter(spfDataAdapter);
    }

    public void addListenerToSpfSpinner() {
        spfSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                TableRow spfRow = (TableRow) findViewById(R.id.spfRow);
                if(pos == 5) {
                    spfRow.setVisibility(View.VISIBLE);
                }
                else {
                    spfRow.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

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
