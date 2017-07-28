package com.quectel.platinumdlna;

import com.plutinosoft.platinum.PltDeviceData;

import java.util.ArrayList;

/**
 * Created by klein on 17-7-27.
 */

public class UpnpContentDevice {

    public static UpnpContentDevice mUpnpContentDevice;

    public ArrayList<PltDeviceData> dmslist;

    public PltDeviceData mActiveMediaServer;

    public static UpnpContentDevice getInstance() {

        if (mUpnpContentDevice == null){
            mUpnpContentDevice = new UpnpContentDevice();
        }
        return mUpnpContentDevice;
    }
}
