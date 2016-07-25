package com.mdc.elementary.alsagps;

/**
 * Created by elementary on 16/07/16.
 */
public interface CustomCallback {
    void updateCoordinates(double latitude, double longitude );
    void errorInService(String key,String error);
    void serviceFinished(boolean result);
}
