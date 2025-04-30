package com.trigg.fault_injection.Controller;

import com.trigg.fault_injection.Service.DockerStatsService;
import com.trigg.fault_injection.Utilities.ContainerStats;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class DockerStatsController {

    private final DockerStatsService dockerStatsService;

    public DockerStatsController(DockerStatsService dockerStatsService) {
        this.dockerStatsService = dockerStatsService;
    }

    @GetMapping(value = "/metrics", produces = MediaType.TEXT_PLAIN_VALUE)
    public String metrics() {
        Map<String, ContainerStats> stats = dockerStatsService.collectStats();

        if (stats.isEmpty()) {
            System.out.println("No container stats collected.");
            return "# No metrics available";
        }

        StringBuilder metrics = new StringBuilder();
        for (Map.Entry<String, ContainerStats> entry : stats.entrySet()) {
            String name = entry.getKey().replace("/", "");
            ContainerStats s = entry.getValue();

            metrics.append(String.format("docker_container_cpu_usage_total{container=\"%s\"} %d\n", name, s.cpuTotalUsage));
            metrics.append(String.format("docker_container_memory_usage_bytes{container=\"%s\"} %d\n", name, s.memoryUsage));
            metrics.append(String.format("docker_container_memory_limit_bytes{container=\"%s\"} %d\n", name, s.memoryLimit));
        }

        return metrics.toString();
    }

}

