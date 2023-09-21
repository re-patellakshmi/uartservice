package com.vehicle.aggregator

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.vehicle.aggregator.utils.SignalPacket


class ReDigitalBroadcaster : Service() {
    private val binder: IBinder = ReDigitalLocalBinder()
    var reDigitThread: ReDigitThread? = null
    fun broadcast(data: CharArray) {
        val firstByte = data[0].code
        val secondByte = data[1].code
        val possibleCanID = firstByte shl 8 or secondByte
        Log.e(
            TAG,
            "Possible CAN-ID in dec: $possibleCanID"
        )
        if (possibleCanID == 0x321) {
            val motorSpeedFirstByte = data[3].code
            val motorSpeedSecondByte = data[2].code
            val motorSpeed = motorSpeedSecondByte shl 8 or motorSpeedFirstByte
            val calMotorSpeed = motorSpeed.toDouble() * 0.1
            val intent = Intent(topicName)
            intent.action = topicName
            val signalPacket = SignalPacket("speed", possibleCanID, motorSpeed)
            intent.putExtra(keyName, signalPacket)
            try {
                sendBroadcast(intent)
            } catch (e: Exception) {
                Log.e(TAG, "Exception:$e")
            }
            return
        }
        if (possibleCanID == 0x12E) {
            val motorSOCFirstByte = data[3].code
            val motorSOCSecondByte = data[2].code
            val soc = motorSOCSecondByte shl 8 or motorSOCFirstByte
            val calSoc = soc.toDouble() * 0.01
            val intent = Intent(topicName)
            intent.action = topicName
            val signalPacket = SignalPacket("soc", possibleCanID, calSoc)
            intent.putExtra(keyName, signalPacket)
            try {
                sendBroadcast(intent)
            } catch (e: Exception) {
                Log.e(TAG, "Exception:$e")
            }
            Log.e(TAG, "12e routine has been completed")
            return
        }
    }

    fun process(data: CharArray) {
        broadcast(data)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        reDigitThread = ReDigitThread()
        reDigitThread!!.start()
        return android.app.Service.START_NOT_STICKY}

    inner class ReDigitThread : Thread() {
        override fun run() {
            super.run()
            while (true) {
                Log.e(TAG, "ReDigitalService started... successfully")
            }
        }
    }

    inner class ReDigitalLocalBinder : Binder() {
        val service: ReDigitalBroadcaster
            get() =// Return this instance of LocalService so clients can call public methods.
                this@ReDigitalBroadcaster
    }

    companion object {
        private const val TAG = "ReDigitalBroadcaster"
        private const val topicName = "com.royalenfield.digital.telemetry.info.ACTION_SEND"
        private const val keyName = "packet"
    }
}