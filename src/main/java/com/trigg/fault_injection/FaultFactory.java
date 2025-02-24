package com.trigg.fault_injection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FaultFactory {
    //this should all be moved once define and inject are separate
    @Autowired
    DockerService dockerService;

    public FaultFactory(DockerService dockerService){
        this.dockerService = dockerService;
    }

    Fault createFault(String type){
        Fault fault = null;
        if(type.equalsIgnoreCase("node-crash")){
            System.out.println("Creating new Node Crash fault...");
            //this should be moved too when they are separated
            dockerService.stopContainerAsync();
            fault = new NodeCrash();
        }
        else if(type.equalsIgnoreCase("node-restart")){
            System.out.println("Creating new Node Restart fault...");
            fault = new NodeRestart();
        }
        else{
            System.out.println("Specified fault type does not exist.");
        }
        return fault;
    }
}
