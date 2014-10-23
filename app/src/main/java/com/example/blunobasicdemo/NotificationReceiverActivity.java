package com.example.blunobasicdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

/**
 * creates notification
 */
public class NotificationReceiverActivity extends Activity {

    TextView spfAdv;
    Boolean spfAppl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_receiver);
        Intent intent = getIntent();
        spfAppl = intent.getExtras().getBoolean("spfAppl");
        spfMessage(spfAppl);
    }

    public void spfMessage(Boolean spfAppl) {
        if(!spfAppl) {
            spfAdv = (TextView) findViewById(R.id.spfKnow);
            spfAdv.setText("You can prolong your UV exposure duration by applying sunblock.\n\nNew Exposure " +
                    "duration = Baseline duration x SPF factor");
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
