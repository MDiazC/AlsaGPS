package com.mdc.elementary.alsagps;

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

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class SettingsScreenActivity extends Activity {
    private InternalParams internal_params = null;
    private ContactList contact_list=null;

    public SettingsScreenActivity() {
        this.internal_params = new InternalParams(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings_screen);

        this.contact_list= new ContactList(this);
        Boolean conctac_list_empty= this.contact_list.isContactListEmpty();

        this.internal_params = new InternalParams(this);
        this.internal_params.loadParams();

        this.visibilityFirstTimeLayout(conctac_list_empty);
        this.activateBottomBar();

        if(conctac_list_empty) {
            this.activateAllFeatures();
        }
    }

    private void visibilityFirstTimeLayout(Boolean contact_list_empty){

        LinearLayout lyt_settings_first_time = (LinearLayout) findViewById(R.id.layout_settings_first_time);
        LinearLayout lyt_settings = (LinearLayout) findViewById(R.id.layout_settings);
        LinearLayout lyt_bottom_options = (LinearLayout) findViewById(R.id.layout_options);

        lyt_settings.setVisibility(View.VISIBLE);
        lyt_bottom_options.setVisibility(View.GONE);

        if(contact_list_empty) {
            lyt_settings_first_time.setVisibility(View.GONE);
        }else{
            lyt_settings_first_time.setVisibility(View.VISIBLE);
        }

    }

    private void activateBottomBar(){
        LinearLayout lyt_back_button = (LinearLayout) findViewById(R.id.bottom_bar_back);
        LinearLayout lyt_about = (LinearLayout) findViewById(R.id.bottom_bar_about);

        lyt_back_button.setOnClickListener(initialScreenHandler);
        lyt_about.setOnClickListener(initialScreenHandler);
    }

    private void deactivateBottomBar(){
        LinearLayout lyt_back_button = (LinearLayout) findViewById(R.id.bottom_bar_back);
        LinearLayout lyt_about = (LinearLayout) findViewById(R.id.bottom_bar_about);

        lyt_back_button.setOnClickListener(null);
        lyt_about.setOnClickListener(null);
    }

    private void activateFrequency(){
        LinearLayout input_frequency = (LinearLayout) findViewById(R.id.layout_location_frequency_input);
        LinearLayout lyt_options = (LinearLayout) findViewById(R.id.layout_options);
        LinearLayout input_time = (LinearLayout) findViewById(R.id.layout_time_warn_input);

        input_frequency.setVisibility(View.VISIBLE);
        lyt_options.setVisibility(View.VISIBLE);
        input_time.setVisibility(View.GONE);
    }


    private void activateTimeWarn(){
        LinearLayout input_frequency = (LinearLayout) findViewById(R.id.layout_location_frequency_input);
        LinearLayout lyt_options = (LinearLayout) findViewById(R.id.layout_options);
        LinearLayout input_time = (LinearLayout) findViewById(R.id.layout_time_warn_input);

        input_frequency.setVisibility(View.GONE);
        lyt_options.setVisibility(View.VISIBLE);
        input_time.setVisibility(View.VISIBLE);
    }

    private void hideOptions(){
        LinearLayout lyt_options = (LinearLayout) findViewById(R.id.layout_options);
        lyt_options.setVisibility(View.GONE);
    }

    private void deactivateBackgroundFeatures(){
        LinearLayout lyt_button_contacts = (LinearLayout) findViewById(R.id.layout_blank_contacts);
        LinearLayout lyt_button_message = (LinearLayout) findViewById(R.id.layout_blank_message);
        LinearLayout lyt_button_frequency = (LinearLayout) findViewById(R.id.layout_blank_location);
        LinearLayout lyt_button_time = (LinearLayout) findViewById(R.id.layout_blank_time_warn);

        lyt_button_contacts.setOnClickListener(null);
        lyt_button_message.setOnClickListener(null);
        lyt_button_frequency.setOnClickListener(null);
        lyt_button_time.setOnClickListener(null);
    }

    private void activateAllFeatures(){

        LinearLayout lyt_back_button = (LinearLayout) findViewById(R.id.bottom_bar_back);
        LinearLayout lyt_about = (LinearLayout) findViewById(R.id.bottom_bar_about);

        LinearLayout button_cancel = (LinearLayout) findViewById(R.id.options_button_cancel);
        LinearLayout button_save = (LinearLayout) findViewById(R.id.options_button_save);

        LinearLayout lyt_button_contacts = (LinearLayout) findViewById(R.id.layout_blank_contacts);
        LinearLayout lyt_button_message = (LinearLayout) findViewById(R.id.layout_blank_message);
        LinearLayout lyt_button_frequency = (LinearLayout) findViewById(R.id.layout_blank_location);
        LinearLayout lyt_button_time = (LinearLayout) findViewById(R.id.layout_blank_time_warn);

        button_cancel.setOnClickListener(initialScreenHandler);
        button_save.setOnClickListener(initialScreenHandler);

        lyt_button_contacts.setOnClickListener(initialScreenHandler);
        lyt_button_message.setOnClickListener(initialScreenHandler);
        lyt_button_frequency.setOnClickListener(initialScreenHandler);
        lyt_button_time.setOnClickListener(initialScreenHandler);

    }

    View.OnClickListener initialScreenHandler = new View.OnClickListener(){

        public void onClick(View v) {

            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

            switch(v.getId()) {
                case R.id.bottom_bar_back:
                    Intent intentMainSettings = new Intent(SettingsScreenActivity.this ,InitialScreenActivity.class);
                    SettingsScreenActivity.this.startActivity(intentMainSettings);
                    break;
                case R.id.bottom_bar_about:
                    Intent intentMainAbout = new Intent(SettingsScreenActivity.this ,AboutScreenActivity.class);
                    SettingsScreenActivity.this.startActivity(intentMainAbout);
                    break;
                case R.id.options_button_cancel:
                    hideOptions();
                    activateAllFeatures();
                    activateBottomBar();
                    //hides keyboard
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    break;
                case R.id.options_button_save:
                    hideOptions();
                    activateAllFeatures();
                    activateBottomBar();
                    //hides keyboard
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    break;
                case R.id.layout_blank_contacts:
                    Intent intentMainContacts = new Intent(SettingsScreenActivity.this ,ContactsScreenActivity.class);
                    SettingsScreenActivity.this.startActivity(intentMainContacts);
                    break;
                case R.id.layout_blank_message:
                    Intent intentMainMessage = new Intent(SettingsScreenActivity.this ,PersonalMessageActivity.class);
                    SettingsScreenActivity.this.startActivity(intentMainMessage);
                    break;
                case R.id.layout_blank_location:
                    activateFrequency();
                    deactivateBackgroundFeatures();
                    deactivateBottomBar();
                    break;
                case R.id.layout_blank_time_warn:
                    activateTimeWarn();
                    deactivateBackgroundFeatures();
                    deactivateBottomBar();
                    break;
            }
        }
    };

}