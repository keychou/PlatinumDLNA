package com.quectel.platinumdlna;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import com.plutinosoft.platinum.UPnP;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PlatinumDLNA extends AppCompatActivity{


    public static final String TAG = "PlatinumDLNA";

    UPnpWrapper mUPnpWrapper;
    Button btShowDMS, btShowDMR;
    ListView lvShowList;
    ArrayList<String> dmslist;
    ArrayList<String> dmrlist;
    String[] version = new String[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_platinum_dlna);



        lvShowList = (ListView) findViewById(R.id.listdevce);
        btShowDMS = (Button) findViewById(R.id.show_dms);
        btShowDMR = (Button) findViewById(R.id.show_dmr);

        mUPnpWrapper = new UPnpWrapper();

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
    }

    void showDevice(ArrayList<String> list){
        ArrayList<Map<String, String>> status = new ArrayList<Map<String, String>>();

        for (int i = 0; i < list.size(); i++ ){
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("name", list.get(i));
            map.put("value", list.get(i));
            status.add(map);
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_2, list);

        SimpleAdapter simpleAdapter = new SimpleAdapter(
                this,
                status,
                R.layout.device_item,
                new String[]{"name", "value"},
                new int[]{R.id.status_name, R.id.status_value});

        lvShowList.setAdapter(simpleAdapter);
    }
}
