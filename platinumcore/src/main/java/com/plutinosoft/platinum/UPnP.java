package com.plutinosoft.platinum;

import android.util.Log;

import java.util.ArrayList;


public class UPnP {
    public static final String TAG = "PlatinumDLNA";
    public static final String VERSION_OF_PLATITUMDLNA = "1.0.2";

    ArrayList<String> DmsList = new ArrayList();
    ArrayList<String> DmrList = new ArrayList();


    public interface DeviceStatusChangeListener{
        public void onDmsAdded(String device);
        public void onDmrAdded(String device);
    }


    private DeviceStatusChangeListener mListener;

    public void setDeviceStatusChangeListener(DeviceStatusChangeListener listener){
        mListener = listener;
    }

    public UPnP() {
        cSelf = _init();
    }

    public int start() {
        return _start(cSelf);
    }
    
    public int stop() {
        return _stop(cSelf);
    }

    public int setActiveDms(String device) {
        return _setActiveDms(device);
    }

    public int setActiveDmr(String device) {
        return _setActiveDmr(device);
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
        mListener.onDmsAdded(device);
        //DmsList.add(device);
    }

    public void onDmrAdded(String device){
        Log.d(TAG, "dmr added,device = " + device);
        //DmrList.add(device);
        mListener.onDmrAdded(device);

    }

    // C glue
    private native long _init();
    private native int _start(long self);
    private native int _stop(long self);
    private native int _setActiveDms(String device);
    private native int _setActiveDmr(String device);
	private native String _checkVersion();
	
    private final long cSelf;

    static {
        System.loadLibrary("platinum-jni");
    }
}
