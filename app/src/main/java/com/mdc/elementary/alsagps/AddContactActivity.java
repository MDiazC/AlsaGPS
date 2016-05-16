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
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

public class AddContactActivity  extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.add_contact_screen);

        this.activeAllFeatures();
    }

    private void activeAllFeatures(){
        LinearLayout lyt_back_button = (LinearLayout) findViewById(R.id.bottom_bar_back);
        LinearLayout lyt_about = (LinearLayout) findViewById(R.id.bottom_bar_about);
        LinearLayout lyt_add_contact = (LinearLayout) findViewById(R.id.button_add_contact_manually);
        Button btn_add_contact_green = (Button) findViewById(R.id.add_contact_green);

        LinearLayout btn_cancel = (LinearLayout) findViewById(R.id.options_button_cancel);
        LinearLayout btn_save = (LinearLayout) findViewById(R.id.options_button_save);


        lyt_add_contact.setOnClickListener(initialScreenHandler);
        lyt_back_button.setOnClickListener(initialScreenHandler);
        lyt_about.setOnClickListener(initialScreenHandler);
        btn_add_contact_green.setOnClickListener(initialScreenHandler);

        btn_save.setOnClickListener(initialScreenHandler);
        btn_cancel.setOnClickListener(initialScreenHandler);
    }



    private void addContact(){

    }

    private void showLayoutOptions(){
        LinearLayout lyt_button_time = (LinearLayout) findViewById(R.id.layout_layout_add_contact_bottom);
        lyt_button_time.setVisibility(View.VISIBLE);
    }


    private void hideLayoutOptions(){
        LinearLayout lyt_button_time = (LinearLayout) findViewById(R.id.layout_layout_add_contact_bottom);
        lyt_button_time.setVisibility(View.GONE);
    }

    View.OnClickListener initialScreenHandler = new View.OnClickListener(){

        public void onClick(View v) {

            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

            switch(v.getId()) {
                case R.id.bottom_bar_back:
                    Intent intentMainSettings = new Intent(AddContactActivity.this ,ContactsScreenActivity.class);
                    AddContactActivity.this.startActivity(intentMainSettings);
                    break;
                case R.id.bottom_bar_about:
                    Intent intentMainAbout = new Intent(AddContactActivity.this ,AboutScreenActivity.class);
                    AddContactActivity.this.startActivity(intentMainAbout);
                    break;
                case R.id.button_add_contact_manually:
                    showLayoutOptions();
                    //addContact();
                    //imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    break;
                case R.id.add_contact_green:
                    addContact();
                    break;
                case R.id.options_button_cancel:
                    hideLayoutOptions();
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    break;
                case R.id.options_button_save:
                    hideLayoutOptions();
                    //addContact();
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    break;
            }
        }
    };
}
