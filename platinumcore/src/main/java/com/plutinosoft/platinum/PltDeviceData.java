package com.plutinosoft.platinum;

/**
 * Created by klein on 17-7-24.
 */

public class PltDeviceData {


    public PltDeviceData(String uuid,String friendlyName, String deviceType){
        this.uuid = uuid;
        this.friendlyName = friendlyName;
        this.deviceType = deviceType;
    }

    public String uuid;
    public String friendlyName;
    public String deviceType;

    @Override
    public String toString() {
        String devInfo = "uuid = " + uuid + ", friendlyName = " + friendlyName + ", deviceType" + deviceType;
        return devInfo;
    }
}
