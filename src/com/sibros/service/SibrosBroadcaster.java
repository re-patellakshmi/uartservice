package com.sibros.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.digital.services.ReDigitalBroadcaster;

public class SibrosBroadcaster extends Service {
    private static final String TAG = "SibrosBroadcaster";

    private final IBinder binder = new SibrosBroadcasterBinder();
    public void broadcast(Intent intent){
        try{
            sendBroadcast(intent);
        }catch (Exception e){
            Log.e(TAG, "Exception:"+e);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public class SibrosBroadcasterBinder extends Binder {
        SibrosBroadcasterBinder getService() {
            return SibrosBroadcasterBinder.this;
        }
    }
}
