package com.example.blunobasicdemo;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
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
    private static final Integer[] skinTone = {R.drawable.blank,R.drawable.skin_1,R.drawable.skin_2,R.drawable.skin_3,R.drawable.skin_4,R.drawable.skin_5,R.drawable.skin_6};
    private static final Integer[] hairColour = {R.drawable.blank,R.drawable.hair_1,R.drawable.hair_2,R.drawable.hair_3,R.drawable.hair_4,R.drawable.hair_5};
    private static final Integer[] eyeColour = {R.drawable.blank,R.drawable.eye_1,R.drawable.eye_2,R.drawable.eye_3,R.drawable.eye_4,R.drawable.eye_5};
    public final static String EXTRA_MESSAGE = "com.example.blunobasicdemo.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        onCreateProcess();	//onCreate Process by BlunoLibrary
        serialBegin(115200);	//set the Uart Baudrate on BLE chip to 115200
        serialReceivedText=(TextView) findViewById(R.id.serialReceivedText);	//initial the EditText of the received data
        //serialSendText=(EditText) findViewById(R.id.serialSendText); //initial the EditText of the sending data
        /*buttonSerialSend = (Button) findViewById(R.id.buttonSerialSend); //initial the button for sending the data
        buttonSerialSend.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                serialSend(serialSendText.getText().toString()); //send the data to the BLUNO
            }
        });
        buttonScan = (Button) findViewById(R.id.buttonScan); //initial the button for scanning the BLE device
        buttonScan.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                buttonScanOnClickProcess(); //Alert Dialog for selecting the BLE device
            }
        });*/
        //test commit
        //buttonScan = (Button) findViewById(R.id.buttonScan); //initial the button for scanning the BLE device
        addItemsToEyeSpinner();
        addItemsToHairSpinner();
        addItemsToSkinSpinner();
        addItemsToFrecklesSpinner();
        addItemsToBurnSpinner();
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
            // set the value of the spinner
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
    /*
    public void scanDevice(View V) {
    buttonScanOnClickProcess(); //Alert Dialog for selecting the BLE device
    }
    public void sendData(View V) { //send the data to the BLUNO
    serialSend(serialSendText.getText().toString());
    }
    public void sendData1(View V) {
    serialSend("12");
    }
    */
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
            //do we have a view?
            if(convertView == null) {
                //we don't have a view so create one
                itemView = getLayoutInflater().inflate(R.layout.spinner_row,parent, false);
                hairViewHolder = new HairViewHolder();
                hairViewHolder.imageViewHair = (ImageView) itemView.findViewById(R.id.spinnerImage);
                //set the tag for this view to the current image view holder
                itemView.setTag(hairViewHolder);
            }
            else {
                //we have a view to get the tagged view
                hairViewHolder = (HairViewHolder) itemView.getTag();
            }
            //display the current image
            hairViewHolder.imageViewHair.setImageDrawable(getResources().getDrawable(hairColour[position]));
            return itemView;
        }
    }

    public void addItemsToSkinSpinner() {
        skinSpinner = (Spinner) findViewById(R.id.skinSpinner);
        skinSpinner.setAdapter(new MySkinAdapter());
    }
    private static class SkinViewHolder {
        ImageView imageViewSkin;
    }
    private class MySkinAdapter extends BaseAdapter {
        public int getCount(){
            return skinTone.length;
        }
        @Override
        public Integer getItem(int position) {
            return skinTone[position];
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            SkinViewHolder skinViewHolder;
            //do we have a view?
            if(convertView == null) {
                //we don't have a view so create one
                itemView = getLayoutInflater().inflate(R.layout.spinner_row,parent, false);
                skinViewHolder = new SkinViewHolder();
                skinViewHolder.imageViewSkin = (ImageView) itemView.findViewById(R.id.spinnerImage);
                //set the tag for this view to the current image view holder
                itemView.setTag(skinViewHolder);
            }
            else {
                //we have a view to get the tagged view
                skinViewHolder = (SkinViewHolder) itemView.getTag();
            }
            //display the current image
            skinViewHolder.imageViewSkin.setImageDrawable(getResources().getDrawable(skinTone[position]));
            return itemView;
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
    /*
    public void addItemsToSkinSpinner() {
    skinSpinner = (Spinner) findViewById(R.id.skinSpinner);
    List<String> list = new ArrayList<String>();
    list.add("Please select the colour of your skin");
    list.add("Fair");
    list.add("Tanned");
    list.add("Very tanned");
    ArrayAdapter<String> skinDataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
    skinDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    skinSpinner.setAdapter(skinDataAdapter);
    }*/



    /*
    public void addItemsToEyeSpinner() {
        eyeSpinner = (Spinner) findViewById(R.id.eyeSpinner);
        List<String> list = new ArrayList<String>();
        list.add("Please select your natural eye colour");
        list.add("Light blue, gray or green");
        list.add("Blue, gray or green");
        list.add("Dark blue, gray or green");
        list.add("Brown");
        list.add("Black");
        ArrayAdapter<String> eyeDataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        eyeDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eyeSpinner.setAdapter(eyeDataAdapter);
    }
    */

    /*
    public void addItemsToHairSpinner() {
        hairSpinner = (Spinner) findViewById(R.id.hairSpinner);
        List<String> list = new ArrayList<String>();
        list.add("Please select your natural hair colour");
        list.add("Sandy red");
        list.add("Blond");
        list.add("Chestnut or dark blond");
        list.add("Brown");
        list.add("Black");
        ArrayAdapter<String> hairDataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        hairDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hairSpinner.setAdapter(hairDataAdapter);
    }
    */




    public void addListenerToSpinner() {
        skinSpinner.setOnItemSelectedListener(new MyOnItemSelectedListener(this));
        eyeSpinner.setOnItemSelectedListener(new MyOnItemSelectedListener(this));
        hairSpinner.setOnItemSelectedListener(new MyOnItemSelectedListener(this));
        frecklesSpinner.setOnItemSelectedListener(new MyOnItemSelectedListener(this));
        burnSpinner.setOnItemSelectedListener(new MyOnItemSelectedListener(this));
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
        eyeSpinner = (Spinner) findViewById(R.id.eyeSpinner);
        hairSpinner = (Spinner) findViewById(R.id.hairSpinner);
        skinSpinner = (Spinner) findViewById(R.id.skinSpinner);
        frecklesSpinner = (Spinner) findViewById(R.id.frecklesSpinner);
        burnSpinner = (Spinner) findViewById(R.id.burnSpinner);
        if(eyeSpinner.getSelectedItemPosition()!=0 && hairSpinner.getSelectedItemPosition() !=0 &&
                skinSpinner.getSelectedItemPosition()!=0 && frecklesSpinner.getSelectedItemPosition() !=0 &&
                burnSpinner.getSelectedItemPosition() !=0) {
            serialReceivedText.getEditableText().clear();
            serialSend(String.valueOf(eyeSpinner.getSelectedItemPosition())+String.valueOf(hairSpinner.getSelectedItemPosition())+
                    String.valueOf(skinSpinner.getSelectedItemPosition())+String.valueOf(frecklesSpinner.getSelectedItemPosition())+
                    String.valueOf(burnSpinner.getSelectedItemPosition()));
        }
        else {
            Toast.makeText(this, "Please select an option for each category.", Toast.LENGTH_SHORT).show();
        }
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
    /*@Override
    public void onConectionStateChange(connectionStateEnum theConnectionState) {//Once connection state changes, this function will be called
    switch (theConnectionState) { //Four connection state
    case isConnected:
    buttonScan.setText("Connected");
    break;
    case isConnecting:
    buttonScan.setText("Connecting");
    break;
    case isToScan:
    buttonScan.setText("Scan");
    break;
    case isScanning:
    buttonScan.setText("Scanning");
    break;
    case isDisconnecting:
    buttonScan.setText("isDisconnecting");
    break;
    default:
    break;
    }
    }*/
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
        // TODO Auto-generated method stub
        serialReceivedText.append(theString);	//append the text into the EditText
        //The Serial data from the BLUNO may be sub-packaged, so using a buffer to hold the String is a good choice.
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
    public void ActivateButton(boolean yes_or_no_) {
        Button buttonDisplayResults = (Button) findViewById(R.id.buttonDisplayResults);
        Button buttonSendData = (Button) findViewById(R.id.buttonSendData);

        if (yes_or_no_ == true) {
            buttonDisplayResults.setEnabled(true);
            buttonSendData.setEnabled(true);
        }
        else {
            buttonDisplayResults.setEnabled(false);
            buttonSendData.setEnabled(false);
        }
    }
}