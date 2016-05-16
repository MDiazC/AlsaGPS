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
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

public class ContactList{

    private ArrayList contacts_list = null;
    private static final String CONTACTS_TABLE_NAME = "contacts";
    private static final String CONTACTS_COLUMN_ID = "id";
    private static final String CONTACTS_COLUMN_NAME = "name";
    private static final String CONTACTS_COLUMN_PHONE = "phone";
    private Context context = null;

    public ContactList(Context context){
        this.contacts_list = new ArrayList<>();
        this.context=context;
    }

    public static String getCreateTable(){
        return "CREATE TABLE IF NOT EXISTS " + CONTACTS_TABLE_NAME + "("+CONTACTS_COLUMN_ID+
                " INTEGER PRIMARY KEY, "+CONTACTS_COLUMN_NAME+" TEXT,"+CONTACTS_COLUMN_PHONE+" INTEGER)";
    }
    public static String getDeleteTable(){
        return "DROP TABLE IF EXISTS "+ CONTACTS_TABLE_NAME;
    }

    public Boolean isContactListEmpty(){
        return this.contacts_list == null || this.contacts_list != null && this.contacts_list.isEmpty();
    }

    public int insertContact  (String name, Integer phone)
    {
        ContentValues contentValues = new ContentValues();
        name = DatabaseUtils.sqlEscapeString(name);
        contentValues.put(CONTACTS_COLUMN_NAME, name);
        contentValues.put(CONTACTS_COLUMN_PHONE, phone);

        DBHelper db = new DBHelper(this.context);
        int id =db.insert(this.CONTACTS_TABLE_NAME, contentValues);
        return id;
    }


    public void deleteContact (int id)
    {
        DBHelper db = new DBHelper(this.context);
        String whereClause=" WHERE id = ?";
        String[] whereArgs = new String[] { String.valueOf(id) };
        db.delete(this.CONTACTS_TABLE_NAME, whereClause, whereArgs);
        /*
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(CONTACTS_TABLE_NAME,
                "id = ? ",
                new String[] { Integer.toString(id) });
                */
    }

    public ArrayList getAllContacts()
    {
        Log.e("CREATION", "get all contacts");
        if(isContactListEmpty()){
            this.loadAllContacts();
        }
        Log.e("CREATION", "end get all contacts");
        return this.contacts_list;
    }


    public void loadAllContacts()
    {
        //hp = new HashMap();
        DBHelper db = new DBHelper(this.context);
        String selectQuery =  "SELECT  * FROM " + this.CONTACTS_TABLE_NAME;
        this.contacts_list =db.get(selectQuery);
        //SQLiteDatabase db = this.getReadableDatabase();
        /*Cursor res =  db.get( this.CONTACTS_TABLE_NAME, null, null, null, null, null, null);
        res.moveToFirst();

        while(!res.isAfterLast()){
            array_list.add(res.getString(res.getColumnIndex(CONTACTS_COLUMN_NAME)));
            res.moveToNext();
        }*/
    }

}
