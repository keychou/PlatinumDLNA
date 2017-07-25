package com.plutinosoft.platinum;

/**
 * Created by klein on 17-7-25.
 */

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.lang.ref.WeakReference;


/** @hide */
public class Registrant
{

    public static final String TAG = "PlatinumDLNA.R";

    public Registrant(Handler h, int what, Object obj){
        refH = new WeakReference(h);
        Log.d(TAG, "refH = " + refH);
        this.what = what;
        userObj = obj;
    }

    public void clear(){
        refH = null;
        userObj = null;
    }

    public void notifyRegistrant(){
        internalNotifyRegistrant (null, null);
    }

    public void notifyResult(Object result){
        internalNotifyRegistrant (result, null);
    }

    public void notifyException(Throwable exception){
        internalNotifyRegistrant (null, exception);
    }


    /*package*/ void internalNotifyRegistrant (Object result, Throwable exception){
        Handler h = getHandler();

        Log.d(TAG, "h = " + h);

        if (h == null) {
            clear();
        } else {
            Message msg = Message.obtain();

            msg.what = what;
            Log.d(TAG, "internalNotifyRegistrant result = " + result);

            h.sendMessage(msg);
        }
    }

    /**
     * NOTE: May return null if weak reference has been collected
     */

    public Message messageForRegistrant(){
        Handler h = getHandler();

        if (h == null) {
            clear();

            return null;
        } else {
            Message msg = h.obtainMessage();

            msg.what = what;
            msg.obj = userObj;

            return msg;
        }
    }

    public Handler getHandler(){
        if (refH == null)
            return null;

        return (Handler) refH.get();
    }

    WeakReference   refH;
    int             what;
    Object          userObj;
}
