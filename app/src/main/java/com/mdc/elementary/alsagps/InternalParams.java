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
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class InternalParams{
    private Integer time_warn = 0;
    private Integer frequency = 0;
    private String message = null;

    private final String PARAMS_TABLE_NAME = "internal_params";
    private final String PARAMS_COLUMN_ID = "id";
    private final String PARAMS_COLUMN_PERSONAL_MESSAGE = "personal_message";
    private final String PARAMS_COLUMN_LOCATION_FREQUENCY = "location_frequency";
    private final String PARAMS_COLUMN_TIME_WARN = "time_warn";
    private Context context = null;
    private String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + PARAMS_TABLE_NAME + "("+PARAMS_COLUMN_ID+
            " INTEGER PRIMARY KEY, "+PARAMS_COLUMN_PERSONAL_MESSAGE+" TEXT, "+PARAMS_COLUMN_LOCATION_FREQUENCY+" INTEGER, "+PARAMS_COLUMN_TIME_WARN+" INTEGER)";
    private String SQL_DELETE_TABLE = "DROP TABLE IF EXISTS "+ PARAMS_TABLE_NAME;

    public InternalParams(Context context){
        time_warn = 6;
        frequency = 10;
        message = "";
        this.context=context;
    }

    public Integer getTimeWarn(){
        return this.time_warn;
    }

    public Integer getFrequency(){
        return this.frequency;
    }

    public String getPersonalMessage(){
        return this.message;
    }

    public void setTimeWarn(Integer new_time){
        if(new_time > 0) {
            this.time_warn = new_time;
            this.updateParams();
            //@todo Si hay excepción hay que setear el valor anterior
        }
    }

    public void setFrequency(Integer new_frequency){
        if(new_frequency > 0) {
            this.frequency = new_frequency;
            this.updateParams();
            //@todo Si hay excepción hay que setear el valor anterior
        }
    }

    public void setPersonalMessage(String new_message){
        if(new_message != null && !new_message.isEmpty()){
            this.message=new_message;
            this.updateParams();
            //@todo Si hay excepción hay que setear el valor anterior
        }
    }

    public ArrayList loadParams(){
        DBHelper db = new DBHelper(this.context, this.SQL_CREATE_TABLE, this.SQL_DELETE_TABLE);
        String selectQuery =  "SELECT  * FROM " + this.PARAMS_TABLE_NAME;
        ArrayList array_list =db.get(selectQuery);
        return array_list;
    }

    private boolean updateParams ()
    {
        DBHelper db = new DBHelper(this.context, this.SQL_CREATE_TABLE, this.SQL_DELETE_TABLE);

        ContentValues contentValues = new ContentValues();
        contentValues.put(PARAMS_COLUMN_LOCATION_FREQUENCY, this.frequency);
        message = DatabaseUtils.sqlEscapeString(message);
        contentValues.put(PARAMS_COLUMN_PERSONAL_MESSAGE, this.message);
        contentValues.put(PARAMS_COLUMN_TIME_WARN, this.time_warn);
        db.update(PARAMS_TABLE_NAME, contentValues, "id = ? ", new String[] { "1" } );
        return true;
    }
}

