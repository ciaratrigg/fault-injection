package com.trigg.fault_injection;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.Container;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import static com.trigg.fault_injection.TerminalProcess.runCommand;

@Service
public class DockerService {
    private final DockerClient dockerClient;
    private final ExecutorService executorService;
    private static final Logger LOGGER = Logger.getLogger(DockerService.class.getName());

    public DockerService(DockerClient dockerClient) {
        this.dockerClient = dockerClient;
        this.executorService = Executors.newFixedThreadPool(5); // Limit concurrency to 5 threads
    }

    // List running containers
    public List<Container> listContainers() {
        return dockerClient.listContainersCmd()
                .withShowAll(true)
                .exec();
    }

    public List<String> listContainerIds() {
        List<Container> allContainers = listContainers();
        List<String> containerIds = new ArrayList<>();
        for (Container container : allContainers) {
            containerIds.add(container.getId());
        }
        return containerIds;
    }

    // Stop a random container (Runs in a separate thread)
    public void stopContainerAsync() {
        System.out.println("Creating a new thread...");
        executorService.submit(() -> {
            try {
                List<String> containerIds = listContainerIds();
                if (containerIds.isEmpty()) {
                    LOGGER.warning("No containers available to stop.");
                    return;
                }
                int randIndex = (int) (Math.random() * containerIds.size());
                String containerId = containerIds.get(randIndex);
                dockerClient.stopContainerCmd(containerId).exec();
                LOGGER.info("Stopping container with ID: " + containerId);
            } catch (Exception e) {
                LOGGER.severe("Error stopping container: " + e.getMessage());
            }
        });
    }

    // Inject network delay into a random container (Runs in a separate thread)
    public void injectNetworkDelayAsync() {
        executorService.submit(() -> {
            try {
                List<String> containerIds = listContainerIds();
                if (containerIds.isEmpty()) {
                    LOGGER.warning("No containers available to inject network delay.");
                    return;
                }

                int randIndex = (int) (Math.random() * containerIds.size());
                String containerId = containerIds.get(randIndex);
                File location = new File("/Users/ciaratrigg/Desktop/SE 598/spring project/target systems/pg-primary-replica");
                String command = "docker exec " + containerId + " tc qdisc add dev eth0 root netem delay 100ms";
                runCommand(location, command);
                LOGGER.info("Injected network delay into container ID: " + containerId);
            } catch (Exception e) {
                LOGGER.severe("Error injecting network delay: " + e.getMessage());
            }
        });
    }

    // Restart a container (Runs in a separate thread)
    public void restartContainerAsync() {
        executorService.submit(() -> {
            try {
                List<String> containerIds = listContainerIds();
                if (containerIds.isEmpty()) {
                    LOGGER.warning("No containers available to restart.");
                    return;
                }

                int randIndex = (int) (Math.random() * containerIds.size());
                String containerId = containerIds.get(randIndex);
                dockerClient.restartContainerCmd(containerId).exec();
                LOGGER.info("Restarted container with ID: " + containerId);
            } catch (Exception e) {
                LOGGER.severe("Error restarting container: " + e.getMessage());
            }
        });
    }

    // Gracefully shut down the ExecutorService when the application stops
    public void shutdown() {
        executorService.shutdown();
    }
}
