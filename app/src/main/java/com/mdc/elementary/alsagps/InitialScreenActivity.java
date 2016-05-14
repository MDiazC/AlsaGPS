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
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class InitialScreenActivity extends Activity{

    private Boolean isActiveApp=false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.initial_screen);
        Log.d("CREATION", "prelanzado activiydad");

        ContactList contact_list= new ContactList(this);
        ArrayList ar= contact_list.getAllContacts();

        LinearLayout lyt_settings = (LinearLayout) findViewById(R.id.bottom_bar_settings);

        if(!ar.isEmpty()) {
            this.hideFirstTimeLayout();
        }


    }

    private void hideFirstTimeLayout(){

        LinearLayout lyt_initial_first_time = (LinearLayout) findViewById(R.id.layout_app_first_time);
        LinearLayout lyt_initial_first_time_bottom_bar = (LinearLayout) findViewById(R.id.bottom_bar_first_time);
        LinearLayout lyt_initial_bottom_bar = (LinearLayout) findViewById(R.id.bottom_bar);

        lyt_initial_first_time.setVisibility(View.GONE);
        lyt_initial_first_time_bottom_bar.setVisibility(View.GONE);
        lyt_initial_bottom_bar.setVisibility(View.VISIBLE);

    }

    private void appActive(){

        LinearLayout lyt_initial_first_time = (LinearLayout) findViewById(R.id.layout_app_first_time);
        LinearLayout lyt_initial_switch_button_on = (LinearLayout) findViewById(R.id.layout_layout_switch_button_enabled);
        LinearLayout lyt_initial_switch_button_off = (LinearLayout) findViewById(R.id.layout_layout_switch_button_disabled);
        LinearLayout lyt_initial_first_time_bottom_bar = (LinearLayout) findViewById(R.id.bottom_bar_first_time);
        LinearLayout lyt_initial_bottom_bar = (LinearLayout) findViewById(R.id.bottom_bar);


        if(this.isActiveApp){
            lyt_initial_first_time.setVisibility(View.GONE);
            lyt_initial_first_time_bottom_bar.setVisibility(View.GONE);
            lyt_initial_bottom_bar.setVisibility(View.VISIBLE);
            lyt_initial_switch_button_on.setVisibility(View.VISIBLE);
            lyt_initial_switch_button_off.setVisibility(View.GONE);
        }else{
            lyt_initial_first_time.setVisibility(View.GONE);
            lyt_initial_first_time_bottom_bar.setVisibility(View.GONE);
            lyt_initial_bottom_bar.setVisibility(View.VISIBLE);
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
    }
}
