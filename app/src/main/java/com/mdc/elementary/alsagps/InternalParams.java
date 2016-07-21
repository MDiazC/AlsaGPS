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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class InternalParams{
    private Integer time_warn = 0;
    private Integer frequency = 0;
    private String message = null;

    private final static String PARAMS_TABLE_NAME = "internal_params";
    private final static String PARAMS_COLUMN_ID = "id";
    private final static String PARAMS_COLUMN_PERSONAL_MESSAGE = "personal_message";
    private final static String PARAMS_COLUMN_LOCATION_FREQUENCY = "location_frequency";
    private final static String PARAMS_COLUMN_TIME_WARN = "time_warn";
    private Context context = null;
    private String SQL_DELETE_TABLE = "DROP TABLE IF EXISTS "+ PARAMS_TABLE_NAME;

    public InternalParams(Context context){
        time_warn = 6;
        frequency = 10;
        message = "";
        this.context=context;
    }

    public static String getCreateTable(){
        return "CREATE TABLE IF NOT EXISTS " + PARAMS_TABLE_NAME + "("+PARAMS_COLUMN_ID+
                " INTEGER PRIMARY KEY, "+PARAMS_COLUMN_PERSONAL_MESSAGE+" TEXT, "+PARAMS_COLUMN_LOCATION_FREQUENCY+" INTEGER, "+PARAMS_COLUMN_TIME_WARN+" INTEGER)";
    }

    public static String getDeleteTable(){
        return "DROP TABLE IF EXISTS "+ PARAMS_TABLE_NAME;
    }

    public Integer getTimeWarn(){
        return this.time_warn;
    }

    public Integer getFrequency(){
        return this.frequency;
    }

    public String getPersonalMessage(){
        return (this.message != null && !this.message.isEmpty())?this.message:"";
    }

    public void setTimeWarn(int new_time){
        this.time_warn = new_time;
        this.updateParams();
    }

    public void setFrequency(int new_frequency){
        this.frequency = new_frequency;
        this.updateParams();
    }

    public void setPersonalMessage(String new_message){
        if(new_message != null && !new_message.isEmpty()){
            this.message=new_message;
            this.updateParams();
            //@todo Si hay excepción hay que setear el valor anterior
        }
    }

    public void loadParams(){
        DBHelper dbh = new DBHelper(this.context);
        String selectQuery =  "SELECT  * FROM  " + PARAMS_TABLE_NAME;
        try {
            SQLiteDatabase db =dbh.get(selectQuery);
            Cursor cursor = db.rawQuery(selectQuery, null);
            boolean isEmptyTable = true;

            if (cursor.moveToFirst()) {
                do {
                    this.time_warn = Integer.valueOf(cursor.getString(cursor.getColumnIndex(PARAMS_COLUMN_TIME_WARN)));
                    this.frequency = Integer.valueOf(cursor.getString(cursor.getColumnIndex(PARAMS_COLUMN_LOCATION_FREQUENCY)));
                    this.message = cursor.getString(cursor.getColumnIndex(PARAMS_COLUMN_PERSONAL_MESSAGE));
                     isEmptyTable=false;
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();

            if(isEmptyTable){
                this.createRegisters();
            }

        } catch (SQLException e){
            e.printStackTrace();
        }

    }

    private boolean updateParams ()
    {
        DBHelper db = new DBHelper(this.context);

        ContentValues contentValues = new ContentValues();
        contentValues.put(PARAMS_COLUMN_LOCATION_FREQUENCY, this.frequency);
        this.message = this.escapeString(this.message);
        contentValues.put(PARAMS_COLUMN_PERSONAL_MESSAGE, this.message);
        contentValues.put(PARAMS_COLUMN_TIME_WARN, this.time_warn);
        db.update(PARAMS_TABLE_NAME, contentValues, "id = ? ", new String[] { "1" } );
        return true;
    }

    private String escapeString(String text){
        text = DatabaseUtils.sqlEscapeString(text);
        StringBuilder text_sb = new StringBuilder(text);
        if(text_sb.charAt(0) == ('\'')){
            text_sb = text_sb.deleteCharAt(0);
        }
        if(text_sb.charAt(text_sb.length() - 1) == ('\'')){
            text_sb = text_sb.deleteCharAt(text_sb.length() - 1);
        }

        return text_sb.toString();
    }

    private void createRegisters(){
        ContentValues contentValues = new ContentValues();
        contentValues.put(PARAMS_COLUMN_LOCATION_FREQUENCY, this.frequency);
        contentValues.put(PARAMS_COLUMN_PERSONAL_MESSAGE, this.message);
        contentValues.put(PARAMS_COLUMN_TIME_WARN, this.time_warn);

        DBHelper db = new DBHelper(this.context);
        int id =db.insert(this.PARAMS_TABLE_NAME, contentValues);
    }

}

