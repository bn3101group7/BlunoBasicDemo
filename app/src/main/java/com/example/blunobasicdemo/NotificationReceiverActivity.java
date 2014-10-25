package com.example.blunobasicdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

/**
 * creates notification
 */
public class NotificationReceiverActivity extends Activity {

    TextView spfAdv;
    public String spfFactor;
    String TAG = "alarm";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_receiver);
        Log.d(TAG, "setcontentview");
        Intent intent = getIntent();
        Log.d(TAG, "getintent");
        spfFactor = intent.getExtras().getString("spfAppl");
        if (spfFactor == null) {
            Log.d(TAG, "null");
        } else if (spfFactor.equals("")) {
            Log.d(TAG, "empty");
        }
        Log.d(TAG, "getextras");
        spfMessage(spfFactor);

    }

    /**
     * display warning message/advice to user after alarm is triggered
     *
     * @param spfFact SPF factor sent through intent
     */
    public void spfMessage(String spfFact) {
        spfAdv = (TextView) findViewById(R.id.spfKnow);
        if (spfFact.equals("0")) {
            spfAdv.setText("You can prolong your UV exposure duration by applying sunblock.\n\n" +
                    "New Exposure " + "duration = Baseline duration x SPF factor");
        } else {
            spfAdv.setText("By applying sunblock, you have prolonged your UV exposure by " +
                    spfFactor + " times.");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.notification_receiver, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
