package com.example.blunobasicdemo;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * Created by User on 9/28/2014.
 */
public class MyOnItemSelectedListener implements OnItemSelectedListener {
    SharedPreferences pref;
    Editor editor;

    public MyOnItemSelectedListener(Context context) {
        pref = context.getSharedPreferences("MyPref", 0);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        switch(parent.getId()) {
            case R.id.eyeSpinner:
                int eyeChoice = pos;
                editor = pref.edit();
                editor.putInt("eye", eyeChoice);
                editor.commit();
                //Toast.makeText(parent.getContext(), Integer.toString(pos),Toast.LENGTH_SHORT).show();
                break;
            case R.id.hairSpinner:
                int hairChoice = pos;
                editor = pref.edit();
                editor.putInt("hair", hairChoice);
                editor.commit();
                break;
            case R.id.frecklesSpinner:
                int frecChoice = pos;
                editor = pref.edit();
                editor.putInt("frec", frecChoice);
                editor.commit();
            case R.id.skinSpinner:
                int skinChoice = pos;
                editor = pref.edit();
                editor.putInt("skin", skinChoice);
                editor.commit();
                //Toast.makeText(parent.getContext(), Integer.toString(pos),Toast.LENGTH_SHORT).show();
                break;
            case R.id.burnSpinner:
                int burnChoice = pos;
                editor = pref.edit();
                editor.putInt("burn", burnChoice);
                editor.commit();
                break;
            default:
                break;
        }
        Toast.makeText(parent.getContext(), Integer.toString(pos), Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
