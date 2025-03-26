package com.trigg.fault_injection.Controller;

import com.trigg.fault_injection.Service.DockerService;
import com.trigg.fault_injection.Service.FaultService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;

@RestController
@RequestMapping("/api/docker")
public class DockerController {
    private final DockerService dockerService;
    private FaultService faultService;

    public DockerController(DockerService dockerService, FaultService faultService) {
        this.dockerService = dockerService;
        this.faultService = faultService;
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

    @GetMapping("/define")
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

    /*@GetMapping("/stop")
    public String stopContainer() {
        dockerService.stopContainersAsync();
        return "Stopping a container asynchronously.";
    }*/

   /* @GetMapping("/delay")
    public String injectNetworkDelay() {
        dockerService.injectNetworkDelay();
        return "Injecting network delay asynchronously.";
    }*/

    /*@GetMapping("/restart")
    public String restartContainer() {
        dockerService.restartContainerAsync();
        return "Restarting a container asynchronously.";
    }*/
}
