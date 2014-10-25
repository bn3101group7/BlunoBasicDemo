package com.example.blunobasicdemo;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class DisplayMessageActivity extends Activity {
    private char[] uvTime = new char[3];
    private char[] skinScore = new char[2];
    private char[] uvIndex = new char[2];
    private char psiLvl;
    private Spinner spfSpinner;
    private TextView uvTimeView;
    private EditText spfVal;
    public int uvTimeInt;
    public double multiplier = 1.0;
    public int spfMultiplier = 1;
    public boolean spfEntry = true;
    public double uvExp;
    public String spfFact = "";
    Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);
        Intent intent = getIntent();
        String msg = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        uvTimeView = (TextView) findViewById(R.id.uvExp);
        spfVal = (EditText) findViewById(R.id.spfVal);

        getSkinScore(msg);
        getUvIndex(msg);
        getAlarmTime(msg);
        getPsiLvl(msg);
        addItemsToSpfSpinner();
        addListenerToSpfSpinner();
    }

    /**
     * This function will receive the skin score from Bluno Nano in the a char array of size 2
     * and compute the skin type based on the fitzpatrick skin type chart
     */
    public void getSkinScore(String msg) {
        TextView skinView;
        String skinType;
        int score;
        skinScore[0] = msg.charAt(0);
        skinScore[1] = msg.charAt(1);
        score = Integer.parseInt(new String(skinScore));
        if (score > -1) {
            skinType = "I";
        } else if (score > 6) {
            skinType = "II";
        } else if (score > 13) {
            skinType = "III";
        } else if (score > 20) {
            skinType = "IV";
        } else if (score > 27) {
            skinType = "V";
        } else {
            skinType = "VI";
        }
        skinView = (TextView) findViewById(R.id.skinType);
        skinView.setText("Type " + skinType);
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

    /**
     * Calculates uv Exposure display based on the swimming condition
     * sets alarm
     */

    public void getAlarmTime(String msg) {
        final Switch swimSwitch;
        LinearLayout alarmBtn;
        String uvTimeStr;

        uvTime[0] = msg.charAt(4);
        uvTime[1] = msg.charAt(5);
        uvTime[2] = msg.charAt(6);
        uvTimeStr = new String(uvTime);
        uvTimeInt = Integer.parseInt(uvTimeStr);
        uvTimeView.setText(String.valueOf(uvTimeInt));

        //Toast.makeText(this, String.valueOf(uvTime), Toast.LENGTH_SHORT).show();

        swimSwitch = (Switch) findViewById(R.id.isSwim);
        swimSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    multiplier = 1.5;
                    uvExp = uvTimeInt / multiplier * spfMultiplier + 0.5;
                    uvTimeView.setText(String.valueOf((int) uvExp));
                    uvTimeView.setTextColor(Color.RED);
                } else {
                    multiplier = 1.0;
                    uvExp = uvTimeInt / multiplier * spfMultiplier;
                    uvTimeView.setText(String.valueOf((int) uvExp));
                    uvTimeView.setTextColor(Color.BLACK);
                }
            }
        });

        alarmBtn = (LinearLayout) findViewById(R.id.makeAlarm);
        alarmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int alarmDur = Integer.parseInt(uvTimeView.getText().toString());
                switch (spfSpinner.getSelectedItemPosition()) {
                    case 0:
                        spfFact = "0";
                        break;
                    case 1:
                        spfFact = "15";
                        break;
                    case 2:
                        spfFact = "30";
                        break;
                    case 3:
                        spfFact = "50";
                        break;
                    case 4:
                        spfFact = "100";
                        break;
                    case 5:
                        spfFact = spfVal.getText().toString();
                        break;
                }
                if (spfEntry) {
                    setAlarm(alarmDur, spfFact);
                } else {
                    Toast.makeText(getApplicationContext(), "Please select a valid SPF value",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * get input from Bluno Nano and displays PSI range
     */

    public void getPsiLvl(String msg) {
        TextView psiView;
        psiLvl = msg.charAt(7);
        String psiRange;
        switch (Character.getNumericValue(psiLvl)) {
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

    /*
     * populates spinner for SPF values of sunblock
     */

    public void addItemsToSpfSpinner() {
        spfSpinner = (Spinner) findViewById(R.id.spfSpinner);
        List<String> list = new ArrayList<String>();
        list.add("Didn't apply");
        list.add("SPF 15");
        list.add("SPF 30");
        list.add("SPF 50");
        list.add("SPF 100");
        list.add("Others");
        ArrayAdapter<String> spfDataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        spfDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spfSpinner.setAdapter(spfDataAdapter);
    }

    /**
     * adds OnItemSelectedListener to SPF spinner, toggles visibility of EditText for manual entry
     * of SPF value if "Others" is selected
     */

    public void addListenerToSpfSpinner() {
        spfSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                TableRow spfRow = (TableRow) findViewById(R.id.spfRow);
                if (pos == 5) {
                    spfRow.setVisibility(View.VISIBLE);
                    spfEntry = false;
                    spfVal.setOnKeyListener(new EditText.OnKeyListener() {
                        @Override
                        public boolean onKey(View view, int arg1, KeyEvent arg2) {
                            if (!spfVal.getText().toString().equals("")) {
                                double uvExpDbl;
                                int totalDur;
                                spfMultiplier = Integer.parseInt(spfVal.getText().toString());
                                uvExpDbl = uvTimeInt / multiplier + 0.5;
                                if (spfMultiplier < 151 && spfMultiplier > 1) {
                                    totalDur = (int) (uvExpDbl) * spfMultiplier;
                                    uvTimeView.setText(String.valueOf(totalDur));
                                    spfEntry = true;
                                } else {
                                    spfEntry = false;
                                    Toast.makeText(getApplicationContext(),
                                            "Please enter a valid SPF value", Toast.LENGTH_SHORT)
                                            .show();
                                }
                            } else {
                                spfEntry = false;
                                Toast.makeText(getApplicationContext(),
                                        "Please enter a valid non-zero value", Toast.LENGTH_SHORT)
                                        .show();
                            }
                            return false;
                        }
                    });
                } else {
                    spfEntry = true;
                    spfRow.setVisibility(View.GONE);
                    switch (pos) {
                        case 0:
                            spfMultiplier = 1;
                            break;
                        case 1:
                            spfMultiplier = 15;
                            break;
                        case 2:
                            spfMultiplier = 30;
                            break;
                        case 3:
                            spfMultiplier = 50;
                            break;
                        case 4:
                            spfMultiplier = 100;
                            break;
                        default:
                            spfMultiplier = 1;
                            break;
                    }
                    int totalDur;
                    double uvExpDbl;
                    uvExpDbl = uvTimeInt / multiplier + 0.5;
                    totalDur = (int) (uvExpDbl) * spfMultiplier;
                    uvTimeView.setText(String.valueOf(totalDur));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    /**
     * sets the alarm, which is non-repeating
     */

    public void setAlarm(int time, String spfFact) {
        // When the alarm goes off, we want to broadcast an Intent to our
        // BroadcastReceiver. Here we make an Intent with an explicit class
        // name to have our own receiver (which has been published in
        // AndroidManifest.xml) instantiated and called, and then create an
        // IntentSender to have the intent executed as a broadcast.
        Intent intent = new Intent(DisplayMessageActivity.this, OneShotAlarm.class);
        intent.putExtra("spfAppl", spfFact);
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
        mToast = Toast.makeText(DisplayMessageActivity.this, "Alarm set for " + String.valueOf(time) +
                " minutes.", Toast.LENGTH_SHORT);
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
