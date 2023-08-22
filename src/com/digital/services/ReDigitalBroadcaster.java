package com.digital.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;


public class ReDigitalBroadcaster extends Service {
    private static final String TAG = "ReDigitalBroadcaster";

    private final IBinder binder = new ReDigitalBroadcasterBinder();
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

    public class ReDigitalBroadcasterBinder extends Binder {
        ReDigitalBroadcasterBinder getService() {
            return ReDigitalBroadcasterBinder.this;
        }
    }
}
