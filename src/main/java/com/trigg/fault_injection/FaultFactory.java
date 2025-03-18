package com.trigg.fault_injection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FaultFactory {
    //this should all be moved once define and inject are separate
    //instead it will just create the new fault and insert it into the database
    // then the calls to service will likely be in the controller
    @Autowired
    private DockerService dockerService;

    public FaultFactory(DockerService dockerService){
        this.dockerService = dockerService;
    }

    Fault createFault(String type, int num_nodes){
        Fault fault = null;
        if(type.equalsIgnoreCase("node-crash")){
            System.out.println("Creating new Node Crash fault...");
            //this should be moved too when they are separated
            dockerService.stopContainersAsync(num_nodes);
            fault = new NodeCrash();
        }
        else if(type.equalsIgnoreCase("node-restart")){
            System.out.println("Creating new Node Restart fault...");
            dockerService.restartContainersAsync(num_nodes);
            fault = new NodeRestart();
        }
        else if(type.equalsIgnoreCase("network-delay")){
            System.out.println("Creating new Network Delay fault...");
            //docker service command
            fault = new NetworkDelay();

        }
        else{
            System.out.println("Specified fault type does not exist.");
        }
        return fault;
    }
}
