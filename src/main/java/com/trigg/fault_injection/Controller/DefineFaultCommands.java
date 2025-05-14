package com.trigg.fault_injection.Controller;

import com.trigg.fault_injection.Model.Fault;
import com.trigg.fault_injection.Model.FaultType;
import com.trigg.fault_injection.Service.FaultService;
import com.trigg.fault_injection.Utilities.AuthContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.io.IOException;

@ShellComponent
public class DefineFaultCommands {

    private FaultService faultService;
    private AuthContext authContext;

    @Autowired
    public DefineFaultCommands(FaultService faultService, AuthContext authContext) throws IOException {
        this.faultService = faultService;
        this.authContext = authContext;
    }

    private String defineFaultInternal(FaultType type, String name, int duration, int numNodesOrThreads, Integer frequency, long latency) {
        if (!authContext.isAuthenticated()) {
            return "You must be logged in to run this command.";
        }
        try {
            Fault fault = faultService.defineFault(type.getTypeName());
            fault.setCommonAttr(authContext.getUsername(), name, duration, type.getTypeName());

            if (frequency != null) {
                fault.setUniqueAttr(numNodesOrThreads, frequency);
            }
            else if(latency != -1){
                fault.setUniqueAttr(latency);
            }
            else {
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

        return defineFaultInternal(FaultType.NODE_RESTART, name, duration, numNodes, frequency, -1);
    }

    @ShellMethod(key = "define cpu-stress-sc", value = "Define a CPU stress sidecar fault.")
    public String defineCpuStress(
            @ShellOption(help = "Name of the fault") String name,
            @ShellOption(help = "Duration of the fault in seconds") int duration,
            @ShellOption(defaultValue ="1", help = "Number of CPU stress threads") int numThreads) {

        return defineFaultInternal(FaultType.CPU_STRESS_SC, name, duration, numThreads, null, -1);
    }

    @ShellMethod(key = "define node-crash", value = "Define a node crash fault.")
    public String defineNodeCrash(
            @ShellOption(help = "Name of the fault") String name,
            @ShellOption(help = "Duration of the fault in seconds") int duration,
            @ShellOption(defaultValue = "1", help = "Number of nodes affected") int numNodes) {

        return defineFaultInternal(FaultType.NODE_CRASH, name, duration, numNodes, null, -1);
    }

    @ShellMethod(key = "define network-delay", value = "Define a network delay fault.")
    public String defineNetworkDelay(
            @ShellOption(help = "Name of the fault") String name,
            @ShellOption(help = "Duration of the fault in seconds") int duration,
            @ShellOption(help = "Latency in milliseconds") long latency) {

            return defineFaultInternal(FaultType.NETWORK_DELAY, name, duration, -1, null, latency);
    }

    @ShellMethod(key = "define bandwidth-throttle", value = "Define a bandwidth throttle fault.")
    public String defineBandwidthThrottle(
            @ShellOption(help = "Name of the fault") String name,
            @ShellOption(help = "Duration of the fault in seconds") int duration,
            @ShellOption(help = "Rate in kbps") long rate) {

        return defineFaultInternal(FaultType.BANDWIDTH_THROTTLE, name, duration, -1, null, rate);
    }
}
