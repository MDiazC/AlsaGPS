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

import java.util.ArrayList;
import java.util.HashMap;

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
                    ArrayList aux_array = new ArrayList();
                    aux_array.add(cursor.getString(cursor.getColumnIndex(SP_COLUMN_LATITUDE)));
                    aux_array.add(cursor.getString(cursor.getColumnIndex(SP_COLUMN_LONGITUDE)));
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
}
