package com.thyamix.utils;

import com.thyamix.config.BotConfig;
import com.thyamix.enums.StoredType;
import com.thyamix.handlers.CommandHandler;
import com.thyamix.tasks.Task;

import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TaskRunner {
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final CSVStorage storage;
    private final CommandHandler commandHandler;

    private final BotConfig config;
    private final Task task;

    public TaskRunner(BotConfig config, Task task, CommandHandler commandHandler, CSVStorage storage) {
        this.storage = storage;
        this.commandHandler = commandHandler;
        this.config = config;
        this.task = task;
    }

    public void start() {
        scheduler.scheduleAtFixedRate(this.task::run, 0, this.config.getPollingRate(), TimeUnit.SECONDS);
    }

    public void stop() {
        scheduler.shutdownNow();
    }
}
