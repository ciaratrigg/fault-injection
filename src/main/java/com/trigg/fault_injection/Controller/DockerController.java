package com.trigg.fault_injection.Controller;

import com.trigg.fault_injection.Model.Fault;
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

    @GetMapping("/list-containers")
    public List<String> listContainers(){
        return dockerService.listContainerIds();
    }
/*
    // Defines a fault that can be injected later
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

    // Immediately injects a pre-defined fault
    //TODO: fault dne returns error
    @GetMapping("/inject")
    public void injectFault(String type, String name){
        faultService.injectRequestedFault(type, name);
    }

    //Lists all defined faults
    @GetMapping("/list")
    public List<Fault> listAllFaults(){
        return faultService.listAllFaults();
    }



    //Schedules a fault for later execution
*/
}
