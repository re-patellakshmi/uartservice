//
// Created by lakshmi patel on 2023/6/12
package com.quectel.uartservice;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.digital.services.ReDigitalService;
import com.sibros.service.SibrosService;

public class UARTService extends Service {
    private static final String TAG = "uartservice-UARTService";

    private static final String topicName = "com.royalenfield.telemetry.info.ACTION_SEND";
    private static final String keyName = "packet";
    private UART ttyHSLx;
    private ReDigitalService reDigitalService;
    private SibrosService sibrosService;

    private UARTThread mUARTThread;
    private int RING_BUFFER_SIZE = 100;
    private int CAN_FRAME_SIZE = 10;
    private int START_FRAME_SIZE = 4;
    private char[]  speedData= new char[10];
    private char[]  readData= new char[10];
    private char[]  ringBuffer =  new char[RING_BUFFER_SIZE];

    private long timeCounter = 0l;

    private final IBinder binder = new UartServiceBinder();

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"UART Service Started");
        reDigitalService = new ReDigitalService();
        sibrosService = new SibrosService();
        mUARTThread = new UARTThread();
        mUARTThread.start();
        return 0;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    int data_bridge_init(){
        try {

        }catch (Exception e){
            return 1;
        }
        return 0;
    }

    int uart_init()
    {
        ttyHSLx = new UART();
        int rt = ttyHSLx.uartOpen();
        if (rt != 0) {
            Log.e(TAG, "open port failed");
            return -1;
        }
        Log.d(TAG, "open port succeeded");
        return 0;
    }

    void uart_deinit()
    {
        ttyHSLx.uartClose();
    }

    char uart_read()
    {
        return ttyHSLx.uartRead();
    }

    void sendDataToDigitService(char[] data){
        try{ reDigitalService.processData(data); }catch (Exception e){}
    }

    void sendDataToSibrosService(char[] data){
        try{ sibrosService.processData(data); }catch (Exception e){}
    }

    void processData(char[] data){

        int firstByte = ( int ) data[0];
        int secondByte = (int ) data[1];
        int possibleCanID1 = (firstByte | secondByte << 8);
        int possibleCanID2 =  ( firstByte << 8 | secondByte );
        Log.e(TAG, "Possible CAN-ID in dec: "+possibleCanID1);
        Log.e(TAG, "Possible CAN-ID in dec: "+possibleCanID2);

        if( possibleCanID1 == 0x321 || possibleCanID2 == 0x321){
            int motorSpeedFirstByte = (int ) data[3];
            int motorSpeedSecondByte = (int ) data[2];
            int motorSpeed = (motorSpeedSecondByte << 8 | motorSpeedFirstByte );
            double calMotorSpeed = (double ) motorSpeed * ( 0.1 );
            Intent intent = new Intent(topicName);
            intent.setAction(topicName);
            intent.putExtra("id", possibleCanID2);
            intent.putExtra("speed",  calMotorSpeed);
            intent.putExtra("data", "RE-Data is coming");
            try{
                sendBroadcast(intent);
            }catch (Exception e){
                Log.e(TAG, "Exception:"+e);
            }
            return;
        }

        if( possibleCanID1 == 0x12E || possibleCanID2 == 0x12E){
            int motorSOCFirstByte = (int ) data[3];
            int motorSOCSecondByte = (int ) data[2];
            int soc = ( motorSOCSecondByte << 8 | motorSOCFirstByte );
            double calSoc = (double ) soc * ( 0.01 );
            Intent intent = new Intent(topicName);
            intent.setAction(topicName);
            intent.putExtra("id", possibleCanID2);
            intent.putExtra("soc",  calSoc);
            intent.putExtra("data", "RE-Data is coming");
            try{
                sendBroadcast(intent);
            }catch (Exception e){
                Log.e(TAG, "Exception:"+e);
            }
			Log.e(TAG, "12e routine has been completed");
            return;
        }

    }

    int uart_write(char sendbyte)
    {
        return ttyHSLx.uartWrite(sendbyte);
    }

    char randomChar(int counter){
        int x = (int) ((Math.random()*10 ) % 10);
        char[] chars ={ '0','1','2','3','4', '5', '6', '7', '8', '9'};
        if( counter % 11 == 0 ) return '\n';
        return chars[x];
    }

    char[] dataPacket(){
        char byteReceived;
        char[] tempData = new char[10];
        int counter = 0;
        int charIntValue;
        do {
            byteReceived = uart_read();
            charIntValue = ( int ) byteReceived;
            Log.e(TAG, "Byte-Index: "+counter +" "+"Byte-Value: "+charIntValue);
            counter++;
        }while (counter < START_FRAME_SIZE || byteReceived == 0xFF || byteReceived == 0x00 );

        Log.e(TAG, "Started collecting data packet-frame");
        tempData[0] = byteReceived;
        charIntValue = (int ) tempData[0];
        Log.e(TAG, "Byte-Index: "+ 0 +" "+"Byte-Value: "+charIntValue);
        for(int i = 1; i < 10; i++){
            tempData[i] = uart_read();
            charIntValue = (int ) tempData[i];
            Log.e(TAG, "Byte-Index: "+ i +" "+"Byte-Value: "+charIntValue);
        }

        return tempData;
    }

    class UARTThread extends Thread {
        @Override
        public void run() {
            super.run();
            uart_init();
            data_bridge_init();

            while (true) {
                Log.e(TAG, "Started reading of can-data...");
                readData = dataPacket();
                processData(readData);
                sendDataToDigitService(readData);
                sendDataToSibrosService(readData);
            }
        }
    }

    public class UartServiceBinder extends Binder {
        UartServiceBinder getService() {
            // Return this instance of LocalService so clients can call public methods.
            return UartServiceBinder.this;
        }
    }
}
