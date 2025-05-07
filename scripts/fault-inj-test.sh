define node-crash "single-crash" 60 1

define node-restart "restart1" 120 2 10

define cpu-stress-sc "2cpu-sc" 180 2

define network-delay "latency-sim" 120 500

define bandwidth-throttle "throtttle-bandw" 180 1024

inject "single-crash" 0
inject "restart1" 30
inject "2cpu-sc" 60
inject "latency-sim" 90
inject "throtttle-bandw" 120

list-faults

list-jobs
