package com.vehicle.aggregator

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.util.Log
import com.vehicle.aggregator.utils.SignalPacket

class ReService : Service() {
    private val TAG = "ReService"
    private val topicName = "com.royalenfield.digital.telemetry.info.ACTION_SEND" //From uart
    private val keyName = "packet"
    private var reDigitalBroadcaster: ReDigitalBroadcaster? = null
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
    private lateinit var receiver: BroadcastReceiver

    override fun onCreate() {
        super.onCreate()
        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == topicName) {
                    // Handle the broadcast here
                    val data = intent.getStringExtra("data")
                    Log.d(TAG, "Received broadcast with data: $data")

                    processData(data!!.toCharArray())
                }
            }
        }

        val filter = IntentFilter(topicName)
        registerReceiver(receiver, filter)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (reDigitalBroadcaster == null) {
            reDigitalBroadcaster = ReDigitalBroadcaster()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    fun processData(data: CharArray) {

        val intent = Intent(topicName)
        intent.action = topicName

        val firstByte = data[0].code
        val secondByte = data[1].code
        val possibleCanID = firstByte shl 8 or secondByte
        Log.e(
            TAG,"Possible CAN-ID in dec: $possibleCanID")
        when(possibleCanID){
            0x321 -> {
                val motorSpeedFirstByte = data[3].code
                val motorSpeedSecondByte = data[2].code
                val motorSpeed = motorSpeedSecondByte shl 8 or motorSpeedFirstByte
                val calMotorSpeed = motorSpeed.toDouble() * 0.1
                val signalPacket = SignalPacket("speed", possibleCanID, motorSpeed)
                intent.putExtra(keyName, signalPacket)
                try {
                    reDigitalBroadcaster?.sendBroadcast(intent)
                } catch (e: Exception) {
                    Log.e(TAG, "Exception:$e")
                }
            }

            0x12E -> {
                val motorSOCFirstByte = data[3].code
                val motorSOCSecondByte = data[2].code
                val soc = motorSOCSecondByte shl 8 or motorSOCFirstByte
                val calSoc = soc.toDouble() * 0.01
                val signalPacket = SignalPacket("soc", possibleCanID, calSoc)
                intent.putExtra(keyName, signalPacket)
                try {
                    reDigitalBroadcaster?.sendBroadcast(intent)
                } catch (e: Exception) {
                    Log.e(TAG, "Exception:$e")
                }
            }

            0x6BA -> {

                val highBeamBit = 0
                val lowBeamBit = 1
                val passBeamBit = 2
                val leftIndicatorBit = 3
                val noIndicatorBit = 4
                val rightIndicatorBit = 5
                val hornBit = 6
                val ecoModeBit = 7
                val tourModeBit = 1 //8
                val sportsModeBit = 2 //9
                val turboModeBit = 3 //10

                var lightData = data[2].code
                val isHighBeamOn = (lightData shr highBeamBit) and 0x1
                var signalPacket = SignalPacket("HighBeam", possibleCanID, isHighBeamOn)
                intent.putExtra(keyName, signalPacket)
                try {
                    reDigitalBroadcaster?.sendBroadcast(intent)
                } catch (e: Exception) {
                    Log.e(TAG, "Exception:$e")
                }

                val isLowBeamOn = (lightData shr lowBeamBit) and 0x1
                signalPacket = SignalPacket("LowBeam", possibleCanID, isLowBeamOn)
                intent.putExtra(keyName, signalPacket)
                try {
                    reDigitalBroadcaster?.sendBroadcast(intent)
                } catch (e: Exception) {
                    Log.e(TAG, "Exception:$e")
                }

                val isPassBeamOn = (lightData shr passBeamBit) and 0x1
                signalPacket = SignalPacket("PassBeam", possibleCanID, isPassBeamOn)
                intent.putExtra(keyName, signalPacket)
                try {
                    reDigitalBroadcaster?.sendBroadcast(intent)
                } catch (e: Exception) {
                    Log.e(TAG, "Exception:$e")
                }

                val isLeftIndicatorOn = (lightData shr leftIndicatorBit) and 0x1
                signalPacket = SignalPacket("LeftIndicator", possibleCanID, isLeftIndicatorOn)
                intent.putExtra(keyName, signalPacket)
                try {
                    reDigitalBroadcaster?.sendBroadcast(intent)
                } catch (e: Exception) {
                    Log.e(TAG, "Exception:$e")
                }

                val noIndicatorOn = (lightData shr noIndicatorBit) and 0x1
                signalPacket = SignalPacket("NoIndicator", possibleCanID, noIndicatorOn)
                intent.putExtra(keyName, signalPacket)
                try {
                    reDigitalBroadcaster?.sendBroadcast(intent)
                } catch (e: Exception) {
                    Log.e(TAG, "Exception:$e")
                }

                val isRightIndicatorOn = (lightData shr rightIndicatorBit) and 0x1
                signalPacket = SignalPacket("RightIndicator", possibleCanID, isRightIndicatorOn)
                intent.putExtra(keyName, signalPacket)
                try {
                    reDigitalBroadcaster?.sendBroadcast(intent)
                } catch (e: Exception) {
                    Log.e(TAG, "Exception:$e")
                }

                if( isRightIndicatorOn and isLeftIndicatorOn == 1){
                    signalPacket = SignalPacket("Hazard", possibleCanID, 1)
                    intent.putExtra(keyName, signalPacket)
                }
                try {
                    reDigitalBroadcaster?.sendBroadcast(intent)
                } catch (e: Exception) {
                    Log.e(TAG, "Exception:$e")
                }

                val isHornOn = (lightData shr hornBit) and 0x1
                signalPacket = SignalPacket("HornOn", possibleCanID, isHornOn)
                intent.putExtra(keyName, signalPacket)
                try {
                    reDigitalBroadcaster?.sendBroadcast(intent)
                } catch (e: Exception) {
                    Log.e(TAG, "Exception:$e")
                }

                val isEcoModeOn = (lightData shr ecoModeBit) and 0x1
                signalPacket = SignalPacket("EcoMode", possibleCanID, isEcoModeOn)
                intent.putExtra(keyName, signalPacket)
                try {
                    reDigitalBroadcaster?.sendBroadcast(intent)
                } catch (e: Exception) {
                    Log.e(TAG, "Exception:$e")
                }

                lightData = data[3].code
                val isTourModeOn = (lightData shr tourModeBit) and 0x1
                signalPacket = SignalPacket("TourMode", possibleCanID, isTourModeOn)
                intent.putExtra(keyName, signalPacket)
                try {
                    reDigitalBroadcaster?.sendBroadcast(intent)
                } catch (e: Exception) {
                    Log.e(TAG, "Exception:$e")
                }

                val isSportsModeOn = (lightData shr sportsModeBit) and 0x1
                signalPacket = SignalPacket("SportsMode", possibleCanID, isSportsModeOn)
                intent.putExtra(keyName, signalPacket)
                try {
                    reDigitalBroadcaster?.sendBroadcast(intent)
                } catch (e: Exception) {
                    Log.e(TAG, "Exception:$e")
                }

                val isTurboOn = (lightData shr turboModeBit) and 0x1
                signalPacket = SignalPacket("TurboOn", possibleCanID, isHighBeamOn)
                intent.putExtra(keyName, signalPacket)
                try {
                    reDigitalBroadcaster?.sendBroadcast(intent)
                } catch (e: Exception) {
                    Log.e(TAG, "Exception:$e")
                }

            }

            0x121 -> {
                var coastData = data[2].code
                val coastMode = decodeCoastMode(coastData)
                if(coastMode != null){
                    var signalPacket = SignalPacket(coastMode, possibleCanID, coastMode)
                    intent.putExtra(keyName, signalPacket)
                }
                try {
                    reDigitalBroadcaster?.sendBroadcast(intent)
                } catch (e: Exception) {
                    Log.e(TAG, "Exception:$e")
                }

                var driveModeData = data[7].code
                val driveMode = decodeDrivingMode(driveModeData)
                if(driveMode != null){
                    var signalPacket = SignalPacket(driveMode, possibleCanID, driveMode)
                    intent.putExtra(keyName, signalPacket)
                    try {
                        reDigitalBroadcaster?.sendBroadcast(intent)
                    } catch (e: Exception) {
                        Log.e(TAG, "Exception:$e")
                    }
                    if(driveMode == "REVERSE"){
                        var signalPacket = SignalPacket(driveMode, possibleCanID, driveMode)
                        intent.putExtra(keyName, signalPacket)
                        try {
                            reDigitalBroadcaster?.sendBroadcast(intent)
                        } catch (e: Exception) {
                            Log.e(TAG, "Exception:$e")
                        }
                    }
                }
            }

            0x6BB -> {
                var ignitionKeyData = data[2].code

                val ignitionBit = (ignitionKeyData shr 1) and 0x1
                val startButtonBit = ignitionKeyData and 0x1

                var signalPacket = SignalPacket("ignition_key", possibleCanID, ignitionBit)
                intent.putExtra(keyName, signalPacket)

                signalPacket = SignalPacket("start_key", possibleCanID, startButtonBit)
                intent.putExtra(keyName, signalPacket)
            }

        }

    }

    fun decodeDrivingMode(data: Int):String? {

        val drivingModes = listOf(
            "PARK/IDLE",
            "ECO",
            "TOUR",
            "SPORT",
            "BOOST",
            "REVERSE",
            "LIMPHOME",
            "Hyper",
            "REGENERATION",
            "DERATE",
            "GH3",
            "BOOST2",
            "Fault Mode"
        )

        if (data in 0..12) {
            val selectedMode = drivingModes[data]
            Log.i(TAG,"Driving Mode: $selectedMode")
            return selectedMode.toString()
        } else {
            Log.i(TAG,"Invalid driving mode")
            return null
        }
    }

    fun decodeCoastMode(data: Int):String? {
        val drivingModes = mapOf(
            0x0 to "Not Active",
            0x1 to "Coast_Regen",
            0x2 to "Coast_Forced_Regen",
            0x3 to "Coast_Brake_Regen",
            0x4 to "Coast_Forced_Brake_Regen"
        )

        val selectedMode = drivingModes[data]

        if (selectedMode != null) {
            Log.i(TAG,"Coast Mode: $selectedMode")
            return selectedMode.toString()
        } else {
            Log.i(TAG,"Invalid Driving Mode")
            return null
        }
    }

    fun decodeIgnitionKeyStatus(data: Int):String {

        val ignitionBit = (data shr 1) and 0x1
        val startButtonBit = data and 0x1

        // Interpret the ignition key status based on the combination of bits
        val ignitionStatus = when {
            ignitionBit == 1 && startButtonBit == 0 -> "Key Ignition On"
            ignitionBit == 0 && startButtonBit == 1 -> "Start Button Pressed"
            ignitionBit == 1 && startButtonBit == 1 -> "Key Ignition On and Start Button Pressed"
            else -> "Key Ignition Off"
        }

        Log.i(TAG,"Ignition Key Status: $ignitionStatus")
        return ignitionStatus
    }
}
