package com.datagram;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.quectel.uartservice.UART;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;

public class DatagramService extends Service {

    DatagramSocket socket;
    String TAG = "DatagramService";
    char[] readData;
    DatagramThread mDatagramThread;
    private int START_FRAME_SIZE = 4;

    private final IBinder binder = new DatagramServiceBinder();

    @Override
    public IBinder onBind(Intent intent) {

        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            Log.d("TAG", "Socket not created!");
        }

        return binder;
    }

    class DatagramThread extends Thread {
        @Override
        public void run() {
            super.run();

            uart_init();

            try{

                while (true) {
                    Log.e(TAG, "Started reading of can-data...");
                    readData = dataPacket();
                    try{

                        final String VIN = "4Y1SL65848Z411439";
                        Log.d(TAG,"Prepering the data for SibroBoradcaster");
                        char[] vinPlusCanData = new char[30];
                        int len = VIN.length();

                        Log.d(TAG, "Copying... VIN for sibros");
                        System.arraycopy(VIN.toCharArray(), 0, vinPlusCanData, 0, len);

                        Log.d(TAG, "Copying... actual data for sibros");
                        System.arraycopy(readData, 0, vinPlusCanData, 20, 10);

                        sendViaDatagram(vinPlusCanData);

                    }catch (Exception e){
                        Log.e("UartService", "Exception found during broadcosting to digit via bytes"+e);
                    }
                }

            } catch (Exception e) {
                Log.e(TAG, "Service Stopped");
                Log.e(TAG, e.toString());
            }
        }
    }

    public int onStartCommand(Intent intent, int flags, int startId) {

        mDatagramThread = new DatagramThread();
        mDatagramThread.start();

        return Service.START_STICKY_COMPATIBILITY;
    }

    public void sendViaDatagram(char[] data) {
        try {
            if(socket == null) {
                socket = new DatagramSocket();
            }

            String serverAddress = "127.0.0.1"; // Replace with your server address
            int serverPort = 8080; // Replace with the server port

            Log.e("serverAddress", serverAddress);
            Log.e("serverPort", serverPort+"");
            Log.e("data", Arrays.toString(data));

            byte[] sendData = new String(data).getBytes(); // Convert char[] to byte[]

            InetAddress serverInetAddress = InetAddress.getByName(serverAddress);
            DatagramPacket packet = new DatagramPacket(sendData, sendData.length, serverInetAddress, serverPort);

            socket.send(packet);
            Log.e("Datagram", "packet sent via datagram");

        } catch (Exception e) {
            Log.e("Datagram", "Exception found during datagram to digit via bytes"+e);
        }
    }


    public static class DatagramServiceBinder extends Binder {
        DatagramServiceBinder getService() {
            // Return this instance of LocalService so clients can call public methods.
            return DatagramService.DatagramServiceBinder.this;
        }
    }


    private UART ttyHSLx;

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


}
