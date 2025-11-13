package com.thyamix.utils;

import com.thyamix.config.BotConfig;
import com.thyamix.tasks.Task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TaskRunner {
    private static final Logger LOG = LoggerFactory.getLogger(TaskRunner.class);

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    private final BotConfig config;
    private final Task task;

    public TaskRunner(BotConfig config, Task task) {
        this.config = config;
        this.task = task;
    }

    public void start() {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                this.task.run();
            } catch (Exception ex) {
                LOG.error("Scheduled task failed", ex);
            }
        }, 0, this.config.getPollingRate(), TimeUnit.SECONDS);
    }

    public void stop() {
        scheduler.shutdownNow();
    }
}
