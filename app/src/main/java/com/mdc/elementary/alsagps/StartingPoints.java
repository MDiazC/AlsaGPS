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
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class StartingPoints extends Activity {

    private HashMap<String, ArrayList> starting_points_list = null;
    private Context context = null;
    private final static String SP_TABLE_NAME = "starting_points";
    private final static String SP_COLUMN_ID = "id";
    private final static String SP_COLUMN_NAME = "name";
    private final static String SP_COLUMN_LATITUDE = "latitude";
    private final static String SP_COLUMN_LONGITUDE = "longitude";

    public StartingPoints(Context context){
        starting_points_list = new HashMap<String,  ArrayList>();
        this.context = context;
    }

    public boolean isStartingPointsListEmpty(){
        return this.starting_points_list == null || (this.starting_points_list != null && this.starting_points_list.isEmpty());
    }

    public static String getCreateTable() {
        return "CREATE TABLE IF NOT EXISTS " + SP_TABLE_NAME + "("+SP_COLUMN_ID+
                " INTEGER PRIMARY KEY, "+SP_COLUMN_NAME+" TEXT, "+SP_COLUMN_LATITUDE+" FLOAT, "+SP_COLUMN_LONGITUDE+" FLOAT)";
    }
    public static String getDeleteTable() {
        return "DROP TABLE IF EXISTS "+ SP_TABLE_NAME;
    }

    public HashMap<String, ArrayList> getStartingPoints(){
        return this.starting_points_list;
    }

    public void loadStartingPoints(){
        DBHelper dbh = new DBHelper(this.context);
        String selectQuery =  "SELECT  * FROM " + SP_TABLE_NAME;
        this.starting_points_list = new HashMap<String, ArrayList>();

        try{
            SQLiteDatabase db =dbh.get(selectQuery);
            Cursor cursor = db.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    ArrayList aux_array = new ArrayList<Double>();
                    aux_array.add(cursor.getDouble(cursor.getColumnIndex(SP_COLUMN_LATITUDE)));
                    aux_array.add(cursor.getDouble(cursor.getColumnIndex(SP_COLUMN_LONGITUDE)));
                    this.starting_points_list.put(cursor.getString(cursor.getColumnIndex(SP_COLUMN_NAME)), aux_array);
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public int insertStartingPoint(String name, String latitude, String longitude){

        ContentValues contentValues = new ContentValues();
        contentValues.put(SP_COLUMN_NAME, name);
        contentValues.put(SP_COLUMN_LATITUDE, latitude);
        contentValues.put(SP_COLUMN_LONGITUDE, longitude);

        DBHelper db = new DBHelper(this.context);
        int id =db.insert(this.SP_TABLE_NAME, contentValues);
        return id;

    }

    public void deleteStartingPoint(String name){
        DBHelper db = new DBHelper(this.context);
        String whereClause=" "+SP_COLUMN_NAME+" = ?";
        String[] whereArgs = new String[] { name };
        db.delete(this.SP_TABLE_NAME, whereClause, whereArgs);
    }

    public boolean matchingWithPosition(double[] coordinates){
        boolean matching = false;
        boolean success;

        this.loadStartingPoints();
        HashMap points = this.getStartingPoints();

        Iterator it = points.entrySet().iterator();

        ArrayList<Double> spCoordinates;
        double spLat, spLon;

        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();

            try {
                spCoordinates = (ArrayList)pair.getValue();

                spLat=(Double) spCoordinates.get(0);
                Log.e("CREATE", "matchingPositionWithSP get lat "+spLat);
                spLon=(Double) spCoordinates.get(1);
                Log.e("CREATE", "matchingPositionWithSP get lon "+spLon);
                success=this.compareCoordinates(coordinates[0], coordinates[1], spLat, spLon);
                if(success){
                    matching=true;
                    break;
                }

            }catch (ClassCastException e){
                e.printStackTrace();
            }

            it.remove();
        }
        return matching;
    }

    private boolean compareCoordinates(double currentLat, double currentLon, double spLat, double spLon){
        boolean equal = false;
        double distance = 0.001;
        Log.e("CREATE", "culat "+currentLat+" cuLon "+currentLon+" spLa "+spLat+" spLo "+spLon);
        if(currentLat + distance > spLat && currentLat - distance < spLat){
            Log.e("CREATE", "Match lat");
            if(currentLon + distance > spLon && currentLon - distance < spLon) {
                Log.e("CREATE", "Match lon");
                equal = true;
            }
        }
        return equal;
    }

}
