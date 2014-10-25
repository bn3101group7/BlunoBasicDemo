package com.example.blunobasicdemo;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BlunoLibrary {
    private TextView serialReceivedText;
    private Spinner skinSpinner;
    private Spinner eyeSpinner;
    private Spinner hairSpinner;
    private Spinner frecklesSpinner;
    private Spinner burnSpinner;
    private Spinner brownFreqSpinner;
    private Spinner brownIntSpinner;
    private Spinner faceSpinner;
    private Spinner tanFreqSpinner;
    private Spinner tanHistSpinner;
    private PendingIntent pendingIntent;
    private AlarmManager manager;
    public TextView instructions;

    public boolean calState = false;

    private static final String[] skinText = {"Please choose your skin colour", "Pale", "Fair",
            "Beige", "Olive", "Brown", "Dark Brown"};
    private static final Integer[] skinTone = {R.drawable.blank, R.drawable.skin_1,
            R.drawable.skin_2,R.drawable.skin_3, R.drawable.skin_4, R.drawable.skin_5,
            R.drawable.skin_6};
    private static final String[] hairText = {"Please choose your hair colour", "Sandy red",
            "Blond", "Dark blond", "Chestnut", "Brown", "Black"};
    private static final Integer[] hairColour = {R.drawable.blank, R.drawable.hair_01,
            R.drawable.hair_02,R.drawable.hair_03, R.drawable.hair_04, R.drawable.hair_05,
            R.drawable.hair_06};
    private static final String[] eyeText = {"Please choose your natural eye colour", "Light blue",
            "Light green", "Light gray", "Blue", "Green", "Gray", "Dark blue", "Dark green",
            "Dark gray", "Brown", "Black"};
    private static final Integer[] eyeColour = {R.drawable.blank, R.drawable.eye_01,
            R.drawable.eye_02,R.drawable.eye_03, R.drawable.eye_04, R.drawable.eye_05,
            R.drawable.eye_06, R.drawable.eye_07,
            R.drawable.eye_08, R.drawable.eye_09, R.drawable.eye_10, R.drawable.eye_11};
    public final static String EXTRA_MESSAGE = "com.example.blunobasicdemo.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        onCreateProcess();    //onCreate Process by BlunoLibrary
        serialBegin(115200);    //set the Uart Baudrate on BLE chip to 115200
        //initial the EditText of the received data
        serialReceivedText = (TextView) findViewById(R.id.serialReceivedText);
        instructions = (TextView) findViewById(R.id.instructions);

        addItemsToEyeSpinner();
        addItemsToHairSpinner();
        addItemsToSkinSpinner();
        addItemsToFrecklesSpinner();
        addItemsToBurnSpinner();
        addItemsToBrownFreqSpinner();
        addItemsToBrownIntSpinner();
        addItemsToFaceSpinner();
        addItemsToTanFreqSpinner();
        addItemsToTanHistSpinner();

        addListenerToSpinner();

        SharedPreferences sharedPref = getSharedPreferences("MyPref", MODE_PRIVATE);
        int eyeValue = sharedPref.getInt("eye", -1);
        if (eyeValue != -1) {
            eyeSpinner.setSelection(eyeValue);
        }
        int hairValue = sharedPref.getInt("hair", -1);
        if (hairValue != -1) {
            hairSpinner.setSelection(hairValue);
        }
        int skinValue = sharedPref.getInt("skin", -1);
        if (skinValue != -1) {
            skinSpinner.setSelection(skinValue);
        }
        int frecValue = sharedPref.getInt("frec", -1);
        if (frecValue != -1) {
            frecklesSpinner.setSelection(frecValue);
        }
        int burnValue = sharedPref.getInt("burn", -1);
        if (burnValue != -1) {
            burnSpinner.setSelection(burnValue);
        }
        int brownFreqValue = sharedPref.getInt("brownFreq", -1);
        if (brownFreqValue != -1) {
            brownFreqSpinner.setSelection(brownFreqValue);
        }
        int brownIntValue = sharedPref.getInt("brownInt", -1);
        if (brownIntValue != -1) {
            brownIntSpinner.setSelection(brownIntValue);
        }
        int faceValue = sharedPref.getInt("face", -1);
        if (faceValue != -1) {
            faceSpinner.setSelection(faceValue);
        }
        int tanFreqValue = sharedPref.getInt("tanFreq", -1);
        if (tanFreqValue != -1) {
            tanFreqSpinner.setSelection(tanFreqValue);
        }
        int tanHistValue = sharedPref.getInt("tanHist", -1);
        if (tanHistValue != -1) {
            tanHistSpinner.setSelection(tanHistValue);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        if (mConnectionState.equals(connectionStateEnum.isNull) ||
                mConnectionState.equals(connectionStateEnum.isToScan)) {
            menu.findItem(R.id.menu_scan).setVisible(true);
            menu.findItem(R.id.menu_scanning).setVisible(false);
            menu.findItem(R.id.menu_connecting).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
            menu.findItem(R.id.menu_disconnecting).setVisible(false);
            menu.findItem(R.id.menu_refresh).setActionView(null);

            ActivateButton(false);
            instructions.setText("Please scan and connect device.");
        } else if (mConnectionState.equals(connectionStateEnum.isScanning)) {
            menu.findItem(R.id.menu_scan).setVisible(false);
            menu.findItem(R.id.menu_scanning).setVisible(true);
            menu.findItem(R.id.menu_connecting).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
            menu.findItem(R.id.menu_disconnecting).setVisible(false);
            menu.findItem(R.id.menu_refresh)
                    .setActionView(R.layout.actionbar_progress_indeterminate);

            ActivateButton(false);
        } else if (mConnectionState.equals(connectionStateEnum.isConnecting)) {
            menu.findItem(R.id.menu_scan).setVisible(false);
            menu.findItem(R.id.menu_scanning).setVisible(false);
            menu.findItem(R.id.menu_connecting).setVisible(true);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
            menu.findItem(R.id.menu_disconnecting).setVisible(false);
            menu.findItem(R.id.menu_refresh)
                    .setActionView(R.layout.actionbar_progress_indeterminate);

        } else if (mConnectionState.equals(connectionStateEnum.isConnected)) {
            menu.findItem(R.id.menu_scan).setVisible(false);
            menu.findItem(R.id.menu_scanning).setVisible(false);
            menu.findItem(R.id.menu_connecting).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(true);
            menu.findItem(R.id.menu_disconnecting).setVisible(false);
            menu.findItem(R.id.menu_refresh).setActionView(null);

            ActivateButton(true);
            instructions.setText("Please calibrate the device.");
        } else if (mConnectionState.equals(connectionStateEnum.isDisconnecting)) {
            menu.findItem(R.id.menu_scan).setVisible(false);
            menu.findItem(R.id.menu_scanning).setVisible(false);
            menu.findItem(R.id.menu_connecting).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
            menu.findItem(R.id.menu_disconnecting).setVisible(true);
            menu.findItem(R.id.menu_refresh)
                    .setActionView(R.layout.actionbar_progress_indeterminate);

            ActivateButton(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_scan:
                buttonScanOnClickProcess();
                break;
            case R.id.menu_scanning:
                buttonScanOnClickProcess();
                break;
            case R.id.menu_disconnect:
                buttonScanOnClickProcess();
        }
        return true;
    }

    protected void onResume() {
        super.onResume();
        System.out.println("BlUNOActivity onResume");
        onResumeProcess();    //onResume Process by BlunoLibrary
    }

    /**
     * populated the eye colour spinner
     */
    public void addItemsToEyeSpinner() {
        eyeSpinner = (Spinner) findViewById(R.id.eyeSpinner);
        eyeSpinner.setAdapter(new MyEyeAdapter(MainActivity.this, R.layout.multi_spinner_eye,
                eyeText));
    }

    /**
     * array adapter for eyeSpinner to display image and text in each spinner row
     */
    public class MyEyeAdapter extends ArrayAdapter {

        public MyEyeAdapter(Context context, int textViewResourceId,
                            String[] objects) {
            super(context, textViewResourceId, objects);
        }

        public View getCustomView(int position, View convertView,
                                  ViewGroup parent) {

            // Inflating the layout for the custom Spinner
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.multi_spinner_eye, parent, false);

            // Declaring and Typecasting the textview in the inflated layout
            TextView tvLanguage = (TextView) layout
                    .findViewById(R.id.tvLanguage);

            // Setting the text using the array
            tvLanguage.setText(eyeText[position]);

            // Declaring and Typecasting the imageView in the inflated layout
            ImageView img = (ImageView) layout.findViewById(R.id.imgLanguage);

            // Setting an image using the id's in the array
            img.setImageResource(eyeColour[position]);

            return layout;
        }

        // It gets a View that displays in the drop down popup the data at the specified position
        @Override
        public View getDropDownView(int position, View convertView,
                                    ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        // It gets a View that displays the data at the specified position
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }
    }

    /**
     * populates the hair colour spinner
     */
    public void addItemsToHairSpinner() {
        hairSpinner = (Spinner) findViewById(R.id.hairSpinner);
        hairSpinner.setAdapter(new MyHairAdapter(MainActivity.this, R.layout.multi_spinner_hair,
                hairText));
    }

    /**
     * array adapter for hairSpinner to display image and text in each spinner row
     */
    public class MyHairAdapter extends ArrayAdapter {

        public MyHairAdapter(Context context, int textViewResourceId,
                             String[] objects) {
            super(context, textViewResourceId, objects);
        }

        public View getCustomView(int position, View convertView,
                                  ViewGroup parent) {

            // Inflating the layout for the custom Spinner
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.multi_spinner_hair, parent, false);

            // Declaring and Typecasting the textview in the inflated layout
            TextView tvLanguage = (TextView) layout
                    .findViewById(R.id.tvLanguage);

            // Setting the text using the array
            tvLanguage.setText(hairText[position]);

            // Declaring and Typecasting the imageView in the inflated layout
            ImageView img = (ImageView) layout.findViewById(R.id.imgLanguage);

            // Setting an image using the id's in the array
            img.setImageResource(hairColour[position]);

            return layout;
        }

        // It gets a View that displays in the drop down popup the data at the specified position
        @Override
        public View getDropDownView(int position, View convertView,
                                    ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        // It gets a View that displays the data at the specified position
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }
    }

    /**
     * populates the skin colour spinner
     */
    public void addItemsToSkinSpinner() {
        skinSpinner = (Spinner) findViewById(R.id.skinSpinner);
        skinSpinner.setAdapter(new MySkinAdapter(MainActivity.this, R.layout.multi_spinner,
                skinText));
    }

    /**
     * array adapter for skinSpinner to display image and text in each spinner row
     */
    public class MySkinAdapter extends ArrayAdapter {

        public MySkinAdapter(Context context, int textViewResourceId,
                             String[] objects) {
            super(context, textViewResourceId, objects);
        }

        public View getCustomView(int position, View convertView,
                                  ViewGroup parent) {

            // Inflating the layout for the custom Spinner
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.multi_spinner, parent, false);

            // Declaring and Typecasting the textview in the inflated layout
            TextView tvLanguage = (TextView) layout
                    .findViewById(R.id.tvLanguage);

            // Setting the text using the array
            tvLanguage.setText(skinText[position]);


            // Declaring and Typecasting the imageView in the inflated layout
            ImageView img = (ImageView) layout.findViewById(R.id.imgLanguage);

            // Setting an image using the id's in the array
            img.setImageResource(skinTone[position]);

            return layout;
        }

        // It gets a View that displays in the drop down popup the data at the specified position
        @Override
        public View getDropDownView(int position, View convertView,
                                    ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        // It gets a View that displays the data at the specified position
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }
    }

    /**
     * populate the freckles spinner with text
     */
    public void addItemsToFrecklesSpinner() {
        frecklesSpinner = (Spinner) findViewById(R.id.frecklesSpinner);
        List<String> list = new ArrayList<String>();
        list.add("Number of freckles in unexposed areas");
        list.add("Many");
        list.add("Several");
        list.add("Few");
        list.add("Rare");
        list.add("None");
        ArrayAdapter<String> frecklesDataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        frecklesDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        frecklesSpinner.setAdapter(frecklesDataAdapter);
    }

    /**
     * populate the sun burn spinner with text
     */
    public void addItemsToBurnSpinner() {
        burnSpinner = (Spinner) findViewById(R.id.burnSpinner);
        List<String> list = new ArrayList<String>();
        list.add("What happens when you stay in the sun for too long?");
        list.add("Painful blisters, peeling");
        list.add("Mild blisters, peeling");
        list.add("Burn, mild peeling");
        list.add("Rare skin, no peeling");
        list.add("No burning");
        ArrayAdapter<String> burnDataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        burnDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        burnSpinner.setAdapter(burnDataAdapter);
    }

    /**
     * populate the brown frequency spinner with text
     */
    public void addItemsToBrownFreqSpinner() {
        brownFreqSpinner = (Spinner) findViewById(R.id.brownFreqSpinner);
        List<String> list = new ArrayList<String>();
        list.add("How often do you get tanned?");
        list.add("Never");
        list.add("Seldom");
        list.add("Sometimes");
        list.add("Often");
        list.add("Always");
        ArrayAdapter<String> brownFreqDataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        brownFreqDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        brownFreqSpinner.setAdapter(brownFreqDataAdapter);
    }

    /**
     * populate the brown intensity spinner with text
     */
    public void addItemsToBrownIntSpinner() {
        brownIntSpinner = (Spinner) findViewById(R.id.brownIntSpinner);
        List<String> list = new ArrayList<String>();
        list.add("How brown do you get?");
        list.add("Hardly or not at all");
        list.add("Light tan");
        list.add("Medium tan");
        list.add("Dark tan");
        list.add("Deep dark");
        ArrayAdapter<String> brownIntDataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        brownIntDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        brownIntSpinner.setAdapter(brownIntDataAdapter);
    }

    /**
     * populate the face sensitivity spinner with text
     */
    public void addItemsToFaceSpinner() {
        faceSpinner = (Spinner) findViewById(R.id.faceSpinner);
        List<String> list = new ArrayList<String>();
        list.add("How sensitive is your face to the sun?");
        list.add("Very sensitive");
        list.add("Sensitive");
        list.add("Sometimes");
        list.add("Resistant");
        list.add("Never had a problem");
        ArrayAdapter<String> faceDataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        faceDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        faceSpinner.setAdapter(faceDataAdapter);
    }

    /**
     * populate the tan frequency spinner with text
     */
    public void addItemsToTanFreqSpinner() {
        tanFreqSpinner = (Spinner) findViewById(R.id.tanFreqSpinner);
        List<String> list = new ArrayList<String>();
        list.add("How often do you tan?");
        list.add("Never");
        list.add("Seldom");
        list.add("Sometimes");
        list.add("Often");
        list.add("Always");
        ArrayAdapter<String> tanFreqDataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        tanFreqDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tanFreqSpinner.setAdapter(tanFreqDataAdapter);
    }

    /**
     * populate the tan history spinner with text
     */
    public void addItemsToTanHistSpinner() {
        tanHistSpinner = (Spinner) findViewById(R.id.tanHistSpinner);
        List<String> list = new ArrayList<String>();
        list.add("When was your last tan?");
        list.add(">3 months ago");
        list.add("2-3 months ago");
        list.add("1-2 months ago");
        list.add("A few weeks ago");
        list.add("A few days ago");
        ArrayAdapter<String> tanHistDataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        tanHistDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tanHistSpinner.setAdapter(tanHistDataAdapter);
    }

    /**
     * adds on item selected listeners to all spinners
     */
    public void addListenerToSpinner() {
        skinSpinner.setOnItemSelectedListener(new MyOnItemSelectedListener(this));
        eyeSpinner.setOnItemSelectedListener(new MyOnItemSelectedListener(this));
        hairSpinner.setOnItemSelectedListener(new MyOnItemSelectedListener(this));
        frecklesSpinner.setOnItemSelectedListener(new MyOnItemSelectedListener(this));
        burnSpinner.setOnItemSelectedListener(new MyOnItemSelectedListener(this));
        brownFreqSpinner.setOnItemSelectedListener(new MyOnItemSelectedListener(this));
        brownIntSpinner.setOnItemSelectedListener(new MyOnItemSelectedListener(this));
        faceSpinner.setOnItemSelectedListener(new MyOnItemSelectedListener(this));
        tanFreqSpinner.setOnItemSelectedListener(new MyOnItemSelectedListener(this));
        tanHistSpinner.setOnItemSelectedListener(new MyOnItemSelectedListener(this));
    }

    /**
     * get results from Bluno Nano
     * checks if questions are answered
     * checks if psi reading is correctly
     * prompts recalibration if necessary
     *
     * @param V view that was clicked
     */
    public void displayResults(View V) {
        char psiCheck;
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        TextView textView = (TextView) findViewById(R.id.serialReceivedText);
        if (textView.getText().toString().equals("")) {
            Toast.makeText(this, "Please select something", Toast.LENGTH_SHORT).show();
        } else {
            Button buttonSendData = (Button) findViewById(R.id.buttonSendData);
            Button buttonGetResult = (Button) findViewById(R.id.buttonDisplayResults);
            buttonSendData.setText("Calibrate");
            buttonSendData.setEnabled(true);
            String message = textView.getText().toString();
            psiCheck = message.charAt(7);
            if (Character.getNumericValue(psiCheck) == 9) {
                Toast.makeText(this, "Please re-calibrate device.", Toast.LENGTH_SHORT).show();
                calState = false;
                buttonSendData.setEnabled(true);
                buttonGetResult.setEnabled(false);
            } else {
                intent.putExtra(EXTRA_MESSAGE, message);
                startActivity(intent);
            }
        }
    }

    /**
     * get values from spinners and send as string array to Bluno Nano
     * changes button name between "calibrate" and "measure" depending on stage of calibration
     * process
     *
     * @param V view that was clicked
     */
    public void sendData(View V) {
        if ((eyeSpinner.getSelectedItemPosition() * hairSpinner.getSelectedItemPosition() *
                skinSpinner.getSelectedItemPosition() * frecklesSpinner.getSelectedItemPosition() *
                burnSpinner.getSelectedItemPosition() * brownFreqSpinner.getSelectedItemPosition() *
                brownIntSpinner.getSelectedItemPosition() * faceSpinner.getSelectedItemPosition() *
                tanFreqSpinner.getSelectedItemPosition() * tanHistSpinner.getSelectedItemPosition())
                != 0) {
            serialReceivedText.getEditableText().clear();
            int eyeVal = eyeSpinner.getSelectedItemPosition();
            int eyeOut;
            String calibrate;
            if (eyeVal > 0 && eyeVal < 4) {
                eyeOut = 1;
            } else if (eyeVal > 3 && eyeVal < 7) {
                eyeOut = 2;
            } else if (eyeVal > 6 && eyeVal < 10) {
                eyeOut = 3;
            } else if (eyeVal == 10) {
                eyeOut = 4;
            } else if (eyeVal == 11) {
                eyeOut = 5;
            } else {
                eyeOut = 6;
            }
            if (!calState) {
                calibrate = "0";
                calState = true;
                final Button buttonSendData = (Button) findViewById(R.id.buttonSendData);
                buttonSendData.setText("Measure");
                Toast.makeText(this, "Calibrated!", Toast.LENGTH_SHORT).show();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Do something after 5s = 5000ms
                        instructions.setText("Please press the \"Measure\" button.");
                        buttonSendData.setEnabled(true);
                    }
                }, 5000);
                instructions.setText("Please slide both doors open for 30 seconds.");
                buttonSendData.setEnabled(false);
            } else {
                calibrate = "1";
                calState = false;
                Button buttonSendData = (Button) findViewById(R.id.buttonSendData);
                buttonSendData.setEnabled(false);
                Button buttonDisplayResults = (Button) findViewById(R.id.buttonDisplayResults);
                buttonDisplayResults.setEnabled(true);
                instructions.setText("Please press the \"Get Results\" button.");
            }
            serialSend(String.valueOf(eyeOut) +
                    String.valueOf(hairSpinner.getSelectedItemPosition()) +
                    String.valueOf(skinSpinner.getSelectedItemPosition()) +
                    String.valueOf(frecklesSpinner.getSelectedItemPosition()) +
                    String.valueOf(burnSpinner.getSelectedItemPosition()) +
                    String.valueOf(brownFreqSpinner.getSelectedItemPosition()) +
                    String.valueOf(brownIntSpinner.getSelectedItemPosition()) +
                    String.valueOf(faceSpinner.getSelectedItemPosition()) +
                    String.valueOf(tanFreqSpinner.getSelectedItemPosition()) +
                    String.valueOf(tanHistSpinner.getSelectedItemPosition()) +
                    calibrate);
        } else {
            Toast.makeText(this, "Please select an option for each category.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * cancels existing alarm
     */
    public void cancelAlarm(View view) {
        Intent alarmIntent = new Intent(this, OneShotAlarm.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
        manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntent);
        Toast.makeText(this, "Alarm canceled", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //onActivityResult Process by BlunoLibrary
        onActivityResultProcess(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        onPauseProcess();    //onPause Process by BlunoLibrary
    }

    protected void onStop() {
        super.onStop();
        onStopProcess();    //onStop Process by BlunoLibrary
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        onDestroyProcess();    //onDestroy Process by BlunoLibrary
    }

    @Override
    //Once connection data received, this function will be called
    public void onSerialReceived(String theString) {
        serialReceivedText.append(theString);    //append the text into the EditText
        final Toast toast = Toast.makeText(this, "Sending data...", Toast.LENGTH_SHORT);
        toast.show();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toast.cancel();
            }
        }, 500);
    }

    /**
     * This functions will enable or disable the buttons depending on the bluetooth connectivity
     * status.
     * @param trueFalse boolean variable representing bluetooth connection status of app
     *                  true is connected, false is disconnected
     */
    public void ActivateButton(boolean trueFalse) {
        Button buttonDisplayResults = (Button) findViewById(R.id.buttonDisplayResults);
        Button buttonSendData = (Button) findViewById(R.id.buttonSendData);

        if (trueFalse) {
            buttonSendData.setEnabled(true);
            buttonSendData.setText("Calibrate");
            calState = false;
        } else {
            buttonDisplayResults.setEnabled(false);
            buttonSendData.setEnabled(false);
            buttonSendData.setText("Calibrate");
            calState = false;
        }
    }
}