package com.trigg.fault_injection.Controller;

import com.trigg.fault_injection.Service.FaultService;
import com.trigg.fault_injection.Utilities.AuthContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.stream.Collectors;

@ShellComponent
public class InjectFaultCommands {
    /*
     * Defines shell commands to inject pre-defined faults, list running/completed jobs,
     * and list all defined faults.
     */

    private FaultService faultService;
    private AuthContext authContext;

    @Autowired
    public InjectFaultCommands(FaultService faultService, AuthContext authContext){
        this.faultService = faultService;
        this.authContext = authContext;
    }

    @ShellMethod(key = "inject", value = "Inject a predefined fault")
    public String injectFault(@ShellOption (help = "Name of a predefined fault") String name,
                              @ShellOption(defaultValue = "0", help = "Scheduled start time in seconds after submission") int scheduledFor) {
        if (!authContext.isAuthenticated()) {
            return "You must be logged in to run this command.";
        }
        try{
            String result = faultService.injectRequestedFault(name, authContext.getUsername(), authContext.getRole(), scheduledFor);
            return result;
        }
        catch (DataAccessException e){
            return "Fault does not exist";
        }
    }

    @ShellMethod(key = "list-jobs", value = "List all current/previous jobs")
    public String listRunningJobs() {
        return faultService.listAllJobs().stream()
                .map(Object::toString)
                .collect(Collectors.joining("\n\n"));
    }

    @ShellMethod(key = "list-faults", value = "List all defined faults in the database")
    public String listAllDefinedFaults() {
        return faultService.listAllFaults().stream()
                .map(Object::toString)
                .collect(Collectors.joining("\n\n"));
    }

}
