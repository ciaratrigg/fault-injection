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
    @Autowired
    private DynamicJobScheduler dynamicJobScheduler;

    public DockerController(DockerService dockerService) {
        this.dockerService = dockerService;
    }

    @GetMapping("/schedule-job")
    public String scheduleJob(@RequestParam String jobType, @RequestParam long time) {
        // For example, create a new fault injection task
        Runnable job = () -> System.out.println("Injecting fault at: " + time);

        // Schedule it at a specific time
        dynamicJobScheduler.scheduleJob(job, new Date(time));

        return "Job scheduled to run at: " + time;
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
