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
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.HashMap;

public class AgendaContactsList {

    private HashMap contacts_list = null;
    private static final String CONTACTS_TABLE_NAME = "agenda_contacts";
    private static final String CONTACTS_COLUMN_ID = "id";
    private static final String CONTACTS_COLUMN_NAME = "name";
    private static final String CONTACTS_COLUMN_PHONE = "phone";
    private Context context = null;

    public AgendaContactsList(Context context){
        this.contacts_list = new HashMap<String,String>();
        this.context=context;
    }

    public static String getCreateTable(){
        return "CREATE TABLE IF NOT EXISTS " + CONTACTS_TABLE_NAME + "("+CONTACTS_COLUMN_ID+
                " INTEGER PRIMARY KEY, "+CONTACTS_COLUMN_NAME+" TEXT,"+CONTACTS_COLUMN_PHONE+" TEXT);" +
                "ALTER TABLE" + CONTACTS_TABLE_NAME + " ADD UNIQUE INDEX ("+CONTACTS_COLUMN_NAME+","+CONTACTS_COLUMN_PHONE+")";
    }
    public static String getDeleteTable(){
        return "DROP TABLE IF EXISTS "+ CONTACTS_TABLE_NAME;
    }

    public boolean isAgendaContactListEmpty(){
        return this.contacts_list == null || this.contacts_list != null && this.contacts_list.isEmpty();
    }

    public int insertContact (String name, String phone)
    {
        ContentValues contentValues = new ContentValues();
        name = this.escapeString(name);
        phone = this.escapeString(phone);
        contentValues.put(CONTACTS_COLUMN_NAME, name);
        contentValues.put(CONTACTS_COLUMN_PHONE, phone);

        DBHelper db = new DBHelper(this.context);
        int id =db.insert(this.CONTACTS_TABLE_NAME, contentValues);
        return id;
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

    public HashMap getAllAgendaContacts()
    {
        Log.e("CREATION", "get all agenda contacts");
        if(isAgendaContactListEmpty()){
            this.loadAllAgendaContacts();
        }
        Log.e("CREATION", "end get all agenda contacts");
        return this.contacts_list;
    }

    public void loadAllAgendaContacts()
    {
        //hp = new HashMap();
        DBHelper dbh = new DBHelper(this.context);
        String selectQuery =  "SELECT  * FROM " + CONTACTS_TABLE_NAME + " ORDER BY "+CONTACTS_COLUMN_NAME+" ASC";
        SQLiteDatabase db =dbh.get(selectQuery);

        this.contacts_list = new HashMap<String, String>();
        try {
            Cursor cursor = db.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    this.contacts_list.put(cursor.getString(cursor.getColumnIndex(CONTACTS_COLUMN_NAME)), cursor.getString(cursor.getColumnIndex(CONTACTS_COLUMN_PHONE)));
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }
}
