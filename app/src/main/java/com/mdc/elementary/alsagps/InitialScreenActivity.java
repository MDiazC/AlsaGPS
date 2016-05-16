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
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class InitialScreenActivity extends Activity{

    private Boolean isActiveApp=false;
    private ContactList contact_list=null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.initial_screen);
        Log.e("CREATION", "prelanzado activiydad");

        //this.deleteDatabase("AlsaGPS.db");

        this.contact_list= new ContactList(this);
        Log.e("CREATION", "New activiydad");
        ArrayList ar= this.contact_list.getAllContacts();

        LinearLayout lyt_settings = (LinearLayout) findViewById(R.id.bottom_bar_settings);

        this.visibilityFirstTimeLayout(ar);

        if(ar.isEmpty()) {
            this.activeApp();
            this.activateAllFeatures();
        }

        lyt_settings.setOnClickListener(initialScreenHandler);
    }

    private void visibilityFirstTimeLayout(ArrayList ar){

        LinearLayout lyt_initial_first_time = (LinearLayout) findViewById(R.id.layout_app_first_time);
        LinearLayout lyt_initial_first_time_bottom_bar = (LinearLayout) findViewById(R.id.bottom_bar_first_time);
        LinearLayout lyt_initial_bottom_bar = (LinearLayout) findViewById(R.id.bottom_bar);


        if(ar.isEmpty()) {
            lyt_initial_first_time.setVisibility(View.GONE);
            lyt_initial_first_time_bottom_bar.setVisibility(View.GONE);
            lyt_initial_bottom_bar.setVisibility(View.VISIBLE);
        }else{
            lyt_initial_first_time.setVisibility(View.VISIBLE);
            lyt_initial_first_time_bottom_bar.setVisibility(View.VISIBLE);
            lyt_initial_bottom_bar.setVisibility(View.GONE);
        }

    }

    private void activeApp(){

        LinearLayout lyt_initial_switch_button_on = (LinearLayout) findViewById(R.id.layout_app_enabled);
        LinearLayout lyt_initial_switch_button_off = (LinearLayout) findViewById(R.id.layout_app_disabled);


        if(this.isActiveApp){
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
        LinearLayout lyt_help_button = (LinearLayout) findViewById(R.id.layout_help_button_enabled);

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
                    isActiveApp=true;
                    activeApp();
                    activateAllFeatures();
                    break;
                case R.id.layout_switch_button_enabled:
                    isActiveApp = false;
                    activeApp();
                    activateAllFeatures();
                    break;
                case R.id.layout_help_button_enabled:
                    //Inform the user the button2 has been clicked
                    break;
            }
        }
    };

   /* private void activateAllFeatures(){

        LinearLayout lyt_starting_points = (LinearLayout) findViewById(R.id.bottom_bar_starting_points);
        LinearLayout lyt_about = (LinearLayout) findViewById(R.id.bottom_bar_about);

        LinearLayout lyt_switch_button_off = (LinearLayout) findViewById(R.id.layout_switch_button);
        LinearLayout lyt_switch_button_on = (LinearLayout) findViewById(R.id.layout_switch_button_enabled);
        LinearLayout lyt_help_button = (LinearLayout) findViewById(R.id.layout_help_button_enabled);

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
                        Intent intentMainAbout = new Intent(InitialScreenActivity.this ,AboutScreenActivity.class);
                        InitialScreenActivity.this.startActivity(intentMainAbout);
                    activeApp;
                    break;
                case R.id.layout_switch_button_enabled:
                    //Inform the user the button1 has been clicked
                    Toast.makeText(this, "Button1 clicked.", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.layout_help_button_enabled:
                    //Inform the user the button2 has been clicked
                    Toast.makeText(this, "Button2 clicked.", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };*/
}
