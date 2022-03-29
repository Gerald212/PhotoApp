package com.example.photoapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class SettingsFragment extends Fragment {
    public  SettingsFragment(){
        super(R.layout.fragment_settings);
    }

    public void get(View view){
        EditText tv =  view.findViewById(R.id.editTextView);
        String tempIp = tv.getText().toString();
        Log.i("ip: ", tempIp);


//        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putString("ip", "");
    }
}
