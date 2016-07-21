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
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class GPSPartial  extends Activity {
    private float latitude = 0;
    private float longitude = 0;
    private Context context = null;
    private final static String GPSP_TABLE_NAME = "gps_partial";
    private final static String GPSP_COLUMN_ID = "id";
    private final static String GPSP_COLUMN_LATITUDE = "latitude";
    private final static String GPSP_COLUMN_LONGITUDE = "longitude";
    private final static String GPSP_COLUMN_TIMESTAMP = "timestamp";

    public GPSPartial(Context context){
        this.context = context;
    }

    public static String getCreateTable() {
        return "CREATE TABLE IF NOT EXISTS " + GPSP_TABLE_NAME + "("+GPSP_COLUMN_ID+
                " INTEGER PRIMARY KEY, "+GPSP_COLUMN_LATITUDE+" FLOAT, "+GPSP_COLUMN_LONGITUDE+" FLOAT, "+GPSP_COLUMN_TIMESTAMP+" DATETIME DEFAULT CURRENT_TIMESTAMP)";
    }
    public static String getDeleteTable() {
        return "DROP TABLE IF EXISTS "+ GPSP_TABLE_NAME;
    }

    public float getLastPartialLatitude(){
        return this.latitude;
    }
    public float getLastPartialLongitude(){
        return this.longitude;
    }

    public void loadLastPosition(){
        DBHelper dbh = new DBHelper(this.context);
        String selectQuery =  "SELECT "+this.latitude+", "+this.longitude+" FROM " +this.GPSP_TABLE_NAME+" ORDER BY "+this.GPSP_COLUMN_TIMESTAMP+" DESC LIMIT 1";

        try{
            SQLiteDatabase db =dbh.get(selectQuery);
            Cursor cursor = db.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    this.latitude = Float.valueOf(cursor.getString(cursor.getColumnIndex(GPSP_COLUMN_LATITUDE)));
                    this.longitude = Float.valueOf(cursor.getString(cursor.getColumnIndex(GPSP_COLUMN_LONGITUDE)));
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public int insertPosition(double latitude, double longitude){
        int id =0;
        ContentValues contentValues = new ContentValues();
        contentValues.put(GPSP_COLUMN_LATITUDE, latitude);
        contentValues.put(GPSP_COLUMN_LONGITUDE, longitude);

        DBHelper db = new DBHelper(this.context);
        if(latitude != 0.0 && longitude != 0.0) {
            id = db.insert(this.GPSP_TABLE_NAME, contentValues);
        }
        return id;

    }

    public void deleteOldPositions(){
        DBHelper db = new DBHelper(this.context);
        String whereClause=" "+GPSP_COLUMN_TIMESTAMP+" < datetime('now', '-1 week')";
        String[] whereArgs = new String[] {};
        db.delete(this.GPSP_TABLE_NAME, whereClause, whereArgs);
    }

    public void showAllPositions(){
        DBHelper dbh = new DBHelper(this.context);
        String selectQuery =  "SELECT "+this.GPSP_COLUMN_LATITUDE+", "+this.GPSP_COLUMN_LONGITUDE+" FROM " +this.GPSP_TABLE_NAME+" ORDER BY "+this.GPSP_COLUMN_TIMESTAMP+" DESC";
        float lat , lon;
        int i =0;
        try{
            SQLiteDatabase db =dbh.get(selectQuery);
            Cursor cursor = db.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    lat = Float.valueOf(cursor.getString(cursor.getColumnIndex(GPSP_COLUMN_LATITUDE)));
                    lon = Float.valueOf(cursor.getString(cursor.getColumnIndex(GPSP_COLUMN_LONGITUDE)));
                    Log.e("CREATE","lat "+lat+" lon "+lon+" id "+i++);
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }
}
