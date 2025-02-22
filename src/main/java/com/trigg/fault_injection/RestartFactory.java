package com.trigg.fault_injection;

public class RestartFactory implements FaultFactory{
    @Override
    public Fault createFault() {
        System.out.println("Creating new Node Restart fault...");
        return new NodeRestart();
    }
}
