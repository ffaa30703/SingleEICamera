package com.example.myapplication.Utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;


import com.example.myapplication.CameraActivity;
import com.example.myapplication.R;
import com.wf.wffrsinglecamapp;
import com.wf.wffrjni;

import java.text.SimpleDateFormat;
import java.util.Date;

import static android.R.id.list;

public class SettingActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Call super :
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prefstoolbar);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitle("Settings");
        myToolbar.setTitleTextColor(Color.WHITE);
        myToolbar.setTitleMarginStart(210);
        setSupportActionBar(myToolbar);
        myToolbar.setNavigationIcon(R.drawable.back_button);
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        sharedPreferences = getSharedPreferences("usb_dual_cam", Context.MODE_PRIVATE);
        editor = getSharedPreferences("usb_dual_cam", Context.MODE_PRIVATE).edit();
        getFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingsFragment()).commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(SettingActivity.this, CameraActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }



    private String getDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd_MM_yyyy");
        String date = sdf.format(new Date());
        return date;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public static class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        Preference databasePreference,listDatabasePref;
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View v = super.onCreateView(inflater, container, savedInstanceState);
            if (v != null) {
                ListView lv = (ListView) v.findViewById(list);
                lv.setDivider(null);
                lv.setPadding(0, 5, 0, 5);
            }
            listDatabasePref = (Preference) findPreference("list_database");
            listDatabasePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(getActivity(),EnrolledDatabaseActivity.class);
                    startActivity(intent);
                    return true;
                }
            });

            databasePreference = (Preference) findPreference("delete_database");
            databasePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    showDeleteDBDialog();
                    return true;
                }
            });

            return v;
        }

        private void showDeletePersonDialog(){
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this.getActivity());
            LayoutInflater inflater = this.getActivity().getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.delete_name_dialog, null);
            dialogBuilder.setTitle("Delete Record by Name");
            final EditText delete_firstname = (EditText) dialogView.findViewById(R.id.delete_firstname);
            final EditText delete_lastname = (EditText) dialogView.findViewById(R.id.delete_lastname);
            dialogBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (delete_firstname.getText().length()!=0 || delete_lastname.getText().length()!=0){
                        wffrsinglecamapp.deletePersonbyName(delete_firstname.getText().toString()+" "+delete_lastname.getText().toString());
                    }
                }
            })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            dialogBuilder.setView(dialogView);


            AlertDialog alertDialog = dialogBuilder.create();
            alertDialog.show();
        }

        private void showDeleteDBDialog(){
            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);
            } else {
                builder = new AlertDialog.Builder(getActivity());
            }
            builder.setTitle("Delete Database")
                    .setMessage("Are you sure you want to delete Entire Database?")
                    .setPositiveButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                        }
                    })
                    .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                            Log.i("DADA","DELETING UP");
                            int val = wffrsinglecamapp.deleteDatabase();
                            Log.i("DADA","DELETING VAL : "+val);
                            Log.i("DADA","DELETING DOWN");
                        }
                    })
                    .show();
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);
            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.prefs);
          /*  SeekBarPreference seek = (SeekBarPreference) findPreference("threshold");
            seek.setValue((int) wffrjni.GetRecognitionThreshold());*/
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
             if (key.equals("threshold")) {
                SeekBarPreference seek = (SeekBarPreference) findPreference(key);
                seek.setValue(sharedPreferences.getInt("threshold",50));
                 wffrjni.SetRecognitionThreshold(seek.getValue());
             }
             else if (key.equals("debug_switch")){
                 final SwitchPreference pref = (SwitchPreference) findPreference(key);
                 SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
                 final boolean val = sp.getBoolean(key,false);
             }
        }

        @Override
        public void onPause() {
            // TODO Auto-generated method stub
            super.onPause();
            getPreferenceManager().getSharedPreferences().
                    unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onResume() {
            // TODO Auto-generated method stub
            super.onResume();

            getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }
    }

}
