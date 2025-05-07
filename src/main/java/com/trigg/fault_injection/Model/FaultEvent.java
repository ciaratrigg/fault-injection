package com.trigg.fault_injection.Model;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class FaultEvent {
    private String faultType;
    private String targetContainer;
    private LocalDateTime timestamp;

    public FaultEvent(String faultType, String targetContainer, LocalDateTime timestamp) {
        this.faultType = faultType;
        this.targetContainer = targetContainer;
        this.timestamp = timestamp;
    }

    public String getFaultType() {
        return faultType;
    }

    public void setFaultType(String faultType) {
        this.faultType = faultType;
    }

    public String getTargetContainer() {
        return targetContainer;
    }

    public void setTargetContainer(String targetContainer) {
        this.targetContainer = targetContainer;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}

