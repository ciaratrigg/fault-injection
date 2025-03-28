package com.trigg.fault_injection.Service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

@Service
public class DockerService {
    private final DockerClient dockerClient;
    private final ExecutorService executorService;
    private static final Logger LOGGER = Logger.getLogger(DockerService.class.getName());

    @Value("${docker.tgtlabels}")
    private String targetLabels;


    public DockerService(DockerClient dockerClient) {
        this.dockerClient = dockerClient;
        this.executorService = Executors.newFixedThreadPool(5); // Limit concurrency to 5 threads
    }

    public List<Container> targetSystemContainers() {
        System.out.println("***** Filtering containers with label: " + targetLabels);

        return dockerClient.listContainersCmd()
                .withLabelFilter(Collections.singletonList(targetLabels)) // Filtering by label
                .exec();
    }

    public List<Container> listContainers() {
        return dockerClient.listContainersCmd()
                .withShowAll(true)
                .exec();
    }

    public List<String> listContainerIds() {
        List<Container> allContainers = targetSystemContainers();
        List<String> containerIds = new ArrayList<>();
        for (Container container : allContainers) {
            containerIds.add(container.getId());
        }
        return containerIds;
    }

    public void stopContainersAsync(int numNodes) {
        System.out.println("Creating a new thread...");
        executorService.submit(() -> {
            try {
                List<String> containerIds = listContainerIds();
                int totalContainers = containerIds.size();

                if (totalContainers <= 1) {
                    LOGGER.warning("Not enough containers available to stop.");
                    return;
                }

                int maxStoppable = totalContainers - 1;
                int numToStop = numNodes;
                if (numNodes > maxStoppable) {
                    LOGGER.warning("Requested to stop " + numNodes + " containers, but only " + maxStoppable + " can be stopped.");
                    numToStop = maxStoppable;
                }

                Collections.shuffle(containerIds);
                List<String> containersToStop = containerIds.subList(0, numToStop);

                for (String containerId : containersToStop) {
                    dockerClient.stopContainerCmd(containerId).exec();
                    LOGGER.info("Stopping container with ID: " + containerId);
                }
            } catch (Exception e) {
                LOGGER.severe("Error stopping containers: " + e.getMessage());
            }
        });
    }

    public void restartContainersAsync(int numNodes) {
        System.out.println("Creating a new thread...");
        executorService.submit(() -> {
            try {
                List<String> containerIds = listContainerIds();
                int totalContainers = containerIds.size();

                if (totalContainers <= 1) {
                    LOGGER.warning("Not enough containers available to restart.");
                    return;
                }

                int maxRestartable = totalContainers - 1;
                int numToStop = numNodes;
                if (numNodes > maxRestartable) {
                    LOGGER.warning("Requested to restart " + numNodes + " containers, but only " + maxRestartable + " can be restarted.");
                    numToStop = maxRestartable;
                }

                Collections.shuffle(containerIds);
                List<String> containersToRestart = containerIds.subList(0, numToStop);

                for (String containerId : containersToRestart) {
                    dockerClient.restartContainerCmd(containerId).exec();
                    LOGGER.info("Restarted container with ID: " + containerId);
                }
            } catch (Exception e) {
                LOGGER.severe("Error restarting containers: " + e.getMessage());
            }
        });
    }


    // Gracefully shut down the ExecutorService when the application stops
    public void shutdown() {
        executorService.shutdown();
    }

}
