package com.trigg.fault_injection;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.io.IOException;
import java.util.List;

// add this line to application.properties to launch interactive shell by default
// spring.shell.interactive.enabled=true

@ShellComponent
public class FaultCommands {

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

    @ShellMethod(key = "network-latency")
    public String networkLatency(
            @ShellOption(defaultValue = "spring") String arg
    ) {
        return "Hello world3 " + arg;
    }

    @ShellMethod(key = "throttle-bandwidth")
    public String throttleBandwidth(
            @ShellOption(defaultValue = "spring") String arg
    ) {
        return "Hello world4 " + arg;
    }

}
