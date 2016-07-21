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
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class InitialScreenActivity extends Activity implements MyCallback{

    private boolean appActivated=false;
    private ContactList contact_list=null;
    ThreadTrackCoordinates threadGPS = null;
    ThreadAutomaticSMS threadSMS = null;
    boolean areThreadsLaunched = false;

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
                    deactivateThreads();
                    break;
                case R.id.help_button_on:
                    sendSMSManually();
                    break;
            }
        }
    };

    private void sendSMSManually(){
        SMSSystem sms = new SMSSystem(this);
        sms.sendSMS();
        this.stopApp();
    }

    private void activateApp(){
        boolean is_contact_list_empty= this.contact_list.isContactListEmpty();
        if(!is_contact_list_empty){
            if(!this.areThreadsLaunched) {
                StartingPoints sp = new StartingPoints(this);
                sp.loadStartingPoints();
                if(sp.getStartingPoints() == null || sp.getStartingPoints().isEmpty()){
                    this.stopApp();
                    this.showStartingPointsAlert();
                }else {
                    this.startThreads();
                }
            }
        }
    }

    private void startThreads(){
        GPSSystem gps = new GPSSystem(this);
        if(gps.canGetLocation()){
            Log.e("CREATE", "activateApp");
            this.threadGPS = new ThreadTrackCoordinates(this, gps);
            this.threadGPS.run();
            this.threadSMS = new ThreadAutomaticSMS(this);
            this.threadSMS.callback = this;
            this.threadSMS.run();
            this.areThreadsLaunched=true;
        }else {
            this.stopApp();
            gps.showSettingsAlert();
        }
        gps.stopUsingGPS();
        gps=null;
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
                startThreads();
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

    private void deactivateThreads(){
        this.areThreadsLaunched= false;
        Log.e("CREATE", "deactivateApp");
        if(this.threadGPS != null) {
            this.threadGPS.stopThread();
            this.threadGPS=null;
        }
        if(this.threadSMS != null) {
            this.threadSMS.stopThread();
            this.threadSMS = null;
        }
    }

    public void stopApp(){
        this.appActivated = false;
        this.deactivateThreads();
        this.changeOnOffButton();
        this.activateAllFeatures();
    }

    @Override
    public void updateCoordinates(double latitude, double longitude){}

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putBoolean("appActivated", this.appActivated);
        savedInstanceState.putParcelable("threadGPS", this.threadGPS);
        savedInstanceState.putParcelable("threadSMS", this.threadSMS);
        savedInstanceState.putBoolean("areThreadsLaunched", this.areThreadsLaunched);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.appActivated = savedInstanceState.getBoolean("appActivated");
        this.threadGPS = savedInstanceState.getParcelable("threadGPS");
        this.threadSMS = savedInstanceState.getParcelable("threadSMS");
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
