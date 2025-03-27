package com.trigg.fault_injection.Controller;

import com.trigg.fault_injection.Model.Fault;
import com.trigg.fault_injection.Model.NodeCrash;
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
    @GetMapping("/inject")
    public String injectFault(String type, String name){
        try{
            //fault service call
        } catch(IllegalArgumentException e){
            return "Error " +e.getMessage();
        }
        return "";
    }

    //Lists all defined faults


    //Schedules a fault for later execution

}
