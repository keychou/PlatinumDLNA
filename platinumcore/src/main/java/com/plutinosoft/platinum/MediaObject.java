package com.plutinosoft.platinum;

import android.util.Log;

/**
 * Created by klein on 17-7-26.
 */

public class MediaObject {
    public static final String TAG = "PlatinumDLNA.MO";

    public String m_Objecttype;
    public String m_ObjectID;
    public String m_ParentID;
    public String m_Title;

    public boolean isFile() {
        Log.d(TAG, "m_ParentID.startsWith(\"0/\") = " + m_ParentID);
        return m_ParentID.startsWith("0/");
    }

    @Override
    public String toString() {
        String mediainfo = "[type :" + m_Objecttype +
                "][ID :" + m_ObjectID +
                "][PID :" + m_ParentID +
                "][title :" + m_Title + "]";
        return mediainfo;
    }
}
