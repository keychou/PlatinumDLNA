package com.quectel.platinumdlna;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.plutinosoft.platinum.FileManager;

import static com.quectel.platinumdlna.PlatinumDLNA.TAG;
import static com.quectel.platinumdlna.UPnpWrapper.mUPnpWrapper;

public class UpnpController extends AppCompatActivity {

    public String resid;
    public String mActiveMediaServer;
    public String mActiveMediaRender;
    UPnpWrapper mUPnpWrapper = UPnpWrapper.getInstance();

    TextView tvMediaInfo;
    SeekBar ibSeek;
    ImageButton ibPlayPause;
    ImageButton ibPrev;
    ImageButton ibNext;
    VerticalSeekbar mVerticalSeekbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upnp_controller);


        Intent intent = getIntent();
        resid = intent.getStringExtra(FileManager.FILE_OBJECT_UUID);

        initView();

        mActiveMediaServer = mUPnpWrapper.getActiveDms();
        mActiveMediaRender = mUPnpWrapper.getActiveDmr();

        (new MediaPlayThread()).start();


    }


    void initView(){
        tvMediaInfo = (TextView)findViewById(R.id.media_info);
        ibSeek = (SeekBar) findViewById(R.id.seek);
        mVerticalSeekbar = (VerticalSeekbar)findViewById(R.id.sound_controller);
        ibPrev =  (ImageButton) findViewById(R.id.play_control_prev);
        ibNext =  (ImageButton) findViewById(R.id.play_control_next);
        ibPlayPause =  (ImageButton) findViewById(R.id.play_control_play_pause);

        tvMediaInfo.setText(resid);
    }


    public class MediaPlayThread extends Thread{

        @Override
        public void run() {
            this.setName("playThread - " + Thread.currentThread().getId());
            Thread.currentThread().setName("playThread - " + Thread.currentThread().getId());
            Log.d(TAG, "wait play resour ce,play thread id = " + Thread.currentThread().getId());
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
