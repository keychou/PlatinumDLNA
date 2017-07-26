package com.plutinosoft.platinum;

/**
 * Created by klein on 17-7-26.
 */

public class MediaObject {
    public String m_Objecttype;
    public String m_ObjectID;
    public String m_ParentID;
    public String m_Title;

    @Override
    public String toString() {
        String mediainfo = "[type :" + m_Objecttype +
                           "][ID :" + m_ObjectID +
                           "][PID :" + m_ParentID +
                           "][title :" + m_Title + "]";
        return mediainfo;
    }
}
