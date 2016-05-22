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

public class StartingPointActivity extends Activity {
    private StartingPoints starting_points = null;

    public StartingPointActivity() {
        this.starting_points = new StartingPoints(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.starting_points.loadStartingPoints();
        setContentView(R.layout.starting_points);

        activateAllFeatures();
    }

    private void activateAllFeatures(){

        LinearLayout button_save = (LinearLayout) findViewById(R.id.layout_save_current_position_button);
        Button btn_remove_starting_point = (Button) findViewById(R.id.remove_button);
        EditText inpt_add_name_starting_point = (EditText) findViewById(R.id.type_current_position_name_input);
        LinearLayout lyt_back_button = (LinearLayout) findViewById(R.id.bottom_bar_back);
        LinearLayout lyt_about = (LinearLayout) findViewById(R.id.bottom_bar_about);

        lyt_back_button.setOnClickListener(initialScreenHandler);
        lyt_about.setOnClickListener(initialScreenHandler);

        btn_remove_starting_point.setOnClickListener(initialScreenHandler);
        button_save.setOnClickListener(initialScreenHandler);

        inpt_add_name_starting_point.setOnFocusChangeListener(focusChangeListener);
    }

    private void savePosition(){}

    private void removePosition(){}

    private void hideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    View.OnFocusChangeListener focusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (!hasFocus) {
                hideKeyboard(v);
            }
        }
    };

    View.OnClickListener initialScreenHandler = new View.OnClickListener(){

        public void onClick(View v) {

            switch(v.getId()) {
                case R.id.bottom_bar_back:
                    Intent intentMainSettings = new Intent(StartingPointActivity.this ,InitialScreenActivity.class);
                    StartingPointActivity.this.startActivity(intentMainSettings);
                    break;
                case R.id.bottom_bar_about:
                    Intent intentMainAbout = new Intent(StartingPointActivity.this ,AboutScreenActivity.class);
                    StartingPointActivity.this.startActivity(intentMainAbout);
                    break;
                case R.id.layout_save_current_position_button:
                    savePosition();
                    hideKeyboard(v);
                    break;
                case R.id.remove_button:
                    removePosition();
                    break;
           }
        }
    };
}