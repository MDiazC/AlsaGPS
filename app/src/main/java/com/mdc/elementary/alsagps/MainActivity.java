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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.loading_screen);

        Thread welcomeThread = new Thread() {

            @Override
            public void run() {
                try {
                    super.run();
                    sleep(10000);  //Delay of 10 seconds
                } catch (Exception e) {

                } finally {

                    Intent intentMain = new Intent(MainActivity.this , InitialScreenActivity.class);
                    MainActivity.this.startActivity(intentMain);
                    finish();
                }
            }
        };
        welcomeThread.start();
    }
}

/*

public class Activity1 extends Activity {
//
@Override
public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    Button next = (Button) findViewById(R.id.Button01);
    next.setOnClickListener(new View.OnClickListener() {
        public void onClick(View view) {
            Intent myIntent = new Intent(view.getContext(), Activity2.class);
            startActivityForResult(myIntent, 0);
        }

    });
}
}

public class Activity2 extends Activity {

//
public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main2);

    Button next = (Button) findViewById(R.id.Button02);
    next.setOnClickListener(new View.OnClickListener() {
        public void onClick(View view) {
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        }

    });
}


-------------------------------------------------
Also Make Sure to Create 2 different xml in Layout folder and add both Activity in Manifest File like

<activity android:name=".Activity2"></activity>
-------------------------------------------------



            // Hide status bar
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            // Show status bar
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        */
