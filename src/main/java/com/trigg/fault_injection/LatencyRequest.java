package com.trigg.fault_injection;

public class LatencyRequest {
    private String proxyName;
    private int latency;

    // Default constructor
    public LatencyRequest() {}

    // Constructor
    public LatencyRequest(String proxyName, int latency) {
        this.proxyName = proxyName;
        this.latency = latency;
    }

    // Getters and Setters
    public String getProxyName() {
        return proxyName;
    }

    public void setProxyName(String proxyName) {
        this.proxyName = proxyName;
    }

    public int getLatency() {
        return latency;
    }

    public void setLatency(int latency) {
        this.latency = latency;
    }
}

