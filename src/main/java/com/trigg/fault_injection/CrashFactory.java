package com.trigg.fault_injection;

public class CrashFactory{
    public Fault createFault() {
        System.out.println("Creating new Node Crash fault...");
        return new NodeCrash();
    }
}
