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

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

public class ThreadUpdateCoordsSP extends Thread {
    CustomCallback callback;
    GPSSystem gps;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    public ThreadUpdateCoordsSP(GPSSystem gps) {
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
        this.gps = gps;
        Log.e("CREATE", "Contruct");
    }

    public void stopThread(){
        Log.e("CREATE", "Stop GPS SP");
        this.gps.stopUsingGPS();
        this.gps=null;
        mHandler.removeCallbacks(this);
        mHandler.removeCallbacksAndMessages(null);
        mHandler.getLooper().quit();
    }

    public void run() {
        try {
            this.gps.getLocation();
            double latitude = this.gps.getLatitude();
            double longitude = this.gps.getLongitude();
            Log.e("CREATE", "run get starting point "+latitude);
            if(latitude != 0 && longitude != 0){
                Log.e("CREATE", "coords get ");
                callback.updateCoordinates(latitude, longitude);
            }else{
                mHandler.postDelayed(this, 2000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

