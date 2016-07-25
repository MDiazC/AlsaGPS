package com.mdc.elementary.alsagps;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.util.Log;

/**
 * Created by elementary on 24/07/16.
 */
public class AlertSystemService  extends Service implements CustomCallback {

    public static final int STATUS_RUNNING = 0;
    public static final int STATUS_FINISHED = 1;
    public static final int STATUS_ERROR = 2;

    private static final String TAG = "AlertSystemService";

    ThreadTrackCoordinates threadGPS = null;
    ThreadAutomaticSMS threadSMS = null;

    Bundle bundle;
    Intent intent ;
    AlertSystemService that;


    public AlertSystemService() {
        Bundle bundle = new Bundle();
    }

    @Override
    public int onStartCommand(Intent intnt, int flags, int startId) {

        Log.i(TAG, "Service onStartCommand");

        this.intent=intnt;
        this.that = this;

        //Creating new thread for my service
        //Always write your long running tasks in a separate thread, to avoid ANR
        new Thread(new Runnable() {
            @Override
            public void run() {

                Log.d(TAG, "Service Started!");

                bundle = new Bundle();

                GPSSystem gps = new GPSSystem(that);
                if(gps.canGetLocation()){
                    Log.e("CREATE", "activateApp");
                    threadGPS = new ThreadTrackCoordinates(that, gps);
                    threadGPS.callback = that;
                    threadGPS.run();
                    threadSMS = new ThreadAutomaticSMS(that);
                    threadSMS.callback = that;
                    threadSMS.run();
                }else {
                    final ResultReceiver receiver = intent.getParcelableExtra("receiver");
                    bundle.putString("GPS", "GPS is not enabled. Do you want to go to settings menu?");
                    receiver.send(STATUS_ERROR, bundle);
                }
                try {
                    threadGPS.join();
                }catch (Exception e){
                    errorInService("Track Thread", "Its was a internal problem with the app :-S");
                }
                Log.e("CREATE","end of service");
            }
        }).start();

        return Service.START_STICKY;
    }

    public void serviceFinished(boolean result){
        Log.e("CREATE","endi of ");
        final ResultReceiver receiver = this.intent.getParcelableExtra("receiver");
        bundle.putString(Intent.EXTRA_KEY_EVENT, (Boolean.TRUE.equals(result))?"true":"false");
        receiver.send(STATUS_FINISHED, bundle);
        this.stopThreads();
        this.stopSelf();
    }

    public void errorInService(String key, String error){
        Log.e("CREATE", "deactivateApp");
        this.stopThreads();

        final ResultReceiver receiver = intent.getParcelableExtra("receiver");
        bundle.putString(key, error);
        receiver.send(STATUS_ERROR, bundle);

        this.stopSelf();
    }

    private void stopThreads() {
        if(this.threadGPS != null) {
            this.threadGPS.stopThread();
            this.threadGPS=null;
        }
        if(this.threadSMS != null) {
            this.threadSMS.stopThread();
            this.threadSMS = null;
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        Log.i(TAG, "Service onBind");
        return null;
    }

    @Override
    public void onDestroy(){
        this.stopThreads();
    }
    @Override
    public void updateCoordinates(double latitude, double longitude){}
}

@SuppressLint("ParcelCreator")
class AlertSystemReceiver extends ResultReceiver {
    private Receiver mReceiver;

    public AlertSystemReceiver(Handler handler) {
        super(handler);
    }

    public void setReceiver(Receiver receiver) {
        mReceiver = receiver;
    }

    public interface Receiver {
        public void onReceiveResult(int resultCode, Bundle resultData);
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (mReceiver != null) {
            mReceiver.onReceiveResult(resultCode, resultData);
        }
    }
}