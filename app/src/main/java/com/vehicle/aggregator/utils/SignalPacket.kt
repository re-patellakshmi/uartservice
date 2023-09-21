package com.vehicle.aggregator.utils

import java.io.Serializable


class SignalPacket : Serializable {
    private var signal: String? = null
    private var canId: Int? = null
    private var data: Any? = null
    private var timestamp: Long? = null

    constructor()
    constructor(signal: String?, canId: Int?, data: Any?) {
        this.signal = signal
        this.canId = canId
        this.data = data
        timestamp = System.currentTimeMillis()
    }
}
