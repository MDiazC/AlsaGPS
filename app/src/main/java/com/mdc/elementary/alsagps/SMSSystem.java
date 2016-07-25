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

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.ContextThemeWrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SMSSystem {

    Context context;
    String default_header_message="There is a problem with: ";
    String default_body_message=". The position is: ";

    public SMSSystem(Context cntxt) {
        context=cntxt;
    }

    public boolean sendSMS() throws Exception{
        boolean result=false;

        ContactList contact_list = new ContactList(this.context);
        HashMap cl = contact_list.getAllContacts();

        try {
            String message = this.buildMessage();
            result = this.collectInfoAndSend(cl, message);
            if(!result){
                throw new Exception("We couldn't send the SMS to some of the contacts");
            }
        }catch (Exception e){
            e.printStackTrace();
            throw new Exception("We couldn't gather the coordinates, we couldn't send the SMS");
        }
        return result;
    }

    private String buildMessage() throws Exception{
        double[] coordinates = this.getCoordinates();
        if(coordinates == null){
            throw new Exception("Cannot get the coordinates, we couldn't send the SMS");
        }

        InternalParams ip = new InternalParams(this.context);
        ip.loadParams();

        String owner = this.getOwnerInfo();
        String googleMaps = this.setGoogleMapsDir(coordinates);

        String message = this.default_header_message+owner+this.default_body_message+googleMaps +" - "+ip.getPersonalMessage();
        message = message.substring(0, Math.min(message.length(), 159));

        return message;
    }

    private String setGoogleMapsDir(double[] coordinates){
        double lat = Math.floor(coordinates[0] * 1000) / 1000;
        double lon = Math.floor(coordinates[1] * 1000) / 1000;
        return "https://www.google.com/maps/@"+lat+","+lon+",16z";
    }

    private String getOwnerInfo() {
        TelephonyManager tMgr = (TelephonyManager)this.context.getSystemService(Context.TELEPHONY_SERVICE);
        String owner_info = tMgr.getLine1Number();

        if(owner_info.isEmpty()) {
            AccountManager manager = AccountManager.get(this.context);
            Account[] accounts = manager.getAccountsByType("com.google");
            List<String> possibleEmails = new LinkedList<String>();

            for (Account account : accounts) {
                possibleEmails.add(account.name);
            }

            if (!possibleEmails.isEmpty() && possibleEmails.get(0) != null) {
                owner_info = possibleEmails.get(0);
                String[] parts = owner_info.split("@");
                if (parts.length > 0 && parts[0] != null)
                    owner_info= parts[0];
            }
        }
        return owner_info;
    }

    private boolean collectInfoAndSend(HashMap cl, String SMSmessage){
        String contactNumber="";
        boolean result = true;
        Iterator it = cl.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();

            try {
                contactNumber = (String)pair.getValue();
            }catch (ClassCastException e){
                e.printStackTrace();
                result=false;
                it.remove();
                continue;
            }

            try {
                if(!contactNumber.isEmpty()) {
                    SmsManager smsManager = SmsManager.getDefault();
                    System.out.println("SMS sent. \n");
                    Log.e("CREATE","SMS number:"+contactNumber+" SMS message:'"+SMSmessage+"'\n");
                    smsManager.sendTextMessage(contactNumber, null, SMSmessage, null, null);

                }
            }
            catch (Exception e) {
                e.printStackTrace();
                result=false;
            }
            it.remove();
        }
        return result;
    }

    private double[] getCoordinates(){
        double[] coordinates = null;

        double lat, lon;
        GPSSystem gpsS = new GPSSystem(this.context);
        lat = gpsS.getLatitude();
        lon = gpsS.getLongitude();
        gpsS.stopUsingGPS();
        gpsS=null;
        if(lat == 0.0 && lon == 0.0){
            GPSPartial gpsP = new GPSPartial(this.context);
            gpsP.loadLastPosition();
            lat = gpsP.getLastPartialLatitude();
            lon = gpsP.getLastPartialLongitude();
        }

        if(lat != 0.0 && lon != 0.0){
            coordinates = new double[2];
            coordinates[0]=lat;
            coordinates[1]=lon;
        }

        return coordinates;
    }

    public boolean sendAutomaticSMS() throws Exception{
        boolean result=false;
        double [] coordinates = this.getCoordinates();
        StartingPoints sp = new StartingPoints(this.context);

        boolean matching = sp.matchingWithPosition(coordinates);
        if(!matching){
            result=this.sendSMS();
        }
        return result;
    }

}
