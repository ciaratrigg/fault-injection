package com.trigg.fault_injection;

import com.github.dockerjava.api.model.Container;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/docker")
public class DockerController {

    private DockerService dockerService;

    @Autowired
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
    public String stopContainers() throws IOException {
        return dockerService.stopContainer();
    }

    @GetMapping("/status")
    public void containerStatus() throws IOException {
        System.out.println(dockerService.containerStatus());
    }

    @GetMapping("/delay")
    public void delayNetwork() throws Exception {
        System.out.println(dockerService.networkDelay());
    }

}

