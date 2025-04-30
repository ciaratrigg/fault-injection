package com.trigg.fault_injection.Service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.StatsCmd;
import com.github.dockerjava.api.model.Statistics;
import com.trigg.fault_injection.Utilities.ContainerStats;
import com.trigg.fault_injection.Utilities.StatsCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class DockerStatsService {
    private DockerClient dockerClient;

    @Autowired
    public DockerStatsService(DockerClient dockerClient){
        this.dockerClient = dockerClient;
    }

    public Map<String, ContainerStats> collectStats() {
        Map<String, ContainerStats> statsMap = new HashMap<>();

        dockerClient.listContainersCmd().exec().forEach(container -> {
            String id = container.getId();
            String name = container.getNames()[0];

            try (StatsCmd statsCmd = dockerClient.statsCmd(id)) {
                StatsCallback callback = new StatsCallback();
                statsCmd.exec(callback);
                Statistics stats = callback.getFuture().get(5, TimeUnit.SECONDS);
            } catch (Exception e) {
                System.err.println("Error collecting stats for container " + name + ": " + e.getMessage());
            }
        });

        return statsMap;
    }
}
