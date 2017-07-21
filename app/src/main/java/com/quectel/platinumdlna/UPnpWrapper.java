package com.quectel.platinumdlna;

import android.util.Log;

import com.plutinosoft.platinum.UPnP;

import java.util.ArrayList;

/**
 * Created by klein on 17-7-21.
 */

public class UPnpWrapper extends UPnP {

    public static final String TAG = "PlatinumDLNA.UW";

    UPnP uPnP;

    public UPnpWrapper(){

        uPnP = new UPnP();
    }

    public int start() {
        return uPnP.start();
    }

    public int stop() {
        return uPnP.stop();
    }

    public String setms() {
        return uPnP.setms();
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

    public void onDmsAdded(String device){
        Log.d(TAG, "message from JNI native,device = " + device);
    }

}
