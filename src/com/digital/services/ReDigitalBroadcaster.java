package com.digital.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.digital.services.pojo.SignalPacket;

public class ReDigitalBroadcaster extends Service {
    private static final String TAG = "ReDigitalBroadcaster";

    private static final String topicName = "com.royalenfield.digital.telemetry.info.ACTION_SEND";
    private static final String keyName = "packet";

    private final IBinder binder = new ReDigitalLocalBinder();
    ReDigitThread reDigitThread;

    public void broadcast(char[] data){

        int firstByte = ( int ) data[0];
        int secondByte = (int ) data[1];
        int possibleCanID =  ( firstByte << 8 | secondByte );
        Log.e(TAG, "Possible CAN-ID in dec: "+possibleCanID);

        if( possibleCanID == 0x321){
            int motorSpeedFirstByte = (int ) data[3];
            int motorSpeedSecondByte = (int ) data[2];
            int motorSpeed = (motorSpeedSecondByte << 8 | motorSpeedFirstByte );
            double calMotorSpeed = (double ) motorSpeed * ( 0.1 );
            Intent intent = new Intent(topicName);
            intent.setAction(topicName);
            SignalPacket signalPacket = new SignalPacket("speed", possibleCanID, motorSpeed);
            intent.putExtra(keyName, signalPacket);
            try{
                sendBroadcast(intent);
            }catch (Exception e){
                Log.e(TAG, "Exception:"+e);
            }
            return;
        }

        if( possibleCanID == 0x12E){
            int motorSOCFirstByte = (int ) data[3];
            int motorSOCSecondByte = (int ) data[2];
            int soc = ( motorSOCSecondByte << 8 | motorSOCFirstByte );
            double calSoc = (double ) soc * ( 0.01 );
            Intent intent = new Intent(topicName);
            intent.setAction(topicName);
            SignalPacket signalPacket = new SignalPacket("soc", possibleCanID, calSoc);
            intent.putExtra(keyName, signalPacket);
            try{
                sendBroadcast(intent);
            }catch (Exception e){
                Log.e(TAG, "Exception:"+e);
            }
            Log.e(TAG, "12e routine has been completed");
            return;
        }

    }
    public void process(char[] data){
        broadcast(data);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int flags, int startId){
        reDigitThread = new ReDigitThread();
        reDigitThread.start();
        return 0;
    }

    class ReDigitThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (true) {
                Log.e(TAG, "ReDigitalService started... successfully");
            }
        }
    }

    public class ReDigitalLocalBinder extends Binder {
        public ReDigitalBroadcaster getService() {
            // Return this instance of LocalService so clients can call public methods.
            return ReDigitalBroadcaster.this;
        }
    }

}
