package com.util;

import android.content.Intent;

import com.enums.RidingMode;
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
        return (calKeyIgnition == 1);
    }

    public static boolean getRightIndicator (char[] data){
        int rightIndicator = (int ) data[2];
        int calRightindIcator = rightIndicator & 0b00100000;
        return ( calRightindIcator == 1 );
    }

    public static boolean getLeftIndIcator (char[] data){
        int leftIndicator = (int ) data[2];
        int calLeftIndicator = leftIndicator & 0b00001000;
        return ( calLeftIndicator == 1 );
    }

    public static String getRidingMode ( char [] data) {
        int ridingMode = (int ) data[2];
        int calRidingMode = ridingMode & 0b11110000;
        return RidingMode.getRidingModeByValue(calRidingMode).get().getFullName();
    }

    public static boolean getChargingStatus (char[] data){
        return true;
    }

}
