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
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;

public class PersonalMessageActivity extends Activity{
    private InternalParams internal_params=null;

    public PersonalMessageActivity(){
        this.internal_params= new InternalParams(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.internal_params.loadParams();
        setContentView(R.layout.personal_message);

        this.activateAllFeatures();
        this.insertInViewPersonalMessage();
    }

    private void insertInViewPersonalMessage(){
        EditText edtTxt_personal_message = (EditText) findViewById(R.id.personal_message_text);
        edtTxt_personal_message.setText(this.internal_params.getPersonalMessage());
    }

    private void activateAllFeatures(){
        LinearLayout lyt_back_button = (LinearLayout) findViewById(R.id.bottom_bar_back);
        LinearLayout lyt_about = (LinearLayout) findViewById(R.id.bottom_bar_about);
        LinearLayout btn_save = (LinearLayout) findViewById(R.id.layout_save_button);

        lyt_back_button.setOnClickListener(initialScreenHandler);
        lyt_about.setOnClickListener(initialScreenHandler);
        btn_save.setOnClickListener(initialScreenHandler);

        LinearLayout layout_root = (LinearLayout) findViewById(R.id.layout_personal_message);
        LinearLayout layout_body = (LinearLayout) findViewById(R.id.layout_body_personal_message);
        layout_root.setOnFocusChangeListener(focusChangeListener);
        layout_body.setOnFocusChangeListener(focusChangeListener);
    }



    public void hideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    private void saveMessage(){
        EditText edtTxt_personal_message = (EditText) findViewById(R.id.personal_message_text);
        String message = edtTxt_personal_message.getText().toString();
        if(!message.equals("")){
            internal_params.setPersonalMessage(message);

        }
    }

    View.OnFocusChangeListener focusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                hideKeyboard(v);
            }
        }
    };

    View.OnClickListener initialScreenHandler = new View.OnClickListener(){

        public void onClick(View v) {

            switch(v.getId()) {
                case R.id.bottom_bar_back:
                    Intent intentMainSettings = new Intent(PersonalMessageActivity.this ,SettingsScreenActivity.class);
                    PersonalMessageActivity.this.startActivity(intentMainSettings);
                    break;
                case R.id.bottom_bar_about:
                    Intent intentMainAbout = new Intent(PersonalMessageActivity.this ,AboutScreenActivity.class);
                    PersonalMessageActivity.this.startActivity(intentMainAbout);
                    break;
                case R.id.layout_save_button:
                    saveMessage();
                    hideKeyboard(v);
                    v.clearFocus();
                    break;
            }
        }
    };
}
