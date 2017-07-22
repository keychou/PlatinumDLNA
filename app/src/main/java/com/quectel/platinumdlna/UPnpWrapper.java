package com.quectel.platinumdlna;

import android.util.Log;

import com.plutinosoft.platinum.UPnP;

import java.util.ArrayList;

/**
 * Created by klein on 17-7-21.
 */

public class UPnpWrapper extends UPnP implements UPnP.DeviceStatusChangeListener {

    public static final String TAG = "PlatinumDLNA.UW";

    UPnP uPnP;

    public UPnpWrapper(){

        uPnP = new UPnP();
        uPnP.setDeviceStatusChangeListener(this);
    }

    public int start() {
        return uPnP.start();
    }

    public int stop() {
        return uPnP.stop();
    }

    public int setActiveDms(String device){
        return uPnP.setActiveDms(device);
    }

    public ArrayList getDmsList() {
        return uPnP.getDmsList();
    }

    public ArrayList getDmrList() {
        return uPnP.getDmrList();
    }

    public boolean checkVersion(String[] version) {
        return uPnP.checkVersion(version);
    }


    @Override
    public void onDmsAdded(String device){
        Log.d(TAG, "dms add,device = " + device);
    }

    @Override
    public void onDmrAdded(String device){
        Log.d(TAG, "dmr add,device = " + device);
    }

}
