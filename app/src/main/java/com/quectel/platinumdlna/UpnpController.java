package com.quectel.platinumdlna;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.plutinosoft.platinum.FileManager;

import static com.quectel.platinumdlna.PlatinumDLNA.TAG;
import static com.quectel.platinumdlna.UPnpWrapper.mUPnpWrapper;

public class UpnpController extends AppCompatActivity {

    public String resid;
    public String mActiveMediaServer;
    public String mActiveMediaRender;
    UPnpWrapper mUPnpWrapper = UPnpWrapper.getInstance();
    MediaPlayThread mediaPlayThread = new MediaPlayThread();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upnp_controller);

        Intent intent = getIntent();
        resid = intent.getStringExtra(FileManager.FILE_OBJECT_UUID);

        mActiveMediaServer = mUPnpWrapper.getActiveDms();
        mActiveMediaRender = mUPnpWrapper.getActiveDmr();

        (new MediaPlayThread()).start();

    }


    public class MediaPlayThread extends Thread{

        @Override
        public void run() {
            Log.d(TAG, "wait play resource,play thread id = " + Thread.currentThread().getId());
            try{
                Log.d(TAG, "play resource : " + resid);
                mUPnpWrapper.play(resid);
                Log.d(TAG, "play resource down : " + resid);

            }catch (Exception e){
                e.printStackTrace();
            }


        }
    }
}
