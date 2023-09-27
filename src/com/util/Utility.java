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
    public static boolean getKeyIgnition (char[] data){
        int keyIgnition = (int ) data[2];
        int calKeyIgnition = keyIgnition & 0x01;
        if(calKeyIgnition == 0)  return false;
        else return true;
    }

    public static boolean getRightIndicator (char[] data){
        int rightIndicator = (int ) data[2];
        int calRightindIcator = rightIndicator & 0b00100000;
        if( calRightindIcator == 0 ) return false;
        return true;
    }

    public static boolean getLeftindIcator (char[] data){
        int leftIndicator = (int ) data[2];
        int calLeftIndicator = leftIndicator & 0b00001000;
        if( calLeftIndicator == 0 ) return false;
        return true;
    }

    public static String getRidingMode ( char [] data) {
        int ridingMode = (int ) data[2];
        int calRidIngmode = ridingMode & 0b11110000;
        if( calRidIngmode == 0x00) return "PARK/IDLE";
        else if( calRidIngmode == 0x01) return "ECO";
        else if( calRidIngmode == 0x02 ) return "TOUR";
        else if( calRidIngmode == 0x03) return "SPORT";
        else if( calRidIngmode == 0x04) return "BOOST";
        else if( calRidIngmode == 0x05) return "REVERSE";
        else if( calRidIngmode == 0x06) return "LIMPHOME";
        else if( calRidIngmode == 0x07) return "Hyper";
        else if( calRidIngmode == 0x08) return "REGENERATION";
        else if( calRidIngmode == 0x09) return "DERATE";
        else if( calRidIngmode == 0x0A) return "GH3";
        else if( calRidIngmode == 0x0B) return "BOOST2";
        else return "Fault";
    }

    public static boolean getChargingStatus (char[] data){
        return true;
    }

    public static long getOdoMeter (char[] data){
        int odoMeterFirstByte = (int ) data[3];
        int odoMeterSecondByte = (int ) data[2];
        int odoMeterThirdByte = (int ) data[1];
        long calOdoMeter = ( odoMeterThirdByte << 16 | odoMeterSecondByte << 8 | odoMeterFirstByte);
        return calOdoMeter;
    }

}