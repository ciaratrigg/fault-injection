package com.trigg.fault_injection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.sql.Date;

@Service
public class DynamicJobScheduler {
    private final TaskScheduler taskScheduler;

    @Autowired
    public DynamicJobScheduler(TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
    }

    public void scheduleJob(Runnable job, Date scheduleTime) {
        taskScheduler.schedule(job, scheduleTime);
    }
}
