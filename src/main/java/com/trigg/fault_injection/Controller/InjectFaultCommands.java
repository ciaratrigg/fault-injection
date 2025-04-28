package com.trigg.fault_injection.Controller;

import com.trigg.fault_injection.Model.Fault;
import com.trigg.fault_injection.Service.FaultService;
import com.trigg.fault_injection.Utilities.ShellAuthContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
public class InjectFaultCommands {
    private FaultService faultService;
    private ShellAuthContext shellAuthContext;

    @Autowired
    public InjectFaultCommands(FaultService faultService, ShellAuthContext shellAuthContext){
        this.faultService = faultService;
        this.shellAuthContext = shellAuthContext;
    }

    @ShellMethod(key = "inject")
    public String injectFault(@ShellOption String name) {
        if (!shellAuthContext.isAuthenticated()) {
            return "You must be logged in to run this command.";
        }
        try{
            String result = faultService.injectRequestedFault(name, shellAuthContext.getUsername(), shellAuthContext.getRole());
            return result;
        }
        catch (DataAccessException e){
            return "Fault does not exist";
        }
    }



}
