# Faultify

**Faultify** is an automated fault injection tool designed to test the resilience of distributed systems by simulating real-world failure scenarios. Built with Java and the Spring Framework, and leveraging Docker for deployment and fault execution, Faultify allows you to define, inject, and monitor a wide range of faults (e.g., CPU stress, network delays, node crashes) in any Dockerized application.

## Why Faultify?

Modern distributed systems are powerful but inherently complex and prone to failure. Chaos engineering is a proactive approach to uncovering system weaknesses. Faultify supports this by offering:

- Fine-grained fault definitions
- Container-aware fault targeting
- Real-time monitoring capabilities
- A built-in interactive shell
- Easy integration into existing containerized deployments

---

## Getting Started

### 1. Clone the Repository

```
git clone https://github.com/your-username/faultify.git
cd faultify
```

### 2. To build and start the application:
```
docker compose up --build
```
The application will be available at http://localhost:8080.

### 3. Configuration
Before injecting faults, Faultify needs to know details about your target system.
You must update the application.properties to match your Docker environment. A 
sample config file is provided below:

```
# Docker target label used to identify containers
docker.tgtlabel=my_target_label

# Docker network the target system runs on
docker.network=my_docker_network

# Port Toxiproxy listens on
docker.toxiport=8474

# The port of the upstream service inside the container
docker.upstreamport=5432
```
Each container in your target system that you wish to target must include the label specified in docker.tgtlabel.

For more information about Toxiproxy and network faults see the Toxiproxy repository [here](https://github.com/Shopify/toxiproxy). 

### 4. Accessing the integrated shell
Faultify includes an interactive Spring Shell interface for defining and managing faults.

1. Open a new terminal window and attach to the running container:
```
docker attach faultinjection
```
2. Type ```help``` to list all available commands and their descriptions.

### 5. Fault Definitions

Below are the currently supported fault types and how to define them from the shell interface:

1. ```cpu-stress-sc``` - CPU Stress Sidecar

Description: launches a resource-intensive dummy container 

Example:
```
define cpu-stress-sc --name high-cpu --duration 30 --numThreads 4
```
2. ```node-restart``` - Repeated Node Restart

Description: repeatedly restarts one or more containers

Example:
```
define node-restart --name unstable-nodes --duration 60 --numNodes 2 --frequency 15
```
3. ```node-crash``` - Stop nodes

Description: kills one or more containers 

Example:
```
define node-crash --name crash-test --duration 30 --numNodes 3
```
4. ```network-delay``` - Network Latency

Description: adds artificial latency to container network traffic.

Example:
```
define network-delay --name delay-net --duration 45 --latency 1000
```
5. ```bandwidth-throttle``` - Throttle Bandwidth

Description: limits container network bandwidth.

Example:
```
define bandwidth-throttle --name limit-band --duration 30 --rate 512
```

### 6. Fault Injection
Once faults have been defined, you can schedule them for execution using 
the following command: 
```
inject --name fault-name --scheduled_for 60
```
The scheduled_for parameter represents the seconds after submission that the 
fault will be executed given a thread is available. Otherwise, the job will wait
to execute until a thread becomes available. 

### 7. Tips 
- Run ```help [command]``` to view extended details of any shell command.
- Make sure the specified target system is running (duh! :) )
- Faults can be stacked or chained for complex scenarios using a shell script (see ```script``` command). 
- Use the web dashboard (accessible at port 8080) to visualize faults in real time. 

