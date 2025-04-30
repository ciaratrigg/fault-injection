package com.trigg.fault_injection.Service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.Container;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Logger;

@Service
public class DockerService {

    private final DockerClient dockerClient;
    private final ExecutorService executorService;
    private final ScheduledExecutorService scheduler;
    private final List<ScheduledJob> scheduledJobs;

    private static final Logger LOGGER = Logger.getLogger(DockerService.class.getName());

    @Value("${docker.network}")
    private String targetNetwork;

    @Value("${docker.tgtlabels}")
    private String targetLabels;

    @Value("${spring.application.name}")
    private String appName;

    public DockerService(DockerClient dockerClient) {
        this.dockerClient = dockerClient;
        this.executorService = Executors.newFixedThreadPool(5);
        this.scheduler = Executors.newScheduledThreadPool(5);
        this.scheduledJobs = Collections.synchronizedList(new ArrayList<>());
    }

    public List<Container> targetSystemContainers() {
        return dockerClient.listContainersCmd()
                .withLabelFilter(Collections.singletonList(targetLabels))
                .exec();
    }

    public List<Container> listContainers() {
        return dockerClient.listContainersCmd()
                .withShowAll(true)
                .exec();
    }

    public List<String> listContainerIds() {
        List<Container> containers = targetSystemContainers();
        List<String> ids = new ArrayList<>();
        for (Container c : containers) {
            ids.add(c.getId());
        }
        return ids;
    }

    public void stopContainersAsync(int numNodes, int duration) {
        executorService.submit(() -> {
            try {
                List<String> allRunningContainerIds = listContainerIds(); // Use your method

                int maxStoppable = Math.min(numNodes, allRunningContainerIds.size() - 1);
                if (maxStoppable <= 0) {
                    System.out.println("Not enough containers to stop safely.");
                    return;
                }

                List<String> containerIdsToStop = allRunningContainerIds.subList(0, maxStoppable);

                for (String id : containerIdsToStop) {
                    dockerClient.stopContainerCmd(id).exec();
                    System.out.println("Stopped container: " + id);
                }

                scheduler.schedule(() -> {
                    for (String id : containerIdsToStop) {
                        try {
                            dockerClient.startContainerCmd(id).exec();
                            System.out.println("Restarted container: " + id);
                        } catch (Exception e) {
                            System.err.println("Failed to restart container: " + id);
                            e.printStackTrace();
                        }
                    }
                }, duration, TimeUnit.SECONDS);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    public void restartContainers(Duration duration) {
        executorService.submit(() -> {
            try {
                List<String> ids = listContainerIds();
                if (ids.size() <= 1) return;

                Collections.shuffle(ids);
                List<String> toRestart = ids.subList(0, ids.size() - 1);
                for (String id : toRestart) {
                    dockerClient.restartContainerCmd(id).exec();
                    LOGGER.info("Restarted container: " + id);
                }

                // No real reversal needed, just wait out the duration
                Thread.sleep(duration.toMillis());
            } catch (Exception e) {
                LOGGER.severe("Error in restartContainers: " + e.getMessage());
            }
        });
    }

    public void cpuStressSidecar(Duration duration) {
        executorService.submit(() -> {
            CreateContainerResponse container = dockerClient.createContainerCmd("busybox")
                    .withCmd("sh", "-c", "while true; do :; done")
                    .withNetworkMode(targetNetwork)
                    .exec();

            String containerId = container.getId();
            dockerClient.startContainerCmd(containerId).exec();
            LOGGER.info("Started CPU stress sidecar: " + containerId);

            try {
                Thread.sleep(duration.toMillis());
            } catch (InterruptedException ignored) {}

            dockerClient.stopContainerCmd(containerId).exec();
            dockerClient.removeContainerCmd(containerId).exec();
            LOGGER.info("Stopped and removed CPU stress sidecar: " + containerId);
        });
    }

    public void scheduleFault(String faultName, int delaySeconds, int durationSeconds) {
        Instant startTime = Instant.now().plusSeconds(delaySeconds);
        Duration duration = Duration.ofSeconds(durationSeconds);

        Runnable task = () -> {
            LOGGER.info("Executing fault: " + faultName);

            switch (faultName.toLowerCase()) {
                case "stop":
                    stopContainers(duration);
                    break;
                case "restart":
                    restartContainers(duration);
                    break;
                case "cpu":
                    cpuStressSidecar(duration);
                    break;
                default:
                    LOGGER.warning("Unknown fault: " + faultName);
            }
        };

        Future<?> future = scheduler.schedule(task, delaySeconds, TimeUnit.SECONDS);
        scheduledJobs.add(new ScheduledJob(faultName, startTime, duration, future));
    }

    public List<ScheduledJob> getScheduledJobs() {
        return new ArrayList<>(scheduledJobs);
    }

    public void connectToTgtNetwork() {
        try {
            dockerClient.connectToNetworkCmd()
                    .withContainerId(appName)
                    .withNetworkId(targetNetwork)
                    .exec();
            LOGGER.info("Connected to network: " + targetNetwork);
        } catch (Exception e) {
            LOGGER.severe("Failed to connect to network: " + e.getMessage());
        }
    }

    public void disconnectFromTgtNetwork() {
        try {
            dockerClient.disconnectFromNetworkCmd()
                    .withContainerId(appName)
                    .withNetworkId(targetNetwork)
                    .exec();
            LOGGER.info("Disconnected from network: " + targetNetwork);
        } catch (Exception e) {
            LOGGER.severe("Failed to disconnect from network: " + e.getMessage());
        }
    }

    public void shutdown() {
        executorService.shutdown();
        scheduler.shutdown();
    }

    // Inner class to represent scheduled jobs
    public static class ScheduledJob {
        private final String faultName;
        private final Instant scheduledTime;
        private final Duration duration;
        private final Future<?> future;

        public ScheduledJob(String faultName, Instant scheduledTime, Duration duration, Future<?> future) {
            this.faultName = faultName;
            this.scheduledTime = scheduledTime;
            this.duration = duration;
            this.future = future;
        }

        public String getFaultName() {
            return faultName;
        }

        public Instant getScheduledTime() {
            return scheduledTime;
        }

        public Duration getDuration() {
            return duration;
        }

        public boolean isDone() {
            return future.isDone();
        }

        public boolean isCancelled() {
            return future.isCancelled();
        }
    }
}
