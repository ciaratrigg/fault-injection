package com.trigg.fault_injection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import java.sql.Date;
import java.util.List;

@RestController
@RequestMapping("/api/docker")
public class DockerController {
    private final DockerService dockerService;

    public DockerController(DockerService dockerService) {
        this.dockerService = dockerService;
    }


    @GetMapping("/list")
    public String listRunningContainers() {
        String result = "";
        List<String> containerIds = dockerService.listContainerIds();
        for(String s : containerIds){
            result = result + "Container ID: " + s + "\n";
        }
        return result;
    }

    @GetMapping("/stop")
    public String stopContainer() {
        dockerService.stopContainerAsync();
        return "Stopping a container asynchronously.";
    }

    @GetMapping("/delay")
    public String injectNetworkDelay() {
        dockerService.injectNetworkDelayAsync();
        return "Injecting network delay asynchronously.";
    }

    @GetMapping("/restart")
    public String restartContainer() {
        dockerService.restartContainerAsync();
        return "Restarting a container asynchronously.";
    }
}
