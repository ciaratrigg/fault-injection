package com.trigg.fault_injection;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/toxiproxy")
public class DockerToxiproxyController {

    private final DockerToxiproxyService toxiproxyService;

    public DockerToxiproxyController(DockerToxiproxyService toxiproxyService) {
        this.toxiproxyService = toxiproxyService;
    }
/*
    @PostMapping("/proxy")
    public Mono<String> createProxy(@RequestParam String name, @RequestParam String upstream, @RequestParam int listenPort) {
        return toxiproxyService.createProxy(name, upstream, listenPort);
    }

    @PostMapping("/{proxyName}/latency")
    public Mono<String> applyLatency(@PathVariable String proxyName, @RequestParam int latencyMs, @RequestParam int jitterMs) {
        return toxiproxyService.applyLatency(proxyName, latencyMs, jitterMs);
    }

    @PostMapping("/{proxyName}/bandwidth")
    public Mono<String> applyBandwidth(@PathVariable String proxyName, @RequestParam int rateKbps) {
        return toxiproxyService.applyBandwidthLimit(proxyName, rateKbps);
    }

    @DeleteMapping("/{proxyName}/toxics/{toxicName}")
    public Mono<String> removeToxic(@PathVariable String proxyName, @PathVariable String toxicName) {
        return toxiproxyService.removeToxic(proxyName, toxicName);
    }

    @GetMapping("/api/test")
    public String testEndpoint() {
        return "Hello, world!";
    }*/
}

