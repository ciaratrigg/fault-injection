package com.trigg.fault_injection.Service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class DockerToxiproxyService {

    private final WebClient webClient;

    public DockerToxiproxyService() {
        this.webClient = WebClient.create("http://localhost:8474");
    }

    public Mono<String> createProxy(String name, String upstream, int listenPort) {
        return webClient.post()
                .uri("/proxies")
                .bodyValue(Map.of(
                        "name", name,
                        "listen", "0.0.0.0:" + listenPort,
                        "upstream", upstream
                ))
                .retrieve()
                .bodyToMono(String.class);
    }

    public Mono<String> applyLatency(String proxyName, int latencyMs, int jitterMs) {
        return webClient.post()
                .uri("/proxies/{proxyName}/toxics", proxyName)
                .bodyValue(Map.of(
                        "name", "latency",
                        "type", "latency",
                        "attributes", Map.of(
                                "latency", latencyMs,
                                "jitter", jitterMs
                        )
                ))
                .retrieve()
                .bodyToMono(String.class);
    }

    public Mono<String> applyBandwidthLimit(String proxyName, int rateKbps) {
        return webClient.post()
                .uri("/proxies/{proxyName}/toxics", proxyName)
                .bodyValue(Map.of(
                        "name", "bandwidth",
                        "type", "bandwidth",
                        "attributes", Map.of(
                                "rate", rateKbps
                        )
                ))
                .retrieve()
                .bodyToMono(String.class);
    }

    public Mono<String> removeToxic(String proxyName, String toxicName) {
        return webClient.delete()
                .uri("/proxies/{proxyName}/toxics/{toxicName}", proxyName, toxicName)
                .retrieve()
                .bodyToMono(String.class);
    }
}

