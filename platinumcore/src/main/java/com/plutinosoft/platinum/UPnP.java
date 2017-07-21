package com.plutinosoft.platinum;

import android.util.Log;


public class UPnP {
    public static final String TAG = "PlatinumDLNA";
    public static final String VERSION_OF_PLATITUMDLNA = "1.0.2";



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

    public String getms() {
        return _getms();
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
        Log.d(TAG, "message from JNI native,device = " + device);
    }

    // C glue
    private native long _init();
    private native int _start(long self);
    private native int _stop(long self);
    private native String _setms();
    private native String _getms();
	private native String _checkVersion();
	
    private final long cSelf;

    static {
        System.loadLibrary("platinum-jni");
    }
}
