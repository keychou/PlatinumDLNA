package com.plutinosoft.platinum;

import android.util.Log;

import java.util.ArrayList;


public class UPnP {
    public static final String TAG = "PlatinumDLNA";
    public static final String VERSION_OF_PLATITUMDLNA = "1.0.2";

    ArrayList<String> DmsList = new ArrayList();
    ArrayList<String> DmrList = new ArrayList();

    public UPnP() {
        cSelf = _init();
    }

    public int start() {
        return _start(cSelf);
    }
    
    public int stop() {
        return _stop(cSelf);
    }

    public String setms() {
        return _setms();
    }

    public ArrayList<String> getDmsList() {
        return DmsList;
    }

    public ArrayList<String> getDmrList() {
        return DmrList;
    }

    public boolean checkVersion(String[] version) {
        String version_of_sdk = _checkVersion();
        version[0] = version_of_sdk;
        version[1] = VERSION_OF_PLATITUMDLNA;
        if (version[0].equals(VERSION_OF_PLATITUMDLNA)){
            return true;
        }
        return false;
    }

    public void onDmsAdded(String device){
        Log.d(TAG, "dms added,device = " + device);
        DmsList.add(device);
    }

    public void onDmrAdded(String device){
        Log.d(TAG, "dmr added,device = " + device);
        DmrList.add(device);
    }

    // C glue
    private native long _init();
    private native int _start(long self);
    private native int _stop(long self);
    private native String _setActiveDms();
    private native String _setActiveDmr();
	private native String _checkVersion();
	
    private final long cSelf;

    static {
        System.loadLibrary("platinum-jni");
    }
}
