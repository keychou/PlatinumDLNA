package com.plutinosoft.platinum;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class UPnP {
    public static final String TAG = "PlatinumDLNA";
    public static final String VERSION_OF_PLATITUMDLNA = "1.0.2";

    public interface DeviceStatusChangeListener{
        public void onDmsAdded(PltDeviceData pltDeviceData);
        public void onDmsRemoved(PltDeviceData pltDeviceData);
        public void onDmrAdded(PltDeviceData pltDeviceData);
        public void onDmrRemoved(PltDeviceData pltDeviceData);
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

    public boolean checkVersion(String[] version) {
        String version_of_sdk = _checkVersion();
        version[0] = version_of_sdk;
        version[1] = VERSION_OF_PLATITUMDLNA;
        if (version[0].equals(VERSION_OF_PLATITUMDLNA)){
            return true;
        }
        return false;
    }

    public void onDmsAdded(String uuid, String friendName, String deviceType){

        Log.d(TAG, "dms added,uuid = " + uuid + ", friendName = " + friendName + ", deviceType" + deviceType);
        PltDeviceData pltDeviceData = new PltDeviceData(uuid,friendName,deviceType);
        mListener.onDmsAdded(pltDeviceData);
    }

    public void onDmsRemoved(String uuid, String friendName, String deviceType){

        Log.d(TAG, "dms removed,uuid = " + uuid + ", friendName = " + friendName + ", deviceType" + deviceType);
        PltDeviceData pltDeviceData = new PltDeviceData(uuid,friendName,deviceType);
        mListener.onDmsRemoved(pltDeviceData);
    }

    public void onDmrAdded(String uuid, String friendName, String deviceType){
        Log.d(TAG, "dmr added,uuid = " + uuid + ", friendName = " + friendName + ", deviceType" + deviceType);
        PltDeviceData pltDeviceData = new PltDeviceData(uuid,friendName,deviceType);
        mListener.onDmrAdded(pltDeviceData);
    }

    public void onDmrRemoved(String uuid, String friendName, String deviceType){

        Log.d(TAG, "dmr removed,uuid = " + uuid + ", friendName = " + friendName + ", deviceType" + deviceType);
        PltDeviceData pltDeviceData = new PltDeviceData(uuid,friendName,deviceType);
        mListener.onDmsRemoved(pltDeviceData);
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
