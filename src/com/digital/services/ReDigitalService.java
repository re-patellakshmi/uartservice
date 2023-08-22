package com.digital.services;

import android.content.Intent;
import android.util.Log;

import com.digital.services.pojo.SignalPacket;

public class ReDigitalService {
    private static final String TAG = "ReDigitalService";
    private static final String topicName = "com.royalenfield.telemetry.info.ACTION_SEND";
    private static final String keyName = "packet";
    private ReDigitalBroadcaster reDigitalBroadcaster = null;

    public ReDigitalService(){
        if( reDigitalBroadcaster == null){
            this.reDigitalBroadcaster = new ReDigitalBroadcaster();
        }
    }
    public void processData(char[] data){

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
                reDigitalBroadcaster.sendBroadcast(intent);
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
                reDigitalBroadcaster.sendBroadcast(intent);
            }catch (Exception e){
                Log.e(TAG, "Exception:"+e);
            }
            Log.e(TAG, "12e routine has been completed");
            return;
        }

    }
}
