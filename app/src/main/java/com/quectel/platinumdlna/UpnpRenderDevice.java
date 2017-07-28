package com.quectel.platinumdlna;

import com.plutinosoft.platinum.PltDeviceData;

import java.util.ArrayList;

/**
 * Created by klein on 17-7-27.
 */

public class UpnpRenderDevice {

    public static UpnpRenderDevice mUpnpRenderDevice;
    public ArrayList<PltDeviceData> dmrlist;
    public PltDeviceData mActiveMediaRender;

    public static UpnpRenderDevice getInstance() {

        if (mUpnpRenderDevice == null){
            mUpnpRenderDevice = new UpnpRenderDevice();
        }
        return mUpnpRenderDevice;
    }
}
