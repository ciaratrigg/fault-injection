package com.trigg.fault_injection.Controller;

import com.trigg.fault_injection.Model.Fault;
import com.trigg.fault_injection.Service.FaultService;
import com.trigg.fault_injection.Utilities.ShellAuthContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.stream.Collectors;

@ShellComponent
public class InjectFaultCommands {
    private FaultService faultService;
    private ShellAuthContext shellAuthContext;

    @Autowired
    public InjectFaultCommands(FaultService faultService, ShellAuthContext shellAuthContext){
        this.faultService = faultService;
        this.shellAuthContext = shellAuthContext;
    }

    @ShellMethod(key = "inject", value = "Inject a fault into the target system")
    public String injectFault(@ShellOption (help = "Name of a predefined fault") String name,
                              @ShellOption(defaultValue = "0", help = "Scheduled start time in seconds after submission") int scheduledFor) {
        if (!shellAuthContext.isAuthenticated()) {
            return "You must be logged in to run this command.";
        }
        try{
            String result = faultService.injectRequestedFault(name, shellAuthContext.getUsername(), shellAuthContext.getRole(), scheduledFor);
            return result;
        }
        catch (DataAccessException e){
            return "Fault does not exist";
        }
    }

    @ShellMethod(key = "list-jobs", value = "List previous/current jobs")
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
