package com.trigg.fault_injection;

public class RestartFactory {

    public Fault createFault() {
        System.out.println("Creating new Node Restart fault...");
        return new NodeRestart();
    }
}
