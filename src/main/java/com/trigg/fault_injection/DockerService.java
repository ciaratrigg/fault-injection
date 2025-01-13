package com.trigg.fault_injection;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.Container;
import org.springframework.stereotype.Service;

import javax.imageio.IIOException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.trigg.fault_injection.TerminalProcess.runCommand;

@Service
public class DockerService {

    private final DockerClient dockerClient;
    private ContainerIdParser parser;

    public DockerService(DockerClient dockerClient) {
        this.dockerClient = dockerClient;
        this.parser = new ContainerIdParser();
    }

    // List running containers
    public List<Container> listContainers() {
        return dockerClient.listContainersCmd()
                .withShowAll(true) // Includes stopped containers
                .exec();
    }

    public List<String> listContainerIds(){
        List<Container> allContainers = listContainers();
        List<String> containerIds = new ArrayList<>();
        for(Container container : allContainers){
            containerIds.add(container.getId());
        }
        return containerIds;
    }

    // Create a new container
    public String createContainer(String imageName, String containerName) {
        CreateContainerResponse container = dockerClient.createContainerCmd(imageName)
                .withName(containerName)
                .exec();
        return container.getId();
    }

    // Start a container
    public void startContainer(String containerId) {
        dockerClient.startContainerCmd(containerId).exec();
    }

    // Stop a container
    public String stopContainer() throws IOException {
        List<Container> allContainers = listContainers();
        List<String> containerIds = new ArrayList<>();
        for(Container container : allContainers){
            containerIds.add(container.getId());
        }
        if(containerIds.size() <= 1){
            throw new IllegalStateException("No containers available to stop. " + containerIds.size() + " containers are currently running");
        }

        int randContainer = (int)Math.random() * containerIds.size();
        dockerClient.stopContainerCmd(containerIds.get(randContainer)).exec();
        return "Stopping container with id " + containerIds.get(randContainer);
    }

    public String networkDelay() throws Exception {
        List<Container> allContainers = listContainers();
        List<String> containerIds = new ArrayList<>();
        for(Container container : allContainers){
            containerIds.add(container.getId());
        }
        int randContainer = (int)Math.random() * containerIds.size();
        File location = new File("/Users/ciaratrigg/Desktop/SE 598/spring project/target systems/pg-primary-replica");
        String command = "docker exec " + containerIds.get(randContainer) +" tc qdisc add dev eth0 root netem delay 100ms";
        runCommand(location, command);
        return "Creating a network delay in container with id " + containerIds.get(randContainer);

    }

    public String containerStatus(){
        List<Container> allContainers = listContainers();
        return "Container Status " + allContainers.get(0).getStatus();
    }
}

