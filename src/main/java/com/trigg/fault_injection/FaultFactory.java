package com.trigg.fault_injection;

public class FaultFactory {
    Fault createFault(String type){
        Fault fault = null;
        if(type.equalsIgnoreCase("node-crash")){
            System.out.println("Creating new Node Crash fault...");
            fault = new NodeCrash();
        }
        else if(type.equalsIgnoreCase("node-restart")){
            System.out.println("Creating new Node Restart fault...");
            fault = new NodeRestart();
        }
        return fault;
    }
}
