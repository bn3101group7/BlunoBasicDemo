package com.example.blunobasicdemo;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TableRow;

/**
 * Created by User on 9/28/2014.
 */
public class MyOnItemSelectedListener implements OnItemSelectedListener {
    SharedPreferences pref;
    Editor editor;
    TableRow spfRow;


    public MyOnItemSelectedListener(Context context) {
        pref = context.getSharedPreferences("MyPref", 0);
    }

    /**
     * gets position of selected spinner position and saves it to shared preference
     *
     * @param parent the AdapterView where click happened
     * @param view the view with the AdapterView that was clicked
     * @param pos position of the view in the adapter
     * @param id row id of the item that was clicked
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        switch (parent.getId()) {
            case R.id.eyeSpinner:
                editor = pref.edit();
                editor.putInt("eye", pos);
                break;
            case R.id.hairSpinner:
                editor = pref.edit();
                editor.putInt("hair", pos);
                break;
            case R.id.frecklesSpinner:
                editor = pref.edit();
                editor.putInt("frec", pos);
                break;
            case R.id.skinSpinner:
                editor = pref.edit();
                editor.putInt("skin", pos);
                break;
            case R.id.burnSpinner:
                editor = pref.edit();
                editor.putInt("burn", pos);
                break;
            case R.id.brownFreqSpinner:
                editor = pref.edit();
                editor.putInt("brownFreq", pos);
                break;
            case R.id.brownIntSpinner:
                editor = pref.edit();
                editor.putInt("brownInt", pos);
                break;
            case R.id.faceSpinner:
                editor = pref.edit();
                editor.putInt("face", pos);
                break;
            case R.id.tanFreqSpinner:
                editor = pref.edit();
                editor.putInt("tanFreq", pos);
                break;
            case R.id.tanHistSpinner:
                editor = pref.edit();
                editor.putInt("tanHist", pos);
                break;
            default:
                break;
        }
        editor.apply();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
