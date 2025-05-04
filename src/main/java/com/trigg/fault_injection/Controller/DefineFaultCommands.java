package com.trigg.fault_injection.Controller;

import com.trigg.fault_injection.Model.Fault;
import com.trigg.fault_injection.Model.FaultType;
import com.trigg.fault_injection.Service.FaultService;
import com.trigg.fault_injection.Utilities.ShellAuthContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
public class DefineFaultCommands {

    private FaultService faultService;
    private ShellAuthContext shellAuthContext;

    @Autowired
    public DefineFaultCommands(FaultService faultService, ShellAuthContext shellAuthContext){
        this.faultService = faultService;
        this.shellAuthContext = shellAuthContext;
    }

    private String defineFaultInternal(FaultType type, String name, int duration, int numNodesOrThreads, Integer frequency) {
        if (!shellAuthContext.isAuthenticated()) {
            return "You must be logged in to run this command.";
        }
        try {
            Fault fault = faultService.defineFault(type.getTypeName());
            fault.setCommonAttr(shellAuthContext.getUsername(), name, duration, type.getTypeName());

            if (frequency != null) {
                fault.setUniqueAttr(numNodesOrThreads, frequency);
            } else {
                fault.setUniqueAttr(numNodesOrThreads);
            }

            int faultId = faultService.saveFault(fault);
            return "Successfully created " + type + " fault with ID: " + faultId;
        } catch (DataAccessException e) {
            return "Failed to insert fault due to database error.";
        } catch (Exception e) {
            return "Failed to define fault: " + e.getMessage();
        }
    }

    @ShellMethod(key = "define node-restart", value = "Define a node restart fault.")
    public String defineNodeRestart(
            @ShellOption(help = "Name of the fault") String name,
            @ShellOption(help = "Duration of the fault in seconds") int duration,
            @ShellOption(defaultValue = "1", help = "Number of nodes affected") int numNodes,
            @ShellOption(help = "Frequency of restarts") int frequency) {

        return defineFaultInternal(FaultType.NODE_RESTART, name, duration, numNodes, frequency);
    }

    @ShellMethod(key = "define cpu-stress-sc", value = "Define a CPU stress sidecar fault.")
    public String defineCpuStress(
            @ShellOption(help = "Name of the fault") String name,
            @ShellOption(help = "Duration of the fault in seconds") int duration,
            @ShellOption(defaultValue ="1", help = "Number of CPU stress threads") int numThreads) {

        return defineFaultInternal(FaultType.CPU_STRESS_SC, name, duration, numThreads, null);
    }

    @ShellMethod(key = "define node-crash", value = "Define a node crash fault.")
    public String defineNodeCrash(
            @ShellOption(help = "Name of the fault") String name,
            @ShellOption(help = "Duration of the fault in seconds") int duration,
            @ShellOption(defaultValue = "1", help = "Number of nodes affected") int numNodes) {

        return defineFaultInternal(FaultType.NODE_CRASH, name, duration, numNodes, null);
    }

}
