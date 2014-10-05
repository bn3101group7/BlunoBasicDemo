package com.example.blunobasicdemo;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
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

    private static final String[] skinText = {"Please choose your skin colour","Pale", "Fair", "Beige", "Olive", "Brown", "Dark Brown"};
    private static final Integer[] skinTone = {R.drawable.blank,R.drawable.skin_1,R.drawable.skin_2,
            R.drawable.skin_3,R.drawable.skin_4,R.drawable.skin_5,R.drawable.skin_6};
    private static final Integer[] hairColour = {R.drawable.blank,R.drawable.hair_1,R.drawable.hair_2,
            R.drawable.hair_3,R.drawable.hair_4,R.drawable.hair_5};
    private static final Integer[] eyeColour = {R.drawable.blank,R.drawable.eye_1,R.drawable.eye_2,
            R.drawable.eye_3,R.drawable.eye_4,R.drawable.eye_5};
    private static final Integer[] brownInt = {R.drawable.blank,R.drawable.brown_1,R.drawable.brown_2,
            R.drawable.brown_3,R.drawable.brown_4,R.drawable.brown_5};
    public final static String EXTRA_MESSAGE = "com.example.blunobasicdemo.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        onCreateProcess();	//onCreate Process by BlunoLibrary
        serialBegin(115200);	//set the Uart Baudrate on BLE chip to 115200
        serialReceivedText=(TextView) findViewById(R.id.serialReceivedText);	//initial the EditText of the received data

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

        SharedPreferences sharedPref = getSharedPreferences("MyPref",MODE_PRIVATE);
        int eyeValue = sharedPref.getInt("eye",-1);
        if(eyeValue != -1) {
            eyeSpinner.setSelection(eyeValue);
        }
        int hairValue = sharedPref.getInt("hair", -1);
        if(hairValue != -1) {
            hairSpinner.setSelection(hairValue);
        }
        int skinValue = sharedPref.getInt("skin",-1);
        if(skinValue != -1) {
            skinSpinner.setSelection(skinValue);
        }
        int frecValue = sharedPref.getInt("frec", -1);
        if(frecValue != -1) {
            frecklesSpinner.setSelection(frecValue);
        }
        int burnValue = sharedPref.getInt("burn", -1);
        if(burnValue != -1) {
            burnSpinner.setSelection(burnValue);
        }
        int brownFreqValue = sharedPref.getInt("brownFreq", -1);
        if(brownFreqValue != -1) {
            brownFreqSpinner.setSelection(brownFreqValue);
        }
        int brownIntValue = sharedPref.getInt("brownInt", -1);
        if(brownIntValue != -1) {
            brownIntSpinner.setSelection(brownIntValue);
        }
        int faceValue = sharedPref.getInt("face", -1);
        if(faceValue != -1) {
            faceSpinner.setSelection(faceValue);
        }
        int tanFreqValue = sharedPref.getInt("tanFreq", -1);
        if(tanFreqValue != -1) {
            tanFreqSpinner.setSelection(tanFreqValue);
        }
        int tanHistValue = sharedPref.getInt("tanHist", -1);
        if(tanHistValue != -1) {
            tanHistSpinner.setSelection(tanHistValue);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        if(mConnectionState.equals(connectionStateEnum.isNull)||mConnectionState.equals(connectionStateEnum.isToScan)){
            menu.findItem(R.id.menu_scan).setVisible(true);
            menu.findItem(R.id.menu_scanning).setVisible(false);
            menu.findItem(R.id.menu_connecting).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
            menu.findItem(R.id.menu_disconnecting).setVisible(false);
            menu.findItem(R.id.menu_refresh).setActionView(null);
        }
        else if(mConnectionState.equals(connectionStateEnum.isScanning)) {
            menu.findItem(R.id.menu_scan).setVisible(false);
            menu.findItem(R.id.menu_scanning).setVisible(true);
            menu.findItem(R.id.menu_connecting).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
            menu.findItem(R.id.menu_disconnecting).setVisible(false);
            menu.findItem(R.id.menu_refresh).setActionView(R.layout.actionbar_progress_indeterminate);
        }
        else if(mConnectionState.equals(connectionStateEnum.isConnecting)) {
            menu.findItem(R.id.menu_scan).setVisible(false);
            menu.findItem(R.id.menu_scanning).setVisible(false);
            menu.findItem(R.id.menu_connecting).setVisible(true);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
            menu.findItem(R.id.menu_disconnecting).setVisible(false);
            menu.findItem(R.id.menu_refresh).setActionView(R.layout.actionbar_progress_indeterminate);
        }
        else if(mConnectionState.equals(connectionStateEnum.isConnected)) {
            menu.findItem(R.id.menu_scan).setVisible(false);
            menu.findItem(R.id.menu_scanning).setVisible(false);
            menu.findItem(R.id.menu_connecting).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(true);
            menu.findItem(R.id.menu_disconnecting).setVisible(false);
            menu.findItem(R.id.menu_refresh).setActionView(null);

            ActivateButton(true);
        }
        else if(mConnectionState.equals(connectionStateEnum.isDisconnecting)) {
            menu.findItem(R.id.menu_scan).setVisible(false);
            menu.findItem(R.id.menu_scanning).setVisible(false);
            menu.findItem(R.id.menu_connecting).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
            menu.findItem(R.id.menu_disconnecting).setVisible(true);
            menu.findItem(R.id.menu_refresh).setActionView(R.layout.actionbar_progress_indeterminate);

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

    protected void onResume(){
        super.onResume();
        System.out.println("BlUNOActivity onResume");
        onResumeProcess();	//onResume Process by BlunoLibrary
    }

    public void addItemsToEyeSpinner() {
        eyeSpinner = (Spinner) findViewById(R.id.eyeSpinner);
        eyeSpinner.setAdapter(new MyEyeAdapter());
    }

    private static class EyeViewHolder {
        ImageView imageViewEye;
    }

    private class MyEyeAdapter extends BaseAdapter {
        public int getCount(){
            return eyeColour.length;
        }
        @Override
        public Integer getItem(int position) {
            return eyeColour[position];
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            EyeViewHolder eyeViewHolder;
            //do we have a view?
            if(convertView == null) {
                //we don't have a view so create one
                itemView = getLayoutInflater().inflate(R.layout.spinner_row,parent, false);
                eyeViewHolder = new EyeViewHolder();
                eyeViewHolder.imageViewEye = (ImageView) itemView.findViewById(R.id.spinnerImage);
                //set the tag for this view to the current image view holder
                itemView.setTag(eyeViewHolder);
            }
            else {
                //we have a view to get the tagged view
                eyeViewHolder = (EyeViewHolder) itemView.getTag();
            }
            //display the current image
            eyeViewHolder.imageViewEye.setImageDrawable(getResources().getDrawable(eyeColour[position]));
            return itemView;
        }
    }

    public void addItemsToHairSpinner() {
        hairSpinner = (Spinner) findViewById(R.id.hairSpinner);
        hairSpinner.setAdapter(new MyHairAdapter());
    }

    private static class HairViewHolder {
        ImageView imageViewHair;
    }

    private class MyHairAdapter extends BaseAdapter {
        public int getCount(){
            return hairColour.length;
        }
        @Override
        public Integer getItem(int position) {
            return hairColour[position];
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            HairViewHolder hairViewHolder;
            if(convertView == null) {
                itemView = getLayoutInflater().inflate(R.layout.spinner_row,parent, false);
                hairViewHolder = new HairViewHolder();
                hairViewHolder.imageViewHair = (ImageView) itemView.findViewById(R.id.spinnerImage);
                itemView.setTag(hairViewHolder);
            }
            else {
                hairViewHolder = (HairViewHolder) itemView.getTag();
            }
            hairViewHolder.imageViewHair.setImageDrawable(getResources().getDrawable(hairColour[position]));
            return itemView;
        }
    }

    public void addItemsToSkinSpinner() {
        skinSpinner = (Spinner) findViewById(R.id.skinSpinner);
        skinSpinner.setAdapter(new MySkinAdapter(MainActivity.this, R.layout.multi_spinner, skinText));
    }

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

            // Setting the color of the text
            //tvLanguage.setTextColor(Color.rgb(75, 180, 225));

            // Declaring and Typecasting the imageView in the inflated layout
            ImageView img = (ImageView) layout.findViewById(R.id.imgLanguage);

            // Setting an image using the id's in the array
            img.setImageResource(skinTone[position]);

            // Setting Special attributes for 1st element
            /*
            if (position == 0) {
                // Removing the image view
                img.setVisibility(View.GONE);
                // Setting the size of the text
                tvLanguage.setTextSize(20f);
                // Setting the text Color
                tvLanguage.setTextColor(Color.BLACK);

            }*/

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

    public void addItemsToFrecklesSpinner() {
        frecklesSpinner = (Spinner) findViewById(R.id.frecklesSpinner);
        List<String> list = new ArrayList<String>();
        list.add("Number of freckles in unexposed areas");
        list.add("Many");
        list.add("Several");
        list.add("Few");
        list.add("Rare");
        list.add("None");
        ArrayAdapter<String> frecklesDataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        frecklesDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        frecklesSpinner.setAdapter(frecklesDataAdapter);
    }

    public void addItemsToBurnSpinner() {
        burnSpinner = (Spinner) findViewById(R.id.burnSpinner);
        List<String> list = new ArrayList<String>();
        list.add("What happens when you stay in the sun for too long?");
        list.add("Painful blisters, peeling");
        list.add("Mild blisters, peeling");
        list.add("Burn, mild peeling");
        list.add("Rare skin, no peeling");
        list.add("No burning");
        ArrayAdapter<String> burnDataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        burnDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        burnSpinner.setAdapter(burnDataAdapter);
    }

    public void addItemsToBrownFreqSpinner() {
        brownFreqSpinner = (Spinner) findViewById(R.id.brownFreqSpinner);
        List<String> list = new ArrayList<String>();
        list.add("How often do you get tanned?");
        list.add("Never");
        list.add("Seldom");
        list.add("Sometimes");
        list.add("Often");
        list.add("Always");
        ArrayAdapter<String> brownFreqDataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        brownFreqDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        brownFreqSpinner.setAdapter(brownFreqDataAdapter);
    }

    public void addItemsToBrownIntSpinner() {
        brownIntSpinner = (Spinner) findViewById(R.id.brownIntSpinner);
        brownIntSpinner.setAdapter(new MyBrownIntAdapter());
    }

    private static class BrownIntViewHolder {
        ImageView imageViewBrownInt;
    }

    private class MyBrownIntAdapter extends BaseAdapter {
        public int getCount(){
            return brownInt.length;
        }
        @Override
        public Integer getItem(int position) {
            return brownInt[position];
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            BrownIntViewHolder brownIntViewHolder;
            if(convertView == null) {
                itemView = getLayoutInflater().inflate(R.layout.spinner_row,parent, false);
                brownIntViewHolder = new BrownIntViewHolder();
                brownIntViewHolder.imageViewBrownInt = (ImageView) itemView.findViewById(R.id.spinnerImage);
                itemView.setTag(brownIntViewHolder);
            }
            else {
                brownIntViewHolder = (BrownIntViewHolder) itemView.getTag();
            }
            brownIntViewHolder.imageViewBrownInt.setImageDrawable(getResources().getDrawable(brownInt[position]));
            return itemView;
        }
    }

    public void addItemsToFaceSpinner() {
        faceSpinner = (Spinner) findViewById(R.id.faceSpinner);
        List<String> list = new ArrayList<String>();
        list.add("How sensitive is your face to the sun?");
        list.add("Very sensitive");
        list.add("Sensitive");
        list.add("Sometimes");
        list.add("Resistant");
        list.add("Never had a problem");
        ArrayAdapter<String> faceDataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        faceDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        faceSpinner.setAdapter(faceDataAdapter);
    }

    public void addItemsToTanFreqSpinner() {
        tanFreqSpinner = (Spinner) findViewById(R.id.tanFreqSpinner);
        List<String> list = new ArrayList<String>();
        list.add("How often do you tan?");
        list.add("Never");
        list.add("Seldom");
        list.add("Sometimes");
        list.add("Often");
        list.add("Always");
        ArrayAdapter<String> tanFreqDataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        tanFreqDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tanFreqSpinner.setAdapter(tanFreqDataAdapter);
    }

    public void addItemsToTanHistSpinner() {
        tanHistSpinner = (Spinner) findViewById(R.id.tanHistSpinner);
        List<String> list = new ArrayList<String>();
        list.add("When was your last tan?");
        list.add(">3 months ago");
        list.add("2-3 months ago");
        list.add("1-2 months ago");
        list.add("A few weeks ago");
        list.add("A few days ago");
        ArrayAdapter<String> tanHistDataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        tanHistDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tanHistSpinner.setAdapter(tanHistDataAdapter);
    }

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

    public void displayResults(View V) {
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        TextView textView = (TextView) findViewById(R.id.serialReceivedText);
        if(textView.getText().toString().equals("")){
            Toast.makeText(this,"Please select something", Toast.LENGTH_SHORT).show();
        }
        else {
            String message = textView.getText().toString();
            intent.putExtra(EXTRA_MESSAGE, message);
            startActivity(intent);
        }
    }

    public void sendData(View V) {
        if((eyeSpinner.getSelectedItemPosition() * hairSpinner.getSelectedItemPosition() *
                skinSpinner.getSelectedItemPosition() * frecklesSpinner.getSelectedItemPosition() *
                burnSpinner.getSelectedItemPosition() * brownFreqSpinner.getSelectedItemPosition() *
                brownIntSpinner.getSelectedItemPosition() * faceSpinner.getSelectedItemPosition() *
                tanFreqSpinner.getSelectedItemPosition() * tanHistSpinner.getSelectedItemPosition()) != 0) {
            serialReceivedText.getEditableText().clear();
            serialSend(String.valueOf(eyeSpinner.getSelectedItemPosition())+String.valueOf(hairSpinner.getSelectedItemPosition())+
                    String.valueOf(skinSpinner.getSelectedItemPosition())+String.valueOf(frecklesSpinner.getSelectedItemPosition())+
                    String.valueOf(burnSpinner.getSelectedItemPosition())+String.valueOf(brownFreqSpinner.getSelectedItemPosition())+
                    String.valueOf(brownIntSpinner.getSelectedItemPosition())+String.valueOf(faceSpinner.getSelectedItemPosition())+
                    String.valueOf(tanFreqSpinner.getSelectedItemPosition())+String.valueOf(tanHistSpinner.getSelectedItemPosition()));
        }
        else {
            Toast.makeText(this, "Please select an option for each category.", Toast.LENGTH_SHORT).show();
        }
    }

    public void cancelAlarm(View view) {
        Intent alarmIntent = new Intent(this, OneShotAlarm.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent,0);
        manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntent);
        Toast.makeText(this, "Alarm canceled", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        onActivityResultProcess(requestCode, resultCode, data);	//onActivityResult Process by BlunoLibrary
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        onPauseProcess();	//onPause Process by BlunoLibrary
    }

    protected void onStop() {
        super.onStop();
        onStopProcess();	//onStop Process by BlunoLibrary
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        onDestroyProcess();	//onDestroy Process by BlunoLibrary
    }

    public Boolean isNumeric(String str) {
        try {
            float f = Float.parseFloat(str);
        }
        catch(NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    @Override
    public void onSerialReceived(String theString) {	//Once connection data received, this function will be called
        serialReceivedText.append(theString);	//append the text into the EditText
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
     *  This functions will enable or disable the buttons depending on the bluetooth connectivity
     *  status.
     */
    public void ActivateButton(boolean trueFalse) {
        Button buttonDisplayResults = (Button) findViewById(R.id.buttonDisplayResults);
        Button buttonSendData = (Button) findViewById(R.id.buttonSendData);

        if (trueFalse) {
            buttonDisplayResults.setEnabled(true);
            buttonSendData.setEnabled(true);
        }
        else {
            buttonDisplayResults.setEnabled(false);
            buttonSendData.setEnabled(false);
        }
    }
}