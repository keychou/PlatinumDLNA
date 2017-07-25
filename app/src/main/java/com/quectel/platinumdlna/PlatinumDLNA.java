package com.quectel.platinumdlna;

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

import com.plutinosoft.platinum.PltDeviceData;
import com.plutinosoft.platinum.UPnP;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PlatinumDLNA extends AppCompatActivity{


    public static final String TAG = "PlatinumDLNA";

    /* Event Constants */
    protected static final int EVENT_DMS_ADDED            = 1;
    protected static final int EVENT_DMS_REMOVED                         = 2;
    protected static final int EVENT_DMR_ADDED          = 3;
    protected static final int EVENT_DMR_REMOVED                   = 4;


    UPnpWrapper mUPnpWrapper;
    Button btShowDMS, btShowDMR;
    ListView lvShowList;
    ArrayList<PltDeviceData> dmslist;
    ArrayList<PltDeviceData> dmrlist;
    String[] version = new String[2];

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
                Log.d(TAG, "position = " + position + ", id = " + id);
                PltDeviceData pltDeviceData= dmslist.get(position);
                Log.d(TAG, "pltDeviceData = " + pltDeviceData);

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
}
