package com.quectel.platinumdlna;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.plutinosoft.platinum.FileManager;
import com.plutinosoft.platinum.MediaObject;
import com.plutinosoft.platinum.PltDeviceData;

import java.util.ArrayList;
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
    ImageButton ibShowDMR;
    ImageButton ibBack;
    ListView lvShowList;
    TextView tvTitle;
    ArrayList<PltDeviceData> dmslist;
    ArrayList<PltDeviceData> dmrlist;
    String[] version = new String[2];

    int listviewtype = LISTVIEW_TYPE_DEV;

    public PltDeviceData mActiveMediaServer;
    public PltDeviceData mActiveMediaRender;
    public String resId;
    public FileManager fileManager;

    MyHandler myHandler = new MyHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_platinum_dlna);



        lvShowList = (ListView) findViewById(R.id.listdevce);
        ibShowDMR = (ImageButton) findViewById(R.id.show_dmr);
        ibBack = (ImageButton) findViewById(R.id.back);
        tvTitle = (TextView) findViewById(R.id.dmslist);

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


        ibShowDMR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChooseMediaRender();

                Log.d(TAG, "listviewtype1 = " + listviewtype);
            }
        });

        tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "text view clicked");
            }
        });

        lvShowList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Log.d(TAG, "listviewtype2 = " + listviewtype);

                if (listviewtype == LISTVIEW_TYPE_DEV){
                    PltDeviceData pltDeviceData= dmslist.get(position);
                    Log.d(TAG, "show contents of " + pltDeviceData.friendlyName);

                    mUPnpWrapper.setActiveDms(pltDeviceData.uuid);
                    fileManager = new FileManager(mUPnpWrapper.uPnP, pltDeviceData.uuid);

                    Log.d(TAG, "mUPnpWrapper.getActiveDms() = " + mUPnpWrapper.getActiveDms());

                    ArrayList<MediaObject> mediaObjectArrayList = fileManager.listFiles();

                    if (mediaObjectArrayList != null){
                        mActiveMediaServer = pltDeviceData;
                        showFiles(mediaObjectArrayList);
                        tvTitle.setText(pltDeviceData.friendlyName);
                        listviewtype = LISTVIEW_TYPE_DIR;
                    }else{
                        Toast.makeText(PlatinumDLNA.this, "当前目录下没有文件", Toast.LENGTH_SHORT).show();
                    }

                } else if (listviewtype == LISTVIEW_TYPE_DIR) {
                    ArrayList<MediaObject> mediaObjectArrayList = fileManager.listFiles();


                    MediaObject mediaObject = mediaObjectArrayList.get(position);

                    fileManager.setObjectId(mediaObject.m_ObjectID);

                    mediaObjectArrayList = fileManager.listFiles();

                    if (mediaObjectArrayList != null){

                        Log.d(TAG, "show items under " + mediaObject.m_ObjectID);
                        tvTitle.setText(mediaObject.m_ObjectID);
                        showFiles(mediaObjectArrayList);
                        listviewtype = LISTVIEW_TYPE_ITEM;

                    }else{
                        mUPnpWrapper.cdup();
                        Toast.makeText(PlatinumDLNA.this, "当前目录下没有文件", Toast.LENGTH_SHORT).show();
                    }
                } else if (listviewtype == LISTVIEW_TYPE_ITEM){

                    fileManager.changeDirectory();

                    ArrayList<MediaObject> mediaObjectArrayList = fileManager.listFiles();

                    resId = mediaObjectArrayList.get(position).m_ObjectID;

                    Log.d(TAG, "play media file " + resId);

                    Intent intent = new Intent(PlatinumDLNA.this, UpnpController.class);

                    intent.putExtra(FileManager.FILE_OBJECT_UUID, resId);

                    startActivity(intent);
                }
            }
        });


        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listviewtype == LISTVIEW_TYPE_DIR){
                    Log.d(TAG, "back to dms list");

                    dmslist = mUPnpWrapper.getDmsList();
                    showDevice(dmslist);
                    tvTitle.setText("DMS服务器列表");

                    listviewtype = LISTVIEW_TYPE_DEV;

                } else if (listviewtype == LISTVIEW_TYPE_ITEM){
                    Log.d(TAG, "back to dir");
                    mUPnpWrapper.cdup();
                    ArrayList<MediaObject> mediaObjectArrayList = fileManager.listFiles();
                    showFiles(mediaObjectArrayList);

                    tvTitle.setText(mActiveMediaServer.friendlyName);
                    listviewtype = LISTVIEW_TYPE_DIR;
                }
            }
        });
    }


    public class MediaPlayThread extends Thread{

        @Override
        public void run() {
            Log.d(TAG, "wait play resource,play thread id = " + Thread.currentThread().getId());
            try{
                Log.d(TAG, "play resource : " + resId);
                mUPnpWrapper.play(resId);
                Log.d(TAG, "play resource down : " + resId);

            }catch (Exception e){
                e.printStackTrace();
            }


        }
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

        ibBack.setVisibility(View.GONE);

        Log.d(TAG, "listviewtype3 = " + listviewtype);
    }


    void showFiles(ArrayList<MediaObject> list){
        ArrayList<Map<String, String>> status = new ArrayList<Map<String, String>>();
        SimpleAdapter simpleAdapter;

        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("objectId", list.get(i).m_ObjectID);
                map.put("title", list.get(i).m_Title);
                status.add(map);
            }

            simpleAdapter = new SimpleAdapter(
                    this,
                    status,
                    R.layout.file_item,
                    new String[]{"objectId", "title"},
                    new int[]{R.id.object_title, R.id.object_id});

            lvShowList.setAdapter(simpleAdapter);
            ibBack.setVisibility(View.VISIBLE);

        }
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
                        mActiveMediaRender = dmrlist.get(which);
                        mUPnpWrapper.setActiveDmr(mActiveMediaRender.uuid);
                        Log.d(TAG, "get mActiveMediaRender = " + mUPnpWrapper.getActiveDmr());
                    }
                });

        singleChoiceDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mActiveMediaRender != null){
                            if (yourChoice != -1) {
                                Toast.makeText(PlatinumDLNA.this,
                                        "你选择了" + mActiveMediaRender.uuid,
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
        singleChoiceDialog.show();

        Log.d(TAG, "singleChoiceDialog.show()");

        Log.d(TAG, "listviewtype4 = " + listviewtype);

    }



    public class MyHandler extends Handler{
        public void handleMessage (Message msg) {
            Log.d(TAG, "msg.what = " + msg.what);
            switch (msg.what) {

                case EVENT_DMS_ADDED:
                case EVENT_DMS_REMOVED:
                {
                    dmslist = mUPnpWrapper.getDmsList();
                    if (listviewtype == LISTVIEW_TYPE_DEV){
                        showDevice(dmslist);
                    }
                }
                break;

                case EVENT_DMR_ADDED:
                case EVENT_DMR_REMOVED:
                {
                    dmrlist = mUPnpWrapper.getDmrList();
                }
                break;

                default:
                    super.handleMessage(msg);
            }
        }
    }

//    @Override
//    protected void onResume() {
//        Log.d(TAG, "--------------------resume--------------------");
//
//        super.onResume();
//    }
}
