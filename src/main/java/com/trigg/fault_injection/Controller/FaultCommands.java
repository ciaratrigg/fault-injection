package com.trigg.fault_injection.Controller;

import com.trigg.fault_injection.Service.FaultService;
import com.trigg.fault_injection.Utilities.ShellAuthContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

// add this line to application.properties to launch interactive shell by default
// spring.shell.interactive.enabled=true

@ShellComponent
public class FaultCommands {

    private FaultService faultService;
    private ShellAuthContext shellAuthContext;

    @Autowired
    public FaultCommands(FaultService faultService, ShellAuthContext shellAuthContext){
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

    @ShellMethod("Inject a test fault (requires login)")
    public String injectFault(@ShellOption String faultType) {
        if (!shellAuthContext.isAuthenticated()) {
            return "You must be logged in to run this command.";
        }

        if (!shellAuthContext.hasRole("ROLE_ADMIN")) {
            return "You do not have permission to run this command.";
        }

        // Run the fault injection...
        return "Fault of type " + faultType + " injected by " + shellAuthContext.getUsername();
    }

}
