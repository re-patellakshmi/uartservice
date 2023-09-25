package com.util;

import android.content.Intent;

import com.pojo.SignalPacket;

public class Utility {
    public  static Intent getIntent(String topicName, String keyName, SignalPacket signalPacket){
        Intent intent = new Intent(topicName);
        intent.setAction(topicName);
        String jsonString = signalPacket.toJSON();
        intent.putExtra(keyName, jsonString);
        return intent;
    }

    public static int getCanId(char[] data){
        int firstByte = ( int ) data[0];
        int secondByte = (int ) data[1];
        int possibleCanID =  ( firstByte << 8 | secondByte );
        return possibleCanID;
    }

    public static double getSpeed(char[] data){
        int motorSpeedFirstByte = (int ) data[3];
        int motorSpeedSecondByte = (int ) data[2];
        int motorSpeed = (motorSpeedSecondByte << 8 | motorSpeedFirstByte );
        double calMotorSpeed = (double ) motorSpeed * ( 0.1 );
        return calMotorSpeed;
    }

    public static double getSoc(char[] data){
        int motorSOCFirstByte = (int ) data[3];
        int motorSOCSecondByte = (int ) data[2];
        int soc = ( motorSOCSecondByte << 8 | motorSOCFirstByte );
        double calSoc = (double ) soc * ( 0.01 );
        return calSoc;
    }

    public static double getLowSoc(){
        return 20.00;
    }
    public static boolean getkeyignition (char[] data){
        int keyignition = (int ) data[2];
        int calkeyignition = keyignition & 0x01;
        if(calkeyignition == 0)  return false;
        else return true;
    }

    public static boolean getrightindicator (char[] data){
        int rightindicator = (int ) data[2];
        int calrightindicator = rightindicator & 0b00010000;
        if( calrightindicator == 0 ) return false;
        return true;
    }

    public static boolean getleftindicator (char[] data){
        int leftindicator = (int ) data[2];
        int calleftindicator = leftindicator & 0b00010000;
        if( calleftindicator == 0 ) return false;
        return true;
    }

    public static String getridingmode ( char [] data) {
        int ridingmode = (int ) data[2];
        int calridingmode = ridingmode & 0b11110000;
        if( calridingmode == 0x00) return "PARK/IDLE";
        else if( calridingmode == 0x01) return "ECO";
        else if( calridingmode == 0x02 ) return "TOUR";
        else if( calridingmode == 0x03) return "SPORT";
        else if( calridingmode == 0x04) return "BOOST";
        else if( calridingmode == 0x05) return "REVERSE";
        else if( calridingmode == 0x06) return "LIMPHOME";
        else if( calridingmode == 0x07) return "Hyper";
        else if( calridingmode == 0x08) return "REGENERATION";
        else if( calridingmode == 0x09) return "DERATE";
        else if( calridingmode == 0x0A) return "GH3";
        else if( calridingmode == 0x0B) return "BOOST2";
        else return "Fault";
    }

    public static boolean getChargingStatus (char[] data){
        return true;
    }

}
