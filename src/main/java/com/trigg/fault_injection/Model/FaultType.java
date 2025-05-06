package com.trigg.fault_injection.Model;

public enum FaultType {
    NODE_RESTART("node-restart"),
    CPU_STRESS_SC("cpu-stress-sc"),
    NODE_CRASH("node-crash"),
    NETWORK_DELAY("network-delay"),
    BANDWIDTH_THROTTLE("bandwidth-throttle");

    private final String typeName;

    FaultType(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeName() {
        return typeName;
    }

    @Override
    public String toString() {
        return typeName;
    }
}
