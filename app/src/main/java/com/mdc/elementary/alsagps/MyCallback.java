package com.mdc.elementary.alsagps;

/**
 * Created by elementary on 16/07/16.
 */
public interface MyCallback {
    void updateCoordinates(double latitude, double longitude );
    void stopApp();
}
