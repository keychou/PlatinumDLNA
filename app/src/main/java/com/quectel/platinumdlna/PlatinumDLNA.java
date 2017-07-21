package com.quectel.platinumdlna;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.plutinosoft.platinum.UPnP;

public class PlatinumDLNA extends AppCompatActivity {

    public static final String TAG = "PlatinumDLNA";

    UPnP uPnP;
    Button btShowDMS, btShowDMR;
    String[] version = new String[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_platinum_dlna);

        uPnP = new UPnP();

        //checkVersion must be called before we start upnp.
        if (uPnP.checkVersion(version)){
            Log.d(TAG,"version check ok " + version[0]);
        } else {
            Log.d(TAG,"expected version of platinum sdk is " + version[1] + ", but we got " + version[0]);
        }


        Log.d(TAG, "uPnP.start() = " + uPnP.start());

        btShowDMS = (Button) findViewById(R.id.show_dms);
        btShowDMR = (Button) findViewById(R.id.show_dmr);

        btShowDMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "uPnP.start() = " + uPnP.getms());
            }
        });



        // Examp le of a call to a native method
       // TextView tv = (TextView) findViewById(R.id.sample_text);
       // tv.setText(uPnP.getVersion());
    }

}
