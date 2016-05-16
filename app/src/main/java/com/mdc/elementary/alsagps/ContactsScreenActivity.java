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
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class ContactsScreenActivity extends Activity{

    private ContactList contact_list=null;

    public ContactsScreenActivity(){
        this.contact_list= new ContactList(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.contact_list.loadAllContacts();
        setContentView(R.layout.contacts_screen);

        Boolean contact_list_empty =this.contact_list.isContactListEmpty();

        this.visibilityFirstTimeLayout(contact_list_empty);

        this.activateBottomBar();

        if(!contact_list_empty){
            this.activeAllFeatures();
        }
    }
    private void activeAllFeatures(){
        LinearLayout lyt_add_contact = (LinearLayout) findViewById(R.id.layout_add_contact_button);
        Button btn_remove = (Button) findViewById(R.id.remove_button);

        lyt_add_contact.setOnClickListener(initialScreenHandler);
        btn_remove.setOnClickListener(initialScreenHandler);
    }

    private void activateBottomBar(){
        LinearLayout lyt_back_button = (LinearLayout) findViewById(R.id.bottom_bar_back);
        LinearLayout lyt_about = (LinearLayout) findViewById(R.id.bottom_bar_about);

        lyt_back_button.setOnClickListener(initialScreenHandler);
        lyt_about.setOnClickListener(initialScreenHandler);
    }

    private void removeContact(){

    }

    private void visibilityFirstTimeLayout(Boolean contact_list_empty){
        LinearLayout lyt_contacts_first_time = (LinearLayout) findViewById(R.id.layout_contacts_first_time);
        TextView lyt_add_contact_first_time = (TextView) findViewById(R.id.add_contact_button_first_time);

        lyt_add_contact_first_time.setOnClickListener(initialScreenHandler);

        if(contact_list_empty){
            lyt_contacts_first_time.setVisibility(View.VISIBLE);
        }else{
            lyt_contacts_first_time.setVisibility(View.GONE);
        }
    }

    View.OnClickListener initialScreenHandler = new View.OnClickListener(){

        public void onClick(View v) {

            switch(v.getId()) {
                case R.id.bottom_bar_back:
                    Intent intentMainSettings = new Intent(ContactsScreenActivity.this ,SettingsScreenActivity.class);
                    ContactsScreenActivity.this.startActivity(intentMainSettings);
                    break;
                case R.id.bottom_bar_about:
                    Intent intentMainAbout = new Intent(ContactsScreenActivity.this ,AboutScreenActivity.class);
                    ContactsScreenActivity.this.startActivity(intentMainAbout);
                    break;
                case R.id.layout_add_contact_button:
                case R.id.add_contact_button_first_time:
                    Intent intentMainAddContact = new Intent(ContactsScreenActivity.this ,AddContactActivity.class);
                    ContactsScreenActivity.this.startActivity(intentMainAddContact);
                    break;
                case R.id.remove_button:
                    removeContact();
                    break;
            }
        }
    };
}
