package com.quectel.platinumdlna;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import com.plutinosoft.platinum.UPnP;

public class PlatinumDLNA extends AppCompatActivity {

    UPnP uPnP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_platinum_dlna);

        uPnP = new UPnP();
        uPnP.start();

        // Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.sample_text);
        tv.setText(uPnP.getVersion());
    }

}
