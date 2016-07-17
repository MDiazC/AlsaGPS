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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.telephony.SmsManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SMSSystem {

    Context context;
    String default_message="";

    public SMSSystem(Context cntxt) {
        context=cntxt;
    }

    public void sendSMS(){
        boolean result=false;
        InternalParams ip = new InternalParams(this.context);
        ip.loadParams();

        ContactList contact_list = new ContactList(this.context);
        HashMap cl = contact_list.getAllContacts();

        String message = this.default_message+" - "+ip.getPersonalMessage();
        message = message.substring(0, Math.min(message.length(), 10));

        try {
            result = collectInfoAndSend(cl, message);
        }catch (Exception e){
            this.showCoordsErrorAlert();
        }

        if(result){
            this.showSuccessAlert();
        }else {
            this.showProblemSendAlert();
        }

    }

    private boolean collectInfoAndSend(HashMap cl, String SMSmessage) throws Exception{
        String contactNumber="";
        boolean result = true;
        Iterator it = cl.entrySet().iterator();

        double[] coordinates = this.getCoordinates();
        if(coordinates == null){
            throw new Exception("Cannot get the coordinates, we couldn't send the SMS");
        }

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
//                    smsManager.sendTextMessage(contactNumber, null, SMSmessage, null, null);
                    System.out.println("SMS sent. \n");
                    System.out.println("SMS number:"+contactNumber+" SMS message:'"+SMSmessage+"'\n");
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
            lat = gpsP.getCurrentLatitude();
            lon = gpsP.getCurrentLongitude();
        }

        if(lat != 0.0 && lon != 0.0){
            coordinates = new double[2];
            coordinates[0]=lat;
            coordinates[1]=lon;
        }

        return coordinates;
    }

    public void sendAutomaticSMS(){
        double [] coordinates = this.getCoordinates();
        StartingPoints sp = new StartingPoints(this.context);
        sp.loadStartingPoints();
        HashMap points = sp.getStartingPoints();

        boolean matching = this.matchingPositionWithSP(points, coordinates);
        if(!matching){
            this.sendSMS();
        }
    }

    private boolean matchingPositionWithSP(HashMap points, double[] coordinates){
        boolean matching = false;
        boolean success;
        Iterator it = points.entrySet().iterator();

        ArrayList<Double> spCoordinates;
        double spLat, spLon;

        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();

            try {
                spCoordinates = (ArrayList)pair.getValue();

                spLat=(Double) spCoordinates.get(0);
                Log.e("CREATE", "matchingPositionWithSP get lat "+spLat);
                spLon=(Double) spCoordinates.get(1);
                Log.e("CREATE", "matchingPositionWithSP get lon "+spLon);
                success=this.compareCoordinates(coordinates[0], coordinates[1], spLat, spLon);
                if(success){
                    matching=true;
                    break;
                }

            }catch (ClassCastException e){
                e.printStackTrace();
            }

            it.remove();
        }
        return matching;
    }

    private boolean compareCoordinates(double currentLat, double currentLon, double spLat, double spLon){
        boolean equal = false;
        double distance = 0.001;
        Log.e("CREATE", "culat "+currentLat+" cuLon "+currentLon+" spLa "+spLat+" spLo "+spLon);
        if(currentLat + distance > spLat && currentLat - distance < spLat){
            Log.e("CREATE", "Match lat");
            if(currentLon + distance > spLon && currentLon - distance < spLon) {
                Log.e("CREATE", "Match lon");
                equal = true;
            }
        }
        return equal;
    }

    private void showSettingsAlert(String msg){

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this.context);

        // Setting Dialog Title
        alertDialog.setTitle("SMS system ");

        // Setting Dialog Message
        alertDialog.setMessage(msg);

        // On pressing Settings button
        alertDialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    private void showCoordsErrorAlert(){
        this.showSettingsAlert("We couldn't gather the coordinates, que couldn't send the SMS");
    }

    private void showProblemSendAlert(){
        this.showSettingsAlert("We couldn't send the SMS to some of the contacts");
    }

    private void showSuccessAlert(){
        this.showSettingsAlert("SMS messages sent!");
    }
}
