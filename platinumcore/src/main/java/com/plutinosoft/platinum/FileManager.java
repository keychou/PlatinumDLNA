package com.plutinosoft.platinum;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by klein on 17-7-27.
 */

public class FileManager {

    public String mCurrentObjectId;
    public String mActiveMediaServer;
    public UPnP mUPnP;

    public static final int FILE_TYPE_DIR =  0x30;
    public static final int FILE_TYPE_ITEM = FILE_TYPE_DIR + 1;
    public static final int FILE_TYPE_VIDEO = FILE_TYPE_DIR + 2;
    public static final int FILE_TYPE_MUSIC = FILE_TYPE_DIR + 3;
    public static final int FILE_TYPE_PICTURE = FILE_TYPE_DIR + 4;

    public static final String FILE_OBJECT_UUID= "content_uuid";
    public static final String FILE_OBJECT_TYPE= "content_type";

    public FileManager(UPnP uPnP, String uuid){
        mUPnP = uPnP;
        mActiveMediaServer = uuid;
    }

    public void setObjectId(String objectId){
        mCurrentObjectId = objectId;
        changeDirectory();
    }

    public void changeDirectory(){
        mUPnP.changeDirectory(mCurrentObjectId);
    }

    public ArrayList<MediaObject> listFiles(){
        MediaObject[] mediaObjects = mUPnP.lsFiles();

        ArrayList<MediaObject> mediaObjectArrayList = null;

        if (mediaObjects != null){
            mediaObjectArrayList = new ArrayList<MediaObject>(Arrays.asList(mediaObjects));
        }

        return mediaObjectArrayList;

    }


}
