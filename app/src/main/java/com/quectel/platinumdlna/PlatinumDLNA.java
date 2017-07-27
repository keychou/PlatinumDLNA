package com.quectel.platinumdlna;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.plutinosoft.platinum.MediaObject;
import com.plutinosoft.platinum.PltDeviceData;
import com.plutinosoft.platinum.UPnP;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class PlatinumDLNA extends AppCompatActivity{


    public static final String TAG = "PlatinumDLNA";

    /* Event Constants */
    protected static final int EVENT_DMS_ADDED   = 1;
    protected static final int EVENT_DMS_REMOVED = 2;
    protected static final int EVENT_DMR_ADDED   = 3;
    protected static final int EVENT_DMR_REMOVED = 4;

    protected static final int LISTVIEW_TYPE_DEV = 0X10;
    protected static final int LISTVIEW_TYPE_DIR = LISTVIEW_TYPE_DEV + 1;
    protected static final int LISTVIEW_TYPE_ITEM = LISTVIEW_TYPE_DEV + 2;

    public final Object lock = new Object();

    UPnpWrapper mUPnpWrapper;
    Button btShowDMS, btShowDMR;
    ListView lvShowList;
    ArrayList<PltDeviceData> dmslist;
    ArrayList<PltDeviceData> dmrlist;
    String[] version = new String[2];

    int listviewtype = LISTVIEW_TYPE_DEV;

    public String mActiveMediaServer;
    public String mActiveMediaRender;
    public String resId;


    MyHandler myHandler = new MyHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_platinum_dlna);



        lvShowList = (ListView) findViewById(R.id.listdevce);
        btShowDMS = (Button) findViewById(R.id.show_dms);
        btShowDMR = (Button) findViewById(R.id.show_dmr);

        mUPnpWrapper = new UPnpWrapper();

        mUPnpWrapper.registerForDeviceStatusChange(myHandler, EVENT_DMS_ADDED, null);
        mUPnpWrapper.registerForDeviceStatusChange(myHandler, EVENT_DMS_REMOVED, null);
        mUPnpWrapper.registerForDeviceStatusChange(myHandler, EVENT_DMR_ADDED, null);
        mUPnpWrapper.registerForDeviceStatusChange(myHandler, EVENT_DMR_REMOVED, null);

        //checkVersion must be called before we start upnp.
        if (mUPnpWrapper.checkVersion(version)){
            Log.d(TAG,"version check ok " + version[0]);
        } else {
            Log.d(TAG,"expected version of platinum sdk is " + version[1] + ", but we got " + version[0]);
        }


        Log.d(TAG, "mUPnpWrapper.start() = " + mUPnpWrapper.start());

        btShowDMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dmslist = mUPnpWrapper.getDmsList();
                for (int i=0; i < dmslist.size(); i++){
                    Log.d(TAG, "dms = " + dmslist.get(i));
                }
                showDevice(dmslist);
            }
        });

        btShowDMR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dmrlist = mUPnpWrapper.getDmrList();
                for (int i=0; i < dmrlist.size(); i++){
                    Log.d(TAG, "dmr = " + dmrlist.get(i));
                }
                showDevice(dmrlist);
            }
        });


        lvShowList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (listviewtype == LISTVIEW_TYPE_DEV){
                    Log.d(TAG, "show dir");
                    Log.d(TAG, "position = " + position + ", id = " + id);
                    PltDeviceData pltDeviceData= dmslist.get(position);
                    Log.d(TAG, "pltDeviceData = " + pltDeviceData);

                    mUPnpWrapper.setActiveDms(pltDeviceData.uuid);

                    Log.d(TAG, "mUPnpWrapper.getActiveDms() = " + mUPnpWrapper.getActiveDms());

                    MediaObject[] mediaObjects = mUPnpWrapper.lsFiles();

                    ArrayList<MediaObject> mediaObjectArrayList = new ArrayList<MediaObject>(Arrays.asList(mediaObjects));

                    showFiles(mediaObjectArrayList);

                    listviewtype = LISTVIEW_TYPE_DIR;
                } else if (listviewtype == LISTVIEW_TYPE_DIR){
                    Log.d(TAG, "show dev");
                    MediaObject[] mediaObjects = mUPnpWrapper.lsFiles();

                    mUPnpWrapper.changeDirectory(mediaObjects[1].m_ObjectID);

                    mediaObjects = mUPnpWrapper.lsFiles();

                    ArrayList<MediaObject> mediaObjectArrayList = new ArrayList<MediaObject>(Arrays.asList(mediaObjects));

                    showFiles(mediaObjectArrayList);

                    listviewtype = LISTVIEW_TYPE_ITEM;
                }else if (listviewtype == LISTVIEW_TYPE_ITEM){
                    Log.d(TAG, "show mr");
                    ChooseMediaRender();

                    Log.d(TAG, "position = " + position + ", id = " + id);
                    MediaObject[] mediaObjects = mUPnpWrapper.lsFiles();

                    resId = mediaObjects[position].m_ObjectID;

                    MediaPlayThread mediaPlayThread = new MediaPlayThread();
                    Log.d(TAG, "main thread id = " + Thread.currentThread().getId());
                    mediaPlayThread.start();
                }
            }
        });
    }

    void showDevice(ArrayList<PltDeviceData> list){
        ArrayList<Map<String, String>> status = new ArrayList<Map<String, String>>();

        for (int i = 0; i < list.size(); i++ ){
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("friendlyname", list.get(i).friendlyName);
            map.put("uuid", list.get(i).uuid);
            map.put("type", list.get(i).deviceType);
            status.add(map);
        }

        SimpleAdapter simpleAdapter = new SimpleAdapter(
                this,
                status,
                R.layout.device_item,
                new String[]{"friendlyname", "uuid", "type"},
                new int[]{R.id.status_friendlyname, R.id.status_uuid, R.id.status_type});

        lvShowList.setAdapter(simpleAdapter);
    }


    void showFiles(ArrayList<MediaObject> list){
        ArrayList<Map<String, String>> status = new ArrayList<Map<String, String>>();

        for (int i = 0; i < list.size(); i++ ){
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("objectId", list.get(i).m_ObjectID);
            map.put("title", list.get(i).m_Title);
            status.add(map);
        }

        SimpleAdapter simpleAdapter = new SimpleAdapter(
                this,
                status,
                R.layout.file_item,
                new String[]{"objectId", "title"},
                new int[]{R.id.object_title, R.id.object_id});

        lvShowList.setAdapter(simpleAdapter);
    }

    String[] items;
    int yourChoice;

    public void ChooseMediaRender(){

        ArrayList<String> devUuids =new ArrayList<String>();

        dmrlist = mUPnpWrapper.getDmrList();
        for (int i=0; i < dmrlist.size(); i++){

            PltDeviceData pltDeviceData= dmrlist.get(i);
            Log.d(TAG, "dmr = " + pltDeviceData);
            devUuids.add(pltDeviceData.friendlyName);
        }

        items = devUuids.toArray(new String[devUuids.size()]);

        showSingleChoiceDialog(items);


    }

    private void showSingleChoiceDialog(String[] devUuids){

        AlertDialog.Builder singleChoiceDialog =
                new AlertDialog.Builder(PlatinumDLNA.this);
        singleChoiceDialog.setTitle("Choose a Render");
        // 第二个参数是默认选项，此处设置为0

        singleChoiceDialog.setSingleChoiceItems(items, 0,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mActiveMediaRender = dmrlist.get(which).uuid;
                        mUPnpWrapper.setActiveDmr(mActiveMediaRender);
                        Log.d(TAG, "get mActiveMediaRender = " + mUPnpWrapper.getActiveDmr());

                        if (mActiveMediaRender != null){
                            try{
                                synchronized (lock) {
                                    Log.d(TAG, "MediaRender is ready, start to play");
                                    lock.notify();
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }

                    }
                });

        singleChoiceDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (yourChoice != -1) {
                            Toast.makeText(PlatinumDLNA.this,
                                    "你选择了" + mActiveMediaRender,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        singleChoiceDialog.show();

        Log.d(TAG, "singleChoiceDialog.show()");

    }



    public class MyHandler extends Handler{
        public void handleMessage (Message msg) {
            Log.d(TAG, "msg.what = " + msg.what);
            switch (msg.what) {

                case EVENT_DMS_ADDED: {
                    dmslist = mUPnpWrapper.getDmsList();
                    showDevice(dmslist);
                }
                break;

                case EVENT_DMS_REMOVED: {
                    dmslist = mUPnpWrapper.getDmsList();
                    showDevice(dmslist);
                }
                break;

                case EVENT_DMR_ADDED: {
                    dmrlist = mUPnpWrapper.getDmrList();
                }
                break;

                case EVENT_DMR_REMOVED: {

                }
                break;

                default:
                    super.handleMessage(msg);
            }
        }
    }

    public class MediaPlayThread extends Thread{

        @Override
        public void run() {
            Log.d(TAG, "wait play resource,play thread id = " + Thread.currentThread().getId());
            try{
                synchronized (lock) {
                    lock.wait();
                    Log.d(TAG, "play resource");
                    mUPnpWrapper.play(resId);


                }
            }catch (Exception e){
                e.printStackTrace();
            }


        }
    }
}
