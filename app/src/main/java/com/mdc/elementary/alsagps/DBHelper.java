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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class DBHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "AlsaGPS.db";
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //All necessary tables you like to create will create here
        db.execSQL(ContactList.getCreateTable());
        db.execSQL(InternalParams.getCreateTable());
        db.execSQL(StartingPoints.getCreateTable());
        db.execSQL(GPSPartial.getCreateTable());
        db.execSQL(AgendaContactsList.getCreateTable());

        //@todo exceptions
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed, all data will be gone!!!
        db.execSQL(ContactList.getDeleteTable());
        db.execSQL(InternalParams.getDeleteTable());
        db.execSQL(StartingPoints.getDeleteTable());
        db.execSQL(GPSPartial.getDeleteTable());
        db.execSQL(AgendaContactsList.getDeleteTable());

        onCreate(db);

        //@todo exceptions

    }

    public int insert(String table_name, ContentValues values) {

        //Open connection to write data
        SQLiteDatabase db = this.getWritableDatabase();

        // Inserting Row
        long id = db.insert(table_name, null, values);
        db.close(); // Closing database connection
        return (int) id;
        //@todo exceptions
    }

    public boolean delete(String table_name, String whereClause, String[] whereArgs) {

        SQLiteDatabase db = this.getWritableDatabase();

        int result = db.delete(table_name, whereClause, whereArgs);
        db.close(); // Closing database connection
        return result > 0;
        //@todo exceptions
    }

    public void update(String table_name, ContentValues values, String whereClause, String[] whereArgs) {

        SQLiteDatabase db = this.getWritableDatabase();

        // It's a good practice to use parameter ?, instead of concatenate string
        int i = db.update(table_name, values, whereClause, whereArgs);
        db.close(); // Closing database connection
        //@todo exceptions
    }

    public SQLiteDatabase get(String selectQuery){

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        return db;
        //@todo exceptions
    }

}