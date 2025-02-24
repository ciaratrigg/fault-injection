package com.trigg.fault_injection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.io.IOException;
import java.util.List;

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
    public String defineFault(String type){
        try{
            Fault fault = faultFactory.createFault(type);
            return type + "Fault created successfully";
        } catch(IllegalArgumentException e){
            return "Error " + e.getMessage();
        }
    }

    @ShellMethod(key = "node-crash")
    public String nodeCrash(
            @ShellOption(defaultValue = "spring") String arg
    ) {
        return "Hello world " + arg;
    }

    @ShellMethod(key = "node-restart")
    public String nodeRestart(
            @ShellOption(defaultValue = "spring") String arg
    ) {
        return "Hello world2 " + arg;
    }
}
