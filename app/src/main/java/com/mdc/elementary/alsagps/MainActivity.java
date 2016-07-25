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

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.loading_screen);
        //this.deleteDatabase("AlsaGPS.db");

        final ImportAgendaContacts iac = new ImportAgendaContacts(this);

        if (!iac.contactsImported()) {
            this.firstTimeBehaviour(iac);
        }
        else{
            Thread updateAgendaContactsThread = new Thread() {
                @Override
                public void run() {
                    try {
                        super.run();
                        Log.e("CREATE", "run UpdateContacts");
                        iac.fetchAgendaContacts();
                        Log.e("CREATE", "(run) end UpdateContacts");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            updateAgendaContactsThread.start();
            startApp();
        }
    }

    private void firstTimeBehaviour(final ImportAgendaContacts iac) {
        Thread firstImportAgendaContactsThread = new Thread() {
                @Override
            public void run() {
                try {
                    super.run();
                    Log.e("CREATE", "run UpdateContacts");
                    iac.fetchAgendaContacts();
                    Log.e("CREATE", "(run) end UpdateContacts");
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    LinearLayout lyt_starting_points = (LinearLayout) findViewById(R.id.layout_loading_first_time);
                    lyt_starting_points.setOnClickListener(initialScreenHandler);
                }
            }
        };
        firstImportAgendaContactsThread.start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                LinearLayout lyt_explain = (LinearLayout) findViewById(R.id.layout_explain_first_time);
                lyt_explain.setVisibility(View.VISIBLE);
            }
        }, 5000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                LinearLayout lyt_explain = (LinearLayout) findViewById(R.id.layout_start_arrow);
                lyt_explain.setVisibility(View.VISIBLE);
            }
        }, 9000);
    }

    private void startApp(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intentMain = new Intent(MainActivity.this , InitialScreenActivity.class);
                MainActivity.this.startActivity(intentMain);
                finish();
            }
        }, 5000);
    }

    View.OnClickListener initialScreenHandler = new View.OnClickListener(){

        public void onClick(View v) {

            switch(v.getId()) {
                case R.id.layout_loading_first_time:
                    startApp();
                    break;
            }
        }
    };
}