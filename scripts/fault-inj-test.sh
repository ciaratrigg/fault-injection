# Define a Node Crash Fault
define node-crash "single-crash" 60 1

# Define a Node Restart Fault with frequency
define node-restart "restart1" 120 2 10

# Define CPU Stress Fault for sidecar containers
define cpu-stress-sc "2cpu-sc" 180 2

# Define a Network Delay Fault
define network-delay "latency-sim" 120 500

# Define a Bandwidth Throttle Fault
define bandwidth-throttle "throtttle-bandw" 180 1024

# Inject faults with specific delays
inject "single-crash" 0
inject "restart1" 30
inject "2cpu-sc" 60
inject "latency-sim" 90
inject "throtttle-bandw" 120

# List all defined faults
list-faults

# List all current and previous jobs
list-jobs
