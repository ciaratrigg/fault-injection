package com.trigg.fault_injection;

import com.trigg.fault_injection.Service.FaultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

// add this line to application.properties to launch interactive shell by default
// spring.shell.interactive.enabled=true

@ShellComponent
public class FaultCommands {

    private FaultService faultService;

    @Autowired
    public FaultCommands(FaultService faultService){
        this.faultService = faultService;
    }

    @ShellMethod(key = "define")
    public String defineFault(String type, String name, int duration){
        try{
            int result = faultService.defineFault(type, name, duration);
            if(result == 1){
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
