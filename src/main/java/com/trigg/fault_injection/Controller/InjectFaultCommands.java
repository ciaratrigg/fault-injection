package com.trigg.fault_injection.Controller;

import com.trigg.fault_injection.Model.Fault;
import com.trigg.fault_injection.Service.FaultService;
import com.trigg.fault_injection.Utilities.ShellAuthContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

public class InjectFaultCommands {
    private FaultService faultService;
    private ShellAuthContext shellAuthContext;

    @Autowired
    public InjectFaultCommands(FaultService faultService, ShellAuthContext shellAuthContext){
        this.faultService = faultService;
        this.shellAuthContext = shellAuthContext;
    }

    /*
    default value if not given :

    @ShellComponent
    public class MyCommands {

	@ShellMethod(key = "hello-world")
	public String helloWorld(
		@ShellOption(defaultValue = "spring") String arg
	) {
		return "Hello world " + arg;
	}
}
     */

    @ShellMethod(key = "define node-restart")
    public String defineNodeRestart(String name, int duration, int scheduled_for, int num_nodes, int frequency){
        if(!shellAuthContext.isAuthenticated()){
            return "You must be logged in to run this command.";
        }
        Fault fault = faultService.defineFault("node-restart");
        fault.setCommonAttr(shellAuthContext.getUsername(), name, duration, scheduled_for, "node-restart");
        fault.setUniqueAttr(num_nodes, frequency);
        try{
            int faultId = faultService.saveFault(fault);
            return "Successfully created node-restart with id " + faultId;
        }
        catch(DataAccessException e){
            return "Failed to insert fault";
        }
    }

    @ShellMethod(key = "define cpu-stress-sc")
    public String defineCpuStress(String name, int duration, int scheduled_for, int num_nodes){
        if(!shellAuthContext.isAuthenticated()){
            return "You must be logged in to run this command.";
        }
        Fault fault = faultService.defineFault("cpu-stress-sc");
        fault.setCommonAttr(shellAuthContext.getUsername(), name, duration, scheduled_for, "cpu-stress-sc");
        fault.setUniqueAttr(num_nodes);
        try{
            int faultId = faultService.saveFault(fault);
            return "Successfully created cpu-stress-sc with id " + faultId;
        }
        catch(DataAccessException e){
            return "Failed to insert fault";
        }
    }

    @ShellMethod(key = "define node-crash")
    public String defineNodeCrash(String name, int duration, int scheduled_for, int num_nodes){
        if(!shellAuthContext.isAuthenticated()){
            return "You must be logged in to run this command.";
        }
        Fault fault = faultService.defineFault("node-crash");
        fault.setCommonAttr(shellAuthContext.getUsername(), name, duration, scheduled_for, "node-crash");
        fault.setUniqueAttr(num_nodes);
        try{
            int faultId = faultService.saveFault(fault);
            return "Successfully created node-crash with id " + faultId;
        }
        catch(DataAccessException e){
            return "Failed to insert fault";
        }
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
