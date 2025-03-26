package com.trigg.fault_injection.Service;

import org.springframework.scheduling.annotation.Scheduled;

public class FaultInjectionService {
    // This method will run every minute (you can adjust the cron expression or fixedDelay/fixedRate as needed)
    @Scheduled(fixedRate = 60000)
    public void injectFault() {
        // Simulate fault injection logic
        System.out.println("Injecting fault at: " + System.currentTimeMillis());
        // You can implement specific fault injection tasks here
    }

    // Alternatively, you can use cron expressions for specific time-based scheduling
    @Scheduled(cron = "0 0 12 * * ?") // This runs every day at noon
    public void injectFaultAtNoon() {
        System.out.println("Injecting fault at noon: " + System.currentTimeMillis());
        // Inject a fault at noon logic
    }
}
