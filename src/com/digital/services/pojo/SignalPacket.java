package com.digital.services.pojo;
import android.content.Intent;

import java.io.Serializable;

public class SignalPacket implements Serializable {
    private String signal;
    private Integer canId;
    private Object data;
    private Long timestamp;

    public String getSignal() {
        return signal;
    }

    public void setSignal(String signal) {
        this.signal = signal;
    }

    public Integer getCanId() {
        return canId;
    }

    public void setCanId(Integer canId) {
        this.canId = canId;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public SignalPacket(){};
    public SignalPacket(String signal, Integer canId, Object data){
        this.signal = signal;
        this.canId = canId;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }


}
