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

import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.RemoteException;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ImportAgendaContacts {
    Context context = null;

    public ImportAgendaContacts(Context context){
        this.context=context;
    }

    public boolean contactsImported(){
        AgendaContactsList acl = new AgendaContactsList(this.context);
        acl.loadAllAgendaContacts();
        return !acl.isAgendaContactListEmpty();
    }

    private HashMap<String, ArrayList<String>> fetchContactsCProviderClient()
    {
        HashMap<String, ArrayList<String>> mContactList = null;

        ContentResolver cResolver=this.context.getContentResolver();
        ContentProviderClient mCProviderClient = cResolver.acquireContentProviderClient(ContactsContract.Contacts.CONTENT_URI);
        if(mCProviderClient != null) {
            try {
                String[] projection    = new String[] {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER};
                Cursor mCursor = mCProviderClient.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, null, null, null);
                if (mCursor != null && mCursor.getCount() > 0) {
                    mContactList = this.gatherInfoInArrayList(mCursor);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
                mContactList = null;
            } catch (Exception e) {
                e.printStackTrace();
                mContactList = null;
            }

            mCProviderClient.release();
        }

        return mContactList;
    }

    private HashMap gatherInfoInArrayList(Cursor mCursor) {
        String displayName;
        String number;
        HashMap<String, ArrayList<String>> mContactList = new HashMap<String,ArrayList<String>>();;
        mCursor.moveToFirst();
        do {

            displayName = mCursor.getString(mCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            number = mCursor.getString(mCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            ArrayList<String>  listNumbers= new ArrayList<String>();

            if(mContactList.get(displayName) != null){
                listNumbers = mContactList.get(displayName);
            }

            listNumbers.add(number);
            mContactList.put(displayName, listNumbers);
        }while(mCursor.moveToNext());

        mCursor.close();
        return mContactList;
    }

    private void getListContactsAgenda(HashMap<String,ArrayList<String>> agenda_contacts) {

        String nameContact =null;
        ArrayList<String> listNumbers= null;

        Iterator it = agenda_contacts.entrySet().iterator();

        AgendaContactsList acl = new AgendaContactsList(this.context);

        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();

            try {
                listNumbers = (ArrayList<String>) pair.getValue();
                nameContact = (String)pair.getKey();
                this.addContactToDB(listNumbers, nameContact, acl);
            }catch (ClassCastException e){
                e.printStackTrace();
            }
            it.remove();
        }
    }

    public void fetchAgendaContacts(){
        HashMap<String,ArrayList<String>> agenda_contacts = this.fetchContactsCProviderClient();
        this.getListContactsAgenda(agenda_contacts);
    }

    private void addContactToDB( ArrayList<String> listNumbers,  String nameContact, AgendaContactsList acl) {
        Contact contact = null;
        if(listNumbers != null){
            if(listNumbers.size() > 1) {
                HashMap contacts_list = new HashMap<String, String>();
                for (int i = 0; i < listNumbers.size(); i++) {
                    contacts_list.put(listNumbers.get(i).replaceAll("[^\\d+]", ""), nameContact);
                }

                Iterator it = contacts_list.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry)it.next();

                    try {
                        acl.insertContact((String) pair.getValue(), (String) pair.getKey());
                    }catch (ClassCastException e){
                        e.printStackTrace();
                    }
                }

            }else{
                acl.insertContact(nameContact, listNumbers.get(0));
            }
        }
    }
}
