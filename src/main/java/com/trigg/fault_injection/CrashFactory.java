package com.trigg.fault_injection;

public class CrashFactory implements FaultFactory{
    @Override
    public Fault createFault() {
        System.out.println("Creating new Node Crash fault...");
        return new NodeCrash();
    }
}
