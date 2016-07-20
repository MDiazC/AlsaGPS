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

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AddContactActivity  extends Activity {

    private HashMap agenda_contacts = null;
    private ArrayAdapter<Contact> arrayAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.add_contact_screen);

        this.activeAllFeatures();

        if(this.agenda_contacts == null){
            this.agenda_contacts = this.getAgendaContacts();
        }

        fillListView();
    }

    private void fillListView() {
        ListView lv;

        List<Contact> array_list = getListContactsAgenda();

        lv = (ListView) findViewById(R.id.list_view_add_contact);

        if(array_list != null && lv!= null) {
            this.arrayAdapter = new ArrayAdapter<Contact>(
                    this,
                    R.layout.list_contacts_add, R.id.contact_name,
                    array_list);

            lv.setAdapter(this.arrayAdapter);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Contact selectd_contact = (Contact) parent.getAdapter().getItem(position);
                    saveContact(selectd_contact.getName(), selectd_contact.getNumber());
                    updateListView(selectd_contact);
                }
            });
        }
    }

    public void btnAddContact(View v){
        ListView lv = (ListView) findViewById(R.id.list_view_add_contact);
        final int position = lv.getPositionForView((View) v.getParent());
        Contact selectd_contact = (Contact) lv.getAdapter().getItem(position);
        saveContact(selectd_contact.getName(), selectd_contact.getNumber());
        updateListView(selectd_contact);

    }

    private void updateListView(Contact contact){

        arrayAdapter.remove(contact);
        //arrayAdapter.notifyDataSetChanged();
    }

    private List<Contact> getListContactsAgenda() {

        List<Contact> array_list = new ArrayList<Contact>();
        String nameContact =null;
        String numberContact= null;

        Iterator it = this.agenda_contacts.entrySet().iterator();
        ContactList cl = new ContactList(this);
        HashMap contact_list = cl.getAllContacts();

        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();

            try {
                numberContact = (String) pair.getValue();
                nameContact = (String)pair.getKey();
            }catch (ClassCastException e){
                e.printStackTrace();
            }
            array_list = addItemToList(array_list, nameContact, numberContact, contact_list);

            it.remove();
        }

        Collections.sort(array_list, new Comparator<Contact>(){
            public int compare(Contact o1, Contact o2){
                return o1.getName().compareTo(o2.getName());
            }
        });
        return array_list;
    }

    private boolean isAlreadyInserted(String name, String number, HashMap contact_list){

        return (contact_list.get(name) != null && contact_list.get(name).equals(number)) || (contact_list.get(name + " [ " + number + " ]") != null && contact_list.get(name + " [ " + number + " ]").equals(number));
    }

    private List<Contact> addItemToList(List<Contact> array_list, String nameContact, String numberContact, HashMap cl){
        Contact contact;
        if(!isAlreadyInserted(nameContact, numberContact, cl)){
            contact = new Contact();
            contact.setName(nameContact);
            contact.setNumber(nameContact);
            array_list.add(contact);
        }
        return array_list;
    }

    private void deactivateAllFeatures(){
        LinearLayout lyt_back_button = (LinearLayout) findViewById(R.id.bottom_bar_back);
        LinearLayout lyt_about = (LinearLayout) findViewById(R.id.bottom_bar_about);
        LinearLayout lyt_add_contact = (LinearLayout) findViewById(R.id.button_add_contact_manually);

        lyt_add_contact.setOnClickListener(null);
        lyt_back_button.setOnClickListener(null);
        lyt_about.setOnClickListener(null);
    }

    private void activeAllFeatures(){
        LinearLayout lyt_back_button = (LinearLayout) findViewById(R.id.bottom_bar_back);
        LinearLayout lyt_about = (LinearLayout) findViewById(R.id.bottom_bar_about);
        LinearLayout lyt_add_contact = (LinearLayout) findViewById(R.id.button_add_contact_manually);


        LinearLayout btn_cancel = (LinearLayout) findViewById(R.id.options_button_cancel);
        LinearLayout btn_save = (LinearLayout) findViewById(R.id.options_button_save);


        lyt_add_contact.setOnClickListener(initialScreenHandler);
        lyt_back_button.setOnClickListener(initialScreenHandler);
        lyt_about.setOnClickListener(initialScreenHandler);


        btn_save.setOnClickListener(initialScreenHandler);
        btn_cancel.setOnClickListener(initialScreenHandler);
    }

    private void addContact(){
        EditText edtTxt_name = (EditText) findViewById(R.id.layout_input_name);
        EditText edtTxt_number = (EditText) findViewById(R.id.add_contact_input);
        String name = edtTxt_name.getText().toString();
        String number = edtTxt_number.getText().toString();
        if(!name.equals("") && !number.equals("")){
            if(this.isNumber(number)) {
                this.saveContact(name, number);
            }
        }
    }

    private void saveContact(String contactName, String contactNumber){
        agenda_contacts.remove(contactName);
        ContactList cl = new ContactList(this);
        cl.insertContact(contactName, contactNumber);
        showConfirmationMessage();
    }

    private void showConfirmationMessage(){
        //layout_layout_contact_added
        LinearLayout lyt_contact_added = (LinearLayout) findViewById(R.id.layout_layout_contact_added);

        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(lyt_contact_added, "alpha",  1f, 0f);
        fadeOut.setDuration(3000);
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(lyt_contact_added, "alpha", 0f, 1f);
        fadeIn.setDuration(500);

        final AnimatorSet mAnimationSet = new AnimatorSet();
        mAnimationSet.play(fadeOut).after(fadeIn);
        mAnimationSet.start();
    }


    private Boolean isNumber(String number){
        String number_2 = number.replaceAll("[^\\d]", "");
        Integer val = Integer.valueOf(number_2);
        return val != null;
    }

    private void showLayoutOptions(){
        LinearLayout lyt_add_contact_bottom = (LinearLayout) findViewById(R.id.layout_layout_add_contact_bottom);
        lyt_add_contact_bottom.setVisibility(View.VISIBLE);
        lyt_add_contact_bottom.setOnFocusChangeListener(focusChangeListener);
    }


    private void hideLayoutOptions(){
        LinearLayout lyt_add_contact_bottom = (LinearLayout) findViewById(R.id.layout_layout_add_contact_bottom);
        lyt_add_contact_bottom.setOnFocusChangeListener(null);
        lyt_add_contact_bottom.setVisibility(View.GONE);
    }

    private void hideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    private HashMap getAgendaContacts(){
        HashMap contact ;
        AgendaContactsList acl = new AgendaContactsList(this);
        contact= acl.getAllAgendaContacts();

        return contact;
    }

    View.OnFocusChangeListener focusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus && v.getVisibility() == View.VISIBLE) {
                hideLayoutOptions();
                activeAllFeatures();
                hideKeyboard(v);

            }
        }
    };

    View.OnClickListener initialScreenHandler = new View.OnClickListener(){

        public void onClick(View v) {

            switch(v.getId()) {
                case R.id.bottom_bar_back:
                    onBackPressed();
                    //Intent intentMainContact = new Intent(AddContactActivity.this ,ContactsScreenActivity.class);
                    //AddContactActivity.this.startActivity(intentMainContact);
                    break;
                case R.id.bottom_bar_about:
                    Intent intentMainAbout = new Intent(AddContactActivity.this ,AboutScreenActivity.class);
                    AddContactActivity.this.startActivity(intentMainAbout);
                    break;
                case R.id.button_add_contact_manually:
                    showLayoutOptions();
                    deactivateAllFeatures();
                    break;
                case R.id.options_button_cancel:
                    hideLayoutOptions();
                    activeAllFeatures();
                    hideKeyboard(v);
                    break;
                case R.id.options_button_save:
                    hideLayoutOptions();
                    activeAllFeatures();
                    addContact();
                    hideKeyboard(v);
                    break;
            }
        }
    };
}


