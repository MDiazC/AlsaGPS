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

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.HashMap;

public class ThreadTrackCoordinates extends Thread implements Parcelable {
    Context context;
    GPSSystem gps;
    boolean first_time;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    public ThreadTrackCoordinates(Context cntxt, GPSSystem gps) {
        this.gps = gps;
        this.context = cntxt;
        this.first_time=true;
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
    }

    public void run() {
        try {
            //Thread.sleep(3000);
            Log.e("CREATE", "Run GPS");
            if(!this.first_time) {
                this.gps.getLocation();
                double latitude = this.gps.getLatitude();
                double longitude = this.gps.getLongitude();

                GPSPartial gpsP = new GPSPartial(this.context);
                gpsP.loadLastPosition();
                double prevLat = gpsP.getLastPartialLatitude();
                double prevLon = gpsP.getLastPartialLongitude();

                if(!this.sameCoordinates(latitude, longitude, prevLat, prevLon)){

                    this.savePartialPosition(latitude, longitude);

                    boolean matching = this.comparePositionWithStartingPoints(latitude, longitude);
                    if (!matching) {
                        InternalParams ip = new InternalParams(this.context);
                        ip.loadParams();
                        mHandler.postDelayed(this, ip.getFrequency() * 60 *1000);
                    }
                }else {
                    InternalParams ip = new InternalParams(this.context);
                    ip.loadParams();
                    mHandler.postDelayed(this, ip.getFrequency() * 60 *1000);
                }
            }else {
                this.first_time=false;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private boolean comparePositionWithStartingPoints(double latitude, double longitude) {
        double [] coordinates = {latitude,longitude};
        StartingPoints sp = new StartingPoints(this.context);
        return sp.matchingWithPosition(coordinates);
    }

    private void savePartialPosition(double latitude, double longitude) {
        GPSPartial gps_partial = new GPSPartial(this.context);
        gps_partial.insertPosition(latitude,longitude);
        Log.e("CREATE", "Your Location is - \nLat: " + latitude + "\nLong: " + longitude);
    }

    public void stopThread(){
        Log.e("CREATE", "Stop GPS");
        GPSPartial gps_partial = new GPSPartial(this.context);
        gps_partial.deleteOldPositions();
        this.gps.stopUsingGPS();
        this.gps=null;
        mHandler.removeCallbacks(this);
        mHandler.removeCallbacksAndMessages(null);
    }

    private boolean sameCoordinates(double currentLat, double currentLon, double prevLat, double prevLon){
        boolean equal = false;
        double distance = 0.001;
        Log.e("CREATE", "culat "+currentLat+" cuLon "+currentLon+" spLa "+prevLat+" spLo "+prevLon);
        if(currentLat + distance > prevLat && currentLat - distance < prevLat){
            Log.e("CREATE", "Match lat");
            if(currentLon + distance > prevLon && currentLon - distance < prevLon) {
                Log.e("CREATE", "Match lon");
                equal = true;
            }
        }
        return equal;
    }

    // Parcelable functions

    private int mData;

    public int describeContents() {
        return 0;
    }

    /** save object in parcel */
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(mData);
    }

    public static final Parcelable.Creator<ThreadTrackCoordinates> CREATOR
            = new Parcelable.Creator<ThreadTrackCoordinates>() {
        public ThreadTrackCoordinates createFromParcel(Parcel in) {
            return new ThreadTrackCoordinates(in);
        }

        public ThreadTrackCoordinates[] newArray(int size) {
            return new ThreadTrackCoordinates[size];
        }
    };

    /** recreate object from parcel */
    private ThreadTrackCoordinates(Parcel in) {
        mData = in.readInt();
    }
}

