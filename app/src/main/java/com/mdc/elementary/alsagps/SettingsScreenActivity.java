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
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

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
        this.contact_list.loadAllContacts();
        Boolean contact_list_empty= this.contact_list.isContactListEmpty();

        this.internal_params = new InternalParams(this);
        this.internal_params.loadParams();

        this.visibilityFirstTimeLayout(contact_list_empty);
        this.activateBottomBar();

        if(!contact_list_empty) {
            this.activateAllFeatures();
        }
    }

    private void insertInternalParams(){
        EditText edtTxt_frequency = (EditText) findViewById(R.id.location_frequency_input);
        EditText edtTxt_personal_message = (EditText) findViewById(R.id.input_time_warn);
        if(edtTxt_frequency.getVisibility() == View.VISIBLE) {
            edtTxt_frequency.setText(this.internal_params.getFrequency().toString());
        }

        if(edtTxt_personal_message.getVisibility() == View.VISIBLE) {
            edtTxt_personal_message.setText(this.internal_params.getTimeWarn().toString());
        }
    }

    private void visibilityFirstTimeLayout(Boolean contact_list_empty){

        LinearLayout lyt_settings_first_time = (LinearLayout) findViewById(R.id.layout_settings_first_time);
        LinearLayout lyt_settings = (LinearLayout) findViewById(R.id.layout_settings);
        LinearLayout lyt_bottom_options = (LinearLayout) findViewById(R.id.layout_options);

        lyt_settings.setVisibility(View.VISIBLE);
        lyt_bottom_options.setVisibility(View.GONE);

        if(!contact_list_empty) {
            lyt_settings_first_time.setVisibility(View.GONE);
        }else{
            lyt_settings_first_time.setVisibility(View.VISIBLE);
        }

        TextView txtvw_button_contacts_first_time = (TextView) findViewById(R.id.add_contact_button_first_time);
        txtvw_button_contacts_first_time.setOnClickListener(initialScreenHandler);

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
        LinearLayout lyt_options = (LinearLayout) findViewById(R.id.layout_options);
        LinearLayout input_frequency = (LinearLayout) findViewById(R.id.layout_location_frequency_input);
        LinearLayout input_time = (LinearLayout) findViewById(R.id.layout_time_warn_input);

        input_frequency.setVisibility(View.VISIBLE);
        lyt_options.setVisibility(View.VISIBLE);
        input_time.setVisibility(View.GONE);

        insertInternalParams();

        lyt_options.setOnFocusChangeListener(focusChangeListener);
    }


    private void activateTimeWarn(){
        LinearLayout lyt_options = (LinearLayout) findViewById(R.id.layout_options);
        LinearLayout input_frequency = (LinearLayout) findViewById(R.id.layout_location_frequency_input);
        LinearLayout input_time = (LinearLayout) findViewById(R.id.layout_time_warn_input);

        input_frequency.setVisibility(View.GONE);
        lyt_options.setVisibility(View.VISIBLE);
        input_time.setVisibility(View.VISIBLE);

        insertInternalParams();

        lyt_options.setOnFocusChangeListener(focusChangeListener);
    }

    private void hideOptions(){
        LinearLayout lyt_options = (LinearLayout) findViewById(R.id.layout_options);
        lyt_options.setOnFocusChangeListener(null);
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

    private void saveOptions(){
        EditText edtTxt_frequency = (EditText) findViewById(R.id.location_frequency_input);
        EditText edtTxt_time = (EditText) findViewById(R.id.input_time_warn);

        if(edtTxt_frequency.getVisibility() == View.VISIBLE){
            this.saveFrequency(edtTxt_frequency);
        }
        if(edtTxt_time.getVisibility() == View.VISIBLE){
            this.saveTimeWarn(edtTxt_time);
        }
    }

    private void saveTimeWarn(EditText edtTxt_time) {
        int time = 6;
        try {
            time = Integer.parseInt(edtTxt_time.getText().toString());
        }catch (Exception e){
            time=10;
            e.printStackTrace();
        }
        if(time > 1 && time < 20){
            internal_params.setTimeWarn(time);
        }else{
            this.showOptionsAlert("Error setting time warn", "The time period you selected is invalid, please select another");
        }
    }

    private void saveFrequency(EditText edtTxt_frequency) {
        int frequency=10;
        try {
            frequency = Integer.parseInt(edtTxt_frequency.getText().toString());
        }catch (Exception e){
            frequency=10;
            e.printStackTrace();
        }
        if(frequency > 1 && frequency < 60){
            this.internal_params.setFrequency(frequency);
        }else{
            this.showOptionsAlert("Error setting frequency", "The time period you selected is invalid, please select another");
        }
    }

    private void showOptionsAlert(String title, String msg){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom));

        // Setting Dialog Title
        alertDialog.setTitle(title);

        // Setting Dialog Message
        alertDialog.setMessage(msg);

        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {

            }
        });

        // Showing Alert Message
        try {
            alertDialog.show();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    private void hideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    View.OnFocusChangeListener focusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus && v.getVisibility() == View.VISIBLE) {
                activateAllFeatures();
                activateBottomBar();
                hideKeyboard(v);
                hideOptions();

            }
        }
    };

    View.OnClickListener initialScreenHandler = new View.OnClickListener(){

        public void onClick(View v) {

            switch(v.getId()) {
                case R.id.bottom_bar_back:
                    onBackPressed();
                    break;
                case R.id.bottom_bar_about:
                    Intent intentMainAbout = new Intent(SettingsScreenActivity.this ,AboutScreenActivity.class);
                    SettingsScreenActivity.this.startActivity(intentMainAbout);
                    break;
                case R.id.options_button_cancel:
                    hideOptions();
                    activateAllFeatures();
                    activateBottomBar();
                    hideKeyboard(v);
                    break;
                case R.id.options_button_save:
                    saveOptions();
                    hideOptions();
                    activateAllFeatures();
                    activateBottomBar();
                    hideKeyboard(v);

                    break;
                case R.id.layout_blank_contacts:
                case R.id.add_contact_button_first_time:
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
    @Override
    public void onResume(){
        super.onResume();

        this.contact_list= new ContactList(this);
        this.contact_list.loadAllContacts();
        Boolean contact_list_empty= this.contact_list.isContactListEmpty();

        this.internal_params = new InternalParams(this);
        this.internal_params.loadParams();

        this.visibilityFirstTimeLayout(contact_list_empty);
        this.activateBottomBar();

        if(!contact_list_empty) {
            this.activateAllFeatures();
        }
    }
}