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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class StartingPointActivity extends Activity implements MyCallback{
    private StartingPoints starting_points = null;
    ArrayAdapter<String> arrayAdapter=null;
    ThreadUpdateCoordsSP updateCoords;

    public StartingPointActivity() {
        this.starting_points = new StartingPoints(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.starting_points.loadStartingPoints();
        setContentView(R.layout.starting_points);

        activateAllFeatures();
        fillListView();
        getCoordinates();

        if(this.starting_points.isStartingPointsListEmpty())
            Log.e("CREATE", "SP vacio");
        else
            Log.e("CREATE", "SP lleno");

        Log.e("CREATE", "numero de elemente "+this.starting_points.getStartingPoints().size());

        if(!this.starting_points.isStartingPointsListEmpty()){
            this.hideLayoutInfoHelper();
            Log.e("CREATE","Starting pint list no vacia");
        }
    }

    private void hideLayoutInfoHelper(){
        LinearLayout lyt_about = (LinearLayout) findViewById(R.id.layout_layout_sp_explanation);
        lyt_about.setVisibility(View.GONE);
    }

    private void fillListView() {
        ListView lv;

        List<String> list_names = this.getStartingPoints();

        lv = (ListView) findViewById(R.id.list_view_starting_points);

        if(list_names != null && lv!= null) {
            this.arrayAdapter = new ArrayAdapter<String>(
                    this,
                    R.layout.list_contacts_remove, R.id.contact_name,
                    list_names);

            lv.setAdapter(this.arrayAdapter);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String selected_starting_point = (String) parent.getAdapter().getItem(position);
                    removeContact(selected_starting_point);
                }
            });
        }
    }

    public void btnRemoveContact(View v){
        ListView lv = (ListView) findViewById(R.id.list_view_starting_points);
        final int position = lv.getPositionForView((View) v.getParent());
        String selected_starting_point = (String)  lv.getAdapter().getItem(position);
        removeContact(selected_starting_point);
    }

    private List<String> getStartingPoints(){
        HashMap points = this.starting_points.getStartingPoints();
        Log.e("CREATE","asda "+points.size());
        ArrayList<String> list_names = new ArrayList<String>();

        Iterator it = points.entrySet().iterator();
        String nameContact = null;

        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();

            try {
                nameContact = (String)pair.getKey();
            }catch (ClassCastException e){
                e.printStackTrace();
            }
            list_names.add(nameContact);
        }
        return list_names;
    }

    private void removeContact(String starting_point_name){
        starting_points.deleteStartingPoint(starting_point_name);
        arrayAdapter.remove(starting_point_name);
    }

    private void activateAllFeatures(){

        LinearLayout button_save = (LinearLayout) findViewById(R.id.layout_save_current_position_button);
        EditText inpt_add_name_starting_point = (EditText) findViewById(R.id.type_current_position_name_input);
        LinearLayout lyt_back_button = (LinearLayout) findViewById(R.id.bottom_bar_back);
        LinearLayout lyt_about = (LinearLayout) findViewById(R.id.bottom_bar_about);
        LinearLayout lyt_helper = (LinearLayout) findViewById(R.id.layout_layout_sp_explanation);

        lyt_back_button.setOnClickListener(initialScreenHandler);
        lyt_about.setOnClickListener(initialScreenHandler);

        button_save.setOnClickListener(initialScreenHandler);
        lyt_helper.setOnClickListener(initialScreenHandler);
        inpt_add_name_starting_point.setOnFocusChangeListener(focusChangeListener);
    }

    private void savePosition() {
        EditText edtTxt_name = (EditText) findViewById(R.id.type_current_position_name_input);
        TextView edtTxt_longitude = (TextView) findViewById(R.id.current_position_longitude_number);
        TextView edtTxt_latitude = (TextView) findViewById(R.id.current_position_latitude_number);

        String name = edtTxt_name.getText().toString();
        String longitude = edtTxt_longitude.getText().toString();
        String latitude = edtTxt_latitude.getText().toString();
        if (!name.equals("") && !longitude.equals("") && !latitude.equals("")) {
            if (this.isNumber(latitude) && this.isNumber(longitude)) {
                saveStartingPoint(name, latitude, longitude);
                edtTxt_name.setText("");
                edtTxt_name.clearFocus();
            }
        }
    }

    private Boolean isNumber(String number){
        boolean result;
        try {
            result=true;
            Double num = Double.valueOf(number);
        }catch (NumberFormatException e){
            result=false;
        }
        return result;
    }

    private void saveStartingPoint(String name, String latitude, String longitude){
        starting_points.insertStartingPoint(name, latitude, longitude);
        starting_points.loadStartingPoints();
        fillListView();
    }

    private void hideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    private void getCoordinates(){
        GPSSystem gps = new GPSSystem(this);
        if(gps.canGetLocation()){
            this.updateCoords = new ThreadUpdateCoordsSP(gps);
            this.updateCoords.callback = this;
            this.updateCoords.run();
        }else{
            gps.showSettingsAlert();
        }
        gps.stopUsingGPS();
        gps=null;
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
                    onBackPressed();
                    break;
                case R.id.bottom_bar_about:
                    Intent intentMainAbout = new Intent(StartingPointActivity.this ,AboutScreenActivity.class);
                    StartingPointActivity.this.startActivity(intentMainAbout);
                    break;
                case R.id.layout_save_current_position_button:
                    savePosition();
                    hideKeyboard(v);
                    break;
                case R.id.layout_layout_sp_explanation:
                    hideLayoutInfoHelper();
                    break;
           }
        }
    };

    public void updateCoordinates(double latitude, double longitude ) {
        TextView edtTxt_longitude = (TextView) findViewById(R.id.current_position_longitude_number);
        TextView edtTxt_latitude = (TextView) findViewById(R.id.current_position_latitude_number);

        if(edtTxt_latitude.getVisibility() == View.VISIBLE && edtTxt_longitude.getVisibility() == View.VISIBLE) {
            edtTxt_longitude.setText(" " + Double.toString(Math.floor(longitude * 100000) / 100000));
            edtTxt_latitude.setText(" " + Double.toString(Math.floor(latitude * 100000) / 100000));
        }
    }

    @Override
    public void stopApp(){}
}