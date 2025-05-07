package com.trigg.fault_injection.Service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.*;
import com.trigg.fault_injection.Model.ContainerStatusDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContainerInfoService {

    private final DockerClient dockerClient;

    @Value("${docker.tgtlabel}")
    private String targetLabel;

    @Autowired
    public ContainerInfoService(DockerClient dockerClient) {
        this.dockerClient = dockerClient;
    }

    public List<ContainerStatusDTO> getContainerStatuses() {
        return dockerClient.listContainersCmd()
                .withShowAll(true)
                .withLabelFilter(Collections.singletonList(targetLabel))
                .exec()
                .stream()
                .map(container -> {
                    String id = container.getId();
                    String name = container.getNames()[0].replace("/", "");
                    String status = container.getStatus();

                    return new ContainerStatusDTO(id, name, status);
                })

                .collect(Collectors.toList());
    }

}

