package com.example.blunobasicdemo;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity  extends BlunoLibrary {
	private Button buttonScan;
	private Button buttonSerialSend;
	private EditText serialSendText;
	private TextView serialReceivedText;
    private Spinner skinSpinner;
    private Spinner genderSpinner;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        onCreateProcess();														//onCreate Process by BlunoLibrary
        
        serialBegin(115200);													//set the Uart Baudrate on BLE chip to 115200
		
        serialReceivedText=(TextView) findViewById(R.id.serialReceivedText);	//initial the EditText of the received data
        serialSendText=(EditText) findViewById(R.id.serialSendText);			//initial the EditText of the sending data
        
        /*buttonSerialSend = (Button) findViewById(R.id.buttonSerialSend);		//initial the button for sending the data
        buttonSerialSend.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				serialSend(serialSendText.getText().toString());				//send the data to the BLUNO
			}
		});
        
        buttonScan = (Button) findViewById(R.id.buttonScan);					//initial the button for scanning the BLE device
        buttonScan.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				buttonScanOnClickProcess();										//Alert Dialog for selecting the BLE device
			}
		});*/
        //test commit
        buttonScan = (Button) findViewById(R.id.buttonScan);					//initial the button for scanning the BLE device
        addItemsToSkinSpinner();
        addItemsToGenderSpinner();
	}

    public void scanDevice(View V) {
        buttonScanOnClickProcess();                                             //Alert Dialog for selecting the BLE device
    }

    public void sendData(View V) {                                              //send the data to the BLUNO
        serialSend(serialSendText.getText().toString());
    }

    public void sendData1(View V) {
        serialSend("12");
    }

	protected void onResume(){
		super.onResume();
		System.out.println("BlUNOActivity onResume");
		onResumeProcess();														//onResume Process by BlunoLibrary
	}

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
    }

    public void addItemsToGenderSpinner() {
        genderSpinner = (Spinner) findViewById(R.id.genderSpinner);
        List<String> list = new ArrayList<String>();
        list.add("Please select your gender");
        list.add("Male");
        list.add("Female");

        ArrayAdapter<String> genderDataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        genderDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderDataAdapter);
    }

    public void sendSkin(View V) {
        skinSpinner = (Spinner) findViewById(R.id.skinSpinner);
        if(skinSpinner.getSelectedItemPosition()!=0) {
            serialSend(String.valueOf(skinSpinner.getSelectedItemPosition()));
        }
    }

    public void sendGender(View V) {
        genderSpinner = (Spinner) findViewById(R.id.genderSpinner);
        if(genderSpinner.getSelectedItemPosition()!=0) {
            serialSend(String.valueOf(genderSpinner.getSelectedItemPosition()));
        }
    }

    public void sendBoth(View V) {
        skinSpinner = (Spinner) findViewById(R.id.skinSpinner);
        genderSpinner = (Spinner) findViewById(R.id.genderSpinner);
        if(skinSpinner.getSelectedItemPosition()!=0 && genderSpinner.getSelectedItemPosition()!=0) {
            serialSend(String.valueOf(skinSpinner.getSelectedItemPosition())+String.valueOf(genderSpinner.getSelectedItemPosition()));
        }
    }

    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		onActivityResultProcess(requestCode, resultCode, data);					//onActivityResult Process by BlunoLibrary
		super.onActivityResult(requestCode, resultCode, data);
	}
	
    @Override
    protected void onPause() {
        super.onPause();
        onPauseProcess();														//onPause Process by BlunoLibrary
    }
	
	protected void onStop() {
		super.onStop();
		onStopProcess();														//onStop Process by BlunoLibrary
	}
    
	@Override
    protected void onDestroy() {
        super.onDestroy();	
        onDestroyProcess();														//onDestroy Process by BlunoLibrary
    }

	@Override
	public void onConectionStateChange(connectionStateEnum theConnectionState) {//Once connection state changes, this function will be called
		switch (theConnectionState) {											//Four connection state
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
	public void onSerialReceived(String theString) {							//Once connection data received, this function will be called
		// TODO Auto-generated method stub
		serialReceivedText.append(theString);							//append the text into the EditText
		//The Serial data from the BLUNO may be sub-packaged, so using a buffer to hold the String is a good choice.
        if(isNumeric(theString)) {
            if (Float.parseFloat(theString) > 1) {
                Toast.makeText(this, "yes", Toast.LENGTH_SHORT).show();
            }
        }
	}

}