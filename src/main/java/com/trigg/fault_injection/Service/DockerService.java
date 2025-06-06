package com.trigg.fault_injection.Service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.Container;
import com.trigg.fault_injection.Utilities.FaultLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Logger;

@Service
public class DockerService {

    /*
     * Service class which uses the Docker Client to inject faults into the target system.
     */

    private DockerClient dockerClient;
    private ExecutorService executorService;
    private ScheduledExecutorService scheduler;
    private List<ScheduledJob> scheduledJobs;
    private ToxiproxyService toxiproxyService;
    private FaultLog faultLog;
    private static final Logger LOGGER = Logger.getLogger(DockerService.class.getName());

    @Value("${docker.network}")
    private String targetNetwork;

    @Value("${toxiport}")
    private String listen;

    @Value("${upstreamport}")
    private String upstream;

    @Value("${docker.tgtlabel}")
    private String targetLabel;

    @Autowired
    public DockerService(DockerClient dockerClient, ToxiproxyService toxiproxyService, FaultLog faultLog) throws IOException {
        this.dockerClient = dockerClient;
        this.executorService = Executors.newFixedThreadPool(5);
        this.scheduler = Executors.newScheduledThreadPool(5);
        this.scheduledJobs = Collections.synchronizedList(new ArrayList<>());
        this.toxiproxyService = toxiproxyService;
        this.faultLog = faultLog;
    }

    public List<Container> targetSystemContainers() {
        System.out.println("Filtering containers with label: " + targetLabel);

        return dockerClient.listContainersCmd()
                .withLabelFilter(Collections.singletonList(targetLabel))
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

    public void stopContainers(int num_nodes, Duration duration) {

        executorService.submit(() -> {
            try {
                List<String> ids = listContainerIds();
                if (ids.size() <= 1) return;

                int maxStoppable = Math.min(num_nodes, ids.size() - 1);
                Collections.shuffle(ids);
                List<String> toStop = ids.subList(0, maxStoppable);

                for (String id : toStop) {
                    dockerClient.stopContainerCmd(id).exec();
                    faultLog.addEvent("node-crash", id);
                    LOGGER.info("Stopped container: " + id);
                }

                Thread.sleep(duration.toMillis());

                for (String id : toStop) {
                    try {
                        dockerClient.startContainerCmd(id).exec();
                        LOGGER.info("Restarted container: " + id);
                    } catch (Exception e) {
                        LOGGER.warning("Failed to restart container: " + id);
                    }
                }
            } catch (Exception e) {
                LOGGER.severe("Error in stopContainers: " + e.getMessage());
            }
        });
    }

    public void restartContainers(int num_nodes, int frequencySeconds, Duration duration) {
        executorService.submit(() -> {
            try {
                List<String> ids = listContainerIds();
                if (ids.size() <= 1 || frequencySeconds <= 0) return;

                int maxRestartable = Math.min(num_nodes, ids.size() - 1);
                Collections.shuffle(ids);
                List<String> toRestart = ids.subList(0, maxRestartable);

                long endTime = System.currentTimeMillis() + duration.toMillis();

                while (System.currentTimeMillis() < endTime) {
                    for (String id : toRestart) {
                        try {
                            dockerClient.restartContainerCmd(id).exec();
                            faultLog.addEvent("node-restart", id);
                            LOGGER.info("Restarted container: " + id);
                        } catch (Exception e) {
                            LOGGER.warning("Failed to restart container: " + id);
                        }
                    }
                    Thread.sleep(frequencySeconds * 1000L);
                }

            } catch (Exception e) {
                LOGGER.severe("Error in restartContainers: " + e.getMessage());
            }
        });
    }


    public void cpuStressSidecar(int num_nodes, Duration duration) {
        executorService.submit(() -> {
            List<String> sidecarIds = new ArrayList<>();

            try {
                for (int i = 0; i < num_nodes; i++) {
                    CreateContainerResponse container = dockerClient.createContainerCmd("busybox")
                            .withCmd("sh", "-c", "while true; do :; done")
                            .withNetworkMode(targetNetwork)
                            .exec();

                    String containerId = container.getId();
                    dockerClient.startContainerCmd(containerId).exec();
                    faultLog.addEvent("cpu-stress-sc", containerId);
                    sidecarIds.add(containerId);
                    LOGGER.info("Started CPU stress sidecar: " + containerId);
                }

                Thread.sleep(duration.toMillis());

            } catch (Exception e) {
                LOGGER.severe("Error starting CPU stress sidecars: " + e.getMessage());
            } finally {
                for (String containerId : sidecarIds) {
                    try {
                        dockerClient.stopContainerCmd(containerId).exec();
                        dockerClient.removeContainerCmd(containerId).exec();
                        LOGGER.info("Stopped and removed CPU stress sidecar: " + containerId);
                    } catch (Exception e) {
                        LOGGER.warning("Failed to stop/remove CPU stress sidecar: " + containerId);
                    }
                }
            }
        });
    }

    public void createProxy(String proxyName, String listen, String upstream) throws IOException {
        toxiproxyService.createProxy(proxyName, listen, upstream);
    }

    public void networkDelay(String proxyName, long latency, Duration duration) {
        executorService.submit(() -> {
            try {
                toxiproxyService.createProxy(proxyName, listen, upstream);
                toxiproxyService.addLatency(proxyName, latency);
                faultLog.addEvent("network-delay", proxyName);
                Thread.sleep(duration.toMillis());
                toxiproxyService.removeToxic(proxyName, "latency");
                toxiproxyService.deleteProxy(proxyName);
            } catch (Exception e) {
                LOGGER.severe("Error simulating network latency: " + e.getMessage());
            }
        });
    }

    public void bandwidthThrottle(String proxyName, long rate, Duration duration) {
        executorService.submit(() -> {
            try {
                toxiproxyService.createProxy(proxyName, listen, upstream);
                toxiproxyService.addBandwidth(proxyName, rate);
                faultLog.addEvent("bandwidth-throttle", proxyName);
                Thread.sleep(duration.toMillis());
                toxiproxyService.removeToxic(proxyName, "bandwidth");
                toxiproxyService.deleteProxy(proxyName);
            } catch (Exception e) {
                LOGGER.severe("Error simulating bandwidth throttling: " + e.getMessage());
            }
        });
    }

    public void scheduleFault(String faultName, String fault_type, int delaySeconds, int durationSeconds, int frequency, int num_nodes, String proxyName, long latencyRateOrPercent ) {
        Instant startTime = Instant.now().plusSeconds(delaySeconds);
        Duration duration = Duration.ofSeconds(durationSeconds);

        Runnable task = () -> {
            LOGGER.info("Executing fault: " + faultName);

            switch (fault_type.toLowerCase()) {
                case "node-crash":
                    stopContainers(num_nodes, duration);
                    break;
                case "node-restart":
                    restartContainers(num_nodes, frequency, duration);
                    break;
                case "cpu-stress-sc":
                    cpuStressSidecar(num_nodes, duration);
                    break;
                case "network-delay":
                    networkDelay(proxyName, latencyRateOrPercent, duration);
                    break;
                case "bandwidth-throttle":
                    bandwidthThrottle(proxyName, latencyRateOrPercent , duration);
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

        @Override
        public String toString() {
            return String.format(
                    "Fault Name     : %s%n" +
                            "Scheduled Time : %s%n" +
                            "Duration       : %s%n" +
                            "Done           : %s%n" +
                            "Cancelled      : %s",
                    faultName,
                    scheduledTime,
                    duration,
                    isDone(),
                    isCancelled()
            );
        }

    }
}
