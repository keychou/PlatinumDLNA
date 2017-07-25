package com.quectel.platinumdlna;

import android.os.Handler;
import android.util.Log;

import com.plutinosoft.platinum.PltDeviceData;
import com.plutinosoft.platinum.Registrant;
import com.plutinosoft.platinum.RegistrantList;
import com.plutinosoft.platinum.UPnP;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by klein on 17-7-21.
 */

public class UPnpWrapper extends UPnP implements UPnP.DeviceStatusChangeListener {

    public static final String TAG = "PlatinumDLNA.UW";

    UPnP uPnP;

    ArrayList<PltDeviceData> DmsList = new ArrayList<PltDeviceData>();
    ArrayList<PltDeviceData> DmrList = new ArrayList<PltDeviceData>();

    protected RegistrantList mDeviceStatusRegistrants = new RegistrantList();

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
        return DmsList;
    }

    public ArrayList getDmrList() {
        return DmrList;
    }

    public boolean checkVersion(String[] version) {
        return uPnP.checkVersion(version);
    }


    @Override
    public void onDmsAdded(PltDeviceData pltDeviceData){
        Log.d(TAG, "dms add, pltDeviceData:" + pltDeviceData);
        DmsList.add(pltDeviceData);
        mDeviceStatusRegistrants.notifyRegistrants();
    }

    @Override
    public void onDmrAdded(PltDeviceData pltDeviceData){
        Log.d(TAG, "dmr add, pltDeviceData:" + pltDeviceData);
        DmrList.add(pltDeviceData);
    }


    public void registerForDeviceStatusChange(Handler h, int what, Object obj) {
        Log.d(TAG, "registerForDeviceStatusChange, h = " + h);
        Registrant r = new Registrant(h, what, obj);
        Log.d(TAG, "registerForDeviceStatusChange, what = " + what);
        mDeviceStatusRegistrants.add(r);
    }

}
