package com.trigg.fault_injection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

// add this line to application.properties to launch interactive shell by default
// spring.shell.interactive.enabled=true

@ShellComponent
public class FaultCommands {

    private FaultFactory faultFactory;

    @Autowired
    public FaultCommands(FaultFactory faultFactory){
        this.faultFactory = faultFactory;
    }


    @ShellMethod(key = "define")
    public String injectFault(String type, int num_nodes){
        try{
            Fault fault = faultFactory.createFault(type, num_nodes);
            if(fault != null){
                return type + " Fault created successfully";
            }
            else{
                return "Invalid input. Try again.";
            }
        } catch(IllegalArgumentException e){
            return "Error " + e.getMessage();
        }
    }

}
