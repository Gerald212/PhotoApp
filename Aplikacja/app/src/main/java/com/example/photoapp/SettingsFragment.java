package com.example.photoapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

public class SettingsFragment extends Fragment {
    public  SettingsFragment(){
        super(R.layout.fragment_settings);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        Button button = (Button)view.findViewById(R.id.setIpButton);

        EditText et = (EditText)view.findViewById(R.id.editTextView);
        SharedPreferences sp = getActivity().getSharedPreferences("my_pref", Context.MODE_PRIVATE);
        String sharedPrefIp = sp.getString("ip", "127.0.0.1");
        Log.i("ip w fragmencie", sharedPrefIp);
        et.setText(sharedPrefIp);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText tv =  (EditText)view.findViewById(R.id.editTextView);
                String tempIp = tv.getText().toString();
                Log.i("ip: ", tempIp);

                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("my_pref",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("ip", tempIp);
                //editor.apply();
                if(editor.commit()){
                    Snackbar snackbar = Snackbar.make(v, "Zmieniono adres ip na " + tempIp, 2000);
                    snackbar.show();
                }
            }
        });
        return view;
    }
}
