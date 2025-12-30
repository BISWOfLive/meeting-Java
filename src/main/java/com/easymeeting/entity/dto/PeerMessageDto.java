package com.easymeeting.entity.dto;

import java.io.Serializable;

public class PeerMessageDto implements Serializable {

    private String signalType;
    private String signalData;

    public String getSignalType() {
        return signalType;
    }

    public void setSignalType(String signalType) {
        this.signalType = signalType;
    }

    public String getSignalData() {
        return signalData;
    }

    public void setSignalData(String signalData) {
        this.signalData = signalData;
    }
}
