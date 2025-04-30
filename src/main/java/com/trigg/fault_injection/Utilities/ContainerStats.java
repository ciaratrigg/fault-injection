package com.trigg.fault_injection.Utilities;

import com.github.dockerjava.api.model.Statistics;

public class ContainerStats {
    public final long cpuTotalUsage;
    public final long memoryUsage;
    public final long memoryLimit;

    public ContainerStats(Statistics stats) {
        this.cpuTotalUsage = stats.getCpuStats().getCpuUsage().getTotalUsage();
        this.memoryUsage = stats.getMemoryStats().getUsage();
        this.memoryLimit = stats.getMemoryStats().getLimit();
    }
}
