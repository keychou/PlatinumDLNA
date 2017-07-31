package com.plutinosoft.platinum;

import android.util.Log;

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

    public int setActiveDms(String uuid) {
        return _setActiveDms(uuid);
    }

    public int setActiveDmr(String uuid) {
        return _setActiveDmr(uuid);
    }

    public String getActiveDms() {
        return _getActiveDms();
    }

    public String getActiveDmr() {
        return _getActiveDmr();
    }

    public MediaObject[] lsFiles() {

        MediaObject[] mediaObjects = _lsFiles();

        if ((mediaObjects != null)){
            for(int i = 0; i < mediaObjects.length; i++){
                Log.d(TAG, "mediaObjects["+ i + "] = " + mediaObjects[i]);
            }
        }

        return _lsFiles();
    }


    public int changeDirectory(String objectId) {
        return _changeDirectory(objectId);
    }

    public int cdup() {
        Log.d(TAG, "-----cdup----");
        return _cdup();
    }

    public int play(String objectId) {
        return _play(objectId);
    }

    public int seek(String realtime) {
        return _seek(realtime);
    }

    public int mediastop() {
        return _mediastop();
    }

    public int mute() {
        return _mute();
    }

    public int unmute() {
        return _unmute();
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
        mListener.onDmrRemoved(pltDeviceData);
    }



    // C glue
    private native long _init();
    private native int _start(long self);
    private native int _stop(long self);
    private native int _setActiveDms(String uuid);
    private native int _setActiveDmr(String uuid);
    private native String _getActiveDms();
    private native String _getActiveDmr();
	private native String _checkVersion();
    private native MediaObject[] _lsFiles();
    private native int _changeDirectory(String objectId);
    private native int _play(String objectId);
    private native int _cdup();
    private native int _seek(String realtime);
    private native int _mediastop();
    private native int _mute();
    private native int _unmute();

    private final long cSelf;

    static {
        System.loadLibrary("platinum-jni");
    }
}
