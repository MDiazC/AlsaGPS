/*
   AlsaGPS is a panic button app for Android
   Copyright (C) 2016 Moisés Díaz

   This program is free software; you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation; either version 3 of the License, or
   (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this program; if not, write to the Free Software Foundation,
   Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA

*/

package com.mdc.elementary.alsagps;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class InitialScreenActivity extends Activity implements  AlertSystemReceiver.Receiver{

    private boolean appActivated=false;
    private ContactList contact_list=null;
    boolean areThreadsLaunched = false;
    static Intent alertService ;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.initial_screen);

        this.contact_list= new ContactList(this);
        this.contact_list.loadAllContacts();
        boolean is_contact_list_empty= this.contact_list.isContactListEmpty();

        this.visibilityFirstTimeLayout(is_contact_list_empty);

        if(!is_contact_list_empty) {
            this.changeOnOffButton();
            this.activateAllFeatures();
        }

        LinearLayout lyt_settings = (LinearLayout) findViewById(R.id.bottom_bar_settings);
        LinearLayout lyt_settings_first_time = (LinearLayout) findViewById(R.id.bottom_bar_settings_ft);
        lyt_settings.setOnClickListener(initialScreenHandler);
        lyt_settings_first_time.setOnClickListener(initialScreenHandler);
    }

    private void visibilityFirstTimeLayout(boolean is_contact_list_empty){

        LinearLayout lyt_initial_first_time = (LinearLayout) findViewById(R.id.layout_app_first_time);
        LinearLayout lyt_initial_first_time_bottom_bar = (LinearLayout) findViewById(R.id.bottom_bar_first_time);
        LinearLayout lyt_initial_bottom_bar = (LinearLayout) findViewById(R.id.bottom_bar);

        if(!is_contact_list_empty) {
            lyt_initial_first_time.setVisibility(View.GONE);
            lyt_initial_first_time_bottom_bar.setVisibility(View.GONE);
            lyt_initial_bottom_bar.setVisibility(View.VISIBLE);
        }else{
            lyt_initial_first_time.setVisibility(View.VISIBLE);
            lyt_initial_first_time_bottom_bar.setVisibility(View.VISIBLE);
            lyt_initial_bottom_bar.setVisibility(View.GONE);
        }
    }

    private void changeOnOffButton(){

        LinearLayout lyt_initial_switch_button_on = (LinearLayout) findViewById(R.id.layout_app_enabled);
        LinearLayout lyt_initial_switch_button_off = (LinearLayout) findViewById(R.id.layout_app_disabled);

        if(this.appActivated){
            lyt_initial_switch_button_on.setVisibility(View.VISIBLE);
            lyt_initial_switch_button_off.setVisibility(View.GONE);
        }else{
            lyt_initial_switch_button_on.setVisibility(View.GONE);
            lyt_initial_switch_button_off.setVisibility(View.VISIBLE);
        }
    }

    private void activateAllFeatures(){

        LinearLayout lyt_starting_points = (LinearLayout) findViewById(R.id.bottom_bar_starting_points);
        LinearLayout lyt_about = (LinearLayout) findViewById(R.id.bottom_bar_about);

        LinearLayout lyt_switch_button_off = (LinearLayout) findViewById(R.id.layout_switch_button);
        LinearLayout lyt_switch_button_on = (LinearLayout) findViewById(R.id.layout_switch_button_enabled);
        Button lyt_help_button = (Button) findViewById(R.id.help_button_on);

        lyt_starting_points.setOnClickListener(initialScreenHandler);
        lyt_about.setOnClickListener(initialScreenHandler);

        lyt_switch_button_off.setOnClickListener(initialScreenHandler);
        lyt_switch_button_on.setOnClickListener(initialScreenHandler);
        lyt_help_button.setOnClickListener(initialScreenHandler);
    }


    View.OnClickListener initialScreenHandler = new View.OnClickListener(){

        public void onClick(View v) {

            switch(v.getId()) {
                case R.id.bottom_bar_settings:
                case R.id.bottom_bar_settings_ft:
                    Intent intentMainSettings = new Intent(InitialScreenActivity.this ,SettingsScreenActivity.class);
                    InitialScreenActivity.this.startActivity(intentMainSettings);
                    break;
                case R.id.bottom_bar_starting_points:
                    Intent intentMainStartingPoints = new Intent(InitialScreenActivity.this ,StartingPointActivity.class);
                    InitialScreenActivity.this.startActivity(intentMainStartingPoints);
                    break;
                case R.id.bottom_bar_about:
                    Intent intentMainAbout = new Intent(InitialScreenActivity.this ,AboutScreenActivity.class);
                    InitialScreenActivity.this.startActivity(intentMainAbout);
                    break;
                case R.id.layout_switch_button:
                    appActivated=true;
                    changeOnOffButton();
                    activateAllFeatures();
                    activateApp();
                    break;
                case R.id.layout_switch_button_enabled:
                    appActivated = false;
                    changeOnOffButton();
                    activateAllFeatures();
                    stopAlertService();
                    break;
                case R.id.help_button_on:
                    sendSMSManually();
                    break;
            }
        }
    };

    private void openSettingsScreen() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        this.startActivity(intent);
    }

    private void sendSMSManually(){
        SMSSystem sms = new SMSSystem(this);
        try{
            boolean result= sms.sendSMS();
            if(result){
                Log.e("CREATE", "sendSMSManually");
                this.showGenericAlert("SMS system", "SMS messages sent!");
            }
        }catch (Exception e){
            this.showGenericAlert("SMS system", e.getMessage());
        }
        this.stopApp();
    }

    private void activateApp(){
        boolean is_contact_list_empty= this.contact_list.isContactListEmpty();
        if(!is_contact_list_empty){
            if(!this.areThreadsLaunched) {
                StartingPoints sp = new StartingPoints(this);
                sp.loadStartingPoints();
                if(sp.getStartingPoints() == null || sp.getStartingPoints().isEmpty()){
                    this.showStartingPointsAlert();
                    this.stopApp();
                }else {
                    this.startAlertService();
                }
            }
        }
    }


    private void showStartingPointsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom));

        // Setting Dialog Title
        alertDialog.setTitle("Starting points");

        // Setting Dialog Message
        alertDialog.setMessage("In order to not send unnecesary messages, you should set a starting point");

        // On pressing Settings button
        alertDialog.setNegativeButton("Active the app", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                appActivated=true;
                changeOnOffButton();
                activateAllFeatures();
                startAlertService();
            }
        });
        alertDialog.setPositiveButton("Go to Starting points", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intentMainStartingPoints = new Intent(InitialScreenActivity.this ,StartingPointActivity.class);
                InitialScreenActivity.this.startActivity(intentMainStartingPoints);
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }


    /**
     * Function to show settings alert dialog
     * On pressing Settings button will lauch Settings Options
     * */
    public void showSettingsAlert(){

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom));

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                openSettingsScreen();
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        try {
            alertDialog.show();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    private void showGenericAlert(String key, String msg){

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom));

        // Setting Dialog Title
        alertDialog.setTitle(key);

        // Setting Dialog Message
        alertDialog.setMessage(msg);

        // On pressing Settings button
        alertDialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        try {
            alertDialog.show();
        } catch(Exception e){
            Log.e("CREATE", "showGenericAlert "+e.getMessage());
        }
    }

    private void startAlertService(){
        AlertSystemReceiver mReceiver = new AlertSystemReceiver(new Handler());
        mReceiver.setReceiver(this);

        alertService = new Intent(this, AlertSystemService.class);
        alertService.putExtra("receiver", mReceiver);
        startService(alertService);

        Log.e("CREATE", "Start service");
    }

    private void stopAlertService(){
        Log.e("CREATE", "Stop service");
        if(alertService != null)
            stopService(alertService);

    }

    public void stopApp(){
        Log.e("CREATE","Stop app");
        this.appActivated = false;
        this.stopAlertService();
        this.changeOnOffButton();
        this.activateAllFeatures();
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode) {
            case AlertSystemService.STATUS_RUNNING:
                setProgressBarIndeterminateVisibility(true);
                break;
            case AlertSystemService.STATUS_FINISHED:
                String result = resultData.getString(Intent.EXTRA_KEY_EVENT);
                if(result != null && result.equals("true"))
                    this.showGenericAlert("SMS system", "SMS messages sent!");
                break;
            case AlertSystemService.STATUS_ERROR:
                /* Handle the error */
                String error = resultData.getString("GPS");
                if(error != null && error.length() > 0)
                    showSettingsAlert();

                error = resultData.getString("SMS system");
                if(error != null && error.length() > 0)
                    showGenericAlert("SMS system", error);

                error = resultData.getString("SMS Thread");
                if(error != null && error.length() > 0)
                    showGenericAlert("SMS Thread", error);

                error = resultData.getString("Track Thread");
                if(error != null && error.length() > 0)
                    showGenericAlert("Track Thread", error);
                break;
        }
        Log.e("CREATE","onReceiveResult "+resultCode);
        this.appActivated = false;
        this.changeOnOffButton();
        this.activateAllFeatures();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putBoolean("appActivated", this.appActivated);
        savedInstanceState.putBoolean("areThreadsLaunched", this.areThreadsLaunched);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.appActivated = savedInstanceState.getBoolean("appActivated");
        this.areThreadsLaunched = savedInstanceState.getBoolean("areThreadsLaunched");
    }

    @Override
    public void onResume(){
        super.onResume();

        this.contact_list= new ContactList(this);
        this.contact_list.loadAllContacts();
        boolean is_contact_list_empty= this.contact_list.isContactListEmpty();

        this.visibilityFirstTimeLayout(is_contact_list_empty);

        if(!is_contact_list_empty) {
            this.changeOnOffButton();
            this.activateAllFeatures();
        }

        LinearLayout lyt_settings = (LinearLayout) findViewById(R.id.bottom_bar_settings);
        LinearLayout lyt_settings_first_time = (LinearLayout) findViewById(R.id.bottom_bar_settings_ft);
        lyt_settings.setOnClickListener(initialScreenHandler);
        lyt_settings_first_time.setOnClickListener(initialScreenHandler);
    }
}
