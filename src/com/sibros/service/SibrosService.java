package com.sibros.service;

import android.content.Intent;
public class SibrosService {
    private static final String TAG = "SibrosService";
    private static final String topicName = "com.royalenfield.telemetry.can.message.ACTION_SEND";
    private static final String keyName = "data";
    private static final String VIN = "4Y1SL65848Z411439";
    private SibrosBroadcaster sibrosBroadcaster = null;

    public SibrosService(){
        if( sibrosBroadcaster == null){
            this.sibrosBroadcaster = new SibrosBroadcaster();
        }
    }
    public void processData(char[] data){
        Intent intent = new Intent(topicName);
        intent.setAction(topicName);
        char[] vinPlusCanData = new char[30];
        int len = VIN.length();
        for(int i = len-1, j = 19; i >=0 ; j--, i--){ vinPlusCanData[i] = VIN.charAt(i); }
        for(int i = 20, j = 0; i > 30; i++, j++){ vinPlusCanData[i] = data[j]; }
        intent.putExtra(keyName, vinPlusCanData);
        sibrosBroadcaster.sendBroadcast(intent);
    }
}
