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

import java.util.ArrayList;
import java.util.HashMap;

public class DBHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "AlsaGPS.db";
    public String SQL_CREATE_ENTRIES="";
    public String SQL_DELETE_ENTRIES="";

    public DBHelper(Context context, String query_create_table, String query_delete_table) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.SQL_CREATE_ENTRIES=query_create_table;
        this.SQL_DELETE_ENTRIES = query_delete_table;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //All necessary tables you like to create will create here

        db.execSQL(this.SQL_CREATE_ENTRIES);

        /*
        try {
            db.execSQL(this.SQL_CREATE_ENTRIES);
        }catch (Exception e){
            Throw E
        } */
        //@todo exceptions
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed, all data will be gone!!!
        db.execSQL(this.SQL_DELETE_ENTRIES);

        // Create tables again
        onCreate(db);

        //@todo exceptions

    }

    public int insert(String table_name, ContentValues values) {

        //Open connection to write data
        SQLiteDatabase db = this.getWritableDatabase();
        /*ContentValues values = new ContentValues();
        values.put(Student.KEY_age, student.age);
        values.put(Student.KEY_email,student.email);
        values.put(Student.KEY_name, student.name); */

        // Inserting Row
        long id = db.insert(table_name, null, values);
        db.close(); // Closing database connection
        return (int) id;
        //@todo exceptions
    }

    public void delete(String table_name, String whereClause, String[] whereArgs) {

        SQLiteDatabase db = this.getWritableDatabase();
        // It's a good practice to use parameter ?, instead of concatenate string

        /*
        whereArgs = new String[] { String.valueOf(student_Id) }
         */
        db.delete(table_name, whereClause, whereArgs);
        db.close(); // Closing database connection
        //@todo exceptions
    }

    public void update(String table_name, ContentValues values, String whereClause, String[] whereArgs) {

        SQLiteDatabase db = this.getWritableDatabase();
        /*
        ContentValues values = new ContentValues();

        values.put(Student.KEY_age, student.age);
        values.put(Student.KEY_email,student.email);
        values.put(Student.KEY_name, student.name);

        whereArgs = new String[] { String.valueOf(student.student_ID) }
        */

        // It's a good practice to use parameter ?, instead of concatenate string
        db.update(table_name, values, whereClause, whereArgs);
        db.close(); // Closing database connection
        //@todo exceptions
    }

    public ArrayList get(String selectQuery){

        SQLiteDatabase db = this.getReadableDatabase();

        /*
        String selectQuery =  "SELECT  " +
                Student.KEY_ID + "," +
                Student.KEY_name + "," +
                Student.KEY_email + "," +
                Student.KEY_age +
                " FROM " + Student.TABLE; */

        //Student student = new Student();
        ArrayList<HashMap<String, String>> studentList = new ArrayList<HashMap<String, String>>();

        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list

        if (cursor.moveToFirst()) {
            do {
                /*
                HashMap<String, String> student = new HashMap<String, String>();
                student.put("id", cursor.getString(cursor.getColumnIndex(Student.KEY_ID)));
                student.put("name", cursor.getString(cursor.getColumnIndex(Student.KEY_name)));
                studentList.add(student);*/

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return studentList;
        //@todo exceptions

    }

}