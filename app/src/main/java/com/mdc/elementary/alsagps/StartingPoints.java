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
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class StartingPoints extends Activity {

    private ArrayList starting_points_list = null;
    private Context context = null;
    private final static String SP_TABLE_NAME = "starting_points";
    private final static String SP_COLUMN_ID = "id";
    private final static String SP_COLUMN_NAME = "name";
    private final static String SP_COLUMN_LATITUDE = "latitude";
    private final static String SP_COLUMN_LONGITUDE = "longitude";

    public StartingPoints(Context context){
        starting_points_list = new ArrayList<>();
        this.context = context;
    }

    public static String getCreateTable() {
        return "CREATE TABLE IF NOT EXISTS " + SP_TABLE_NAME + "("+SP_COLUMN_ID+
                " INTEGER PRIMARY KEY, "+SP_COLUMN_NAME+" TEXT, "+SP_COLUMN_LATITUDE+" FLOAT, "+SP_COLUMN_LONGITUDE+" FLOAT)";
    }
    public static String getDeleteTable() {
        return "DROP TABLE IF EXISTS "+ SP_TABLE_NAME;
    }

    public ArrayList getStartingPoints(){
        return this.starting_points_list;
    }

    public void loadStartingPoints(){
        DBHelper db = new DBHelper(this.context);
        String selectQuery =  "SELECT  * FROM " + this.SP_TABLE_NAME;
        ArrayList array_list =db.get(selectQuery);
    }

    public int insertStartingPoint(String name, Float latitude, Float longitude){

        ContentValues contentValues = new ContentValues();
        contentValues.put(SP_COLUMN_NAME, name);
        contentValues.put(SP_COLUMN_LATITUDE, latitude);
        contentValues.put(SP_COLUMN_LONGITUDE, longitude);

        DBHelper db = new DBHelper(this.context);
        int id =db.insert(this.SP_TABLE_NAME, contentValues);
        return id;

    }

    public void deleteStartingPoint(int id){
        DBHelper db = new DBHelper(this.context);
        String whereClause=" WHERE id = ?";
        String[] whereArgs = new String[] { String.valueOf(id) };
        db.delete(this.SP_TABLE_NAME, whereClause, whereArgs);
    }
}
