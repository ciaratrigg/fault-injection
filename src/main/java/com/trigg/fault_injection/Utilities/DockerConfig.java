package com.trigg.fault_injection.Utilities;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class DockerConfig {
    /*
     * Sets up the Docker connection, allowing Faultify
     * to communicate with the Docker daemon.
     */

    @Bean
    DockerClient dockerClient() {
        // Specify the location of the Docker daemon
        DockerClientConfig config = DefaultDockerClientConfig
                .createDefaultConfigBuilder()
                .withDockerHost("unix:///var/run/docker.sock")
                .build();

        // Instantiate an HTTP client
        DockerHttpClient httpClient = new ApacheDockerHttpClient
                .Builder()
                .dockerHost(config.getDockerHost())
                .build();

        // Create a Docker client using the config and httpClient
        DockerClient client = DockerClientImpl
                .getInstance(config, httpClient);

        return client;
    }
}
