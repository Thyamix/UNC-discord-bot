package com.thyamix.tasks;

import com.thyamix.config.BotConfig;
import com.thyamix.handlers.CommandHandler;
import com.thyamix.utils.CSVStorage;
import com.thyamix.utils.TaskRunner;

public class StockpileStatusTask {
    private final CSVStorage storage;
    private final TaskRunner taskRunner;

    public StockpileStatusTask(BotConfig config, CommandHandler commandHandler) {
        this.storage = new CSVStorage("RefreshChecker");

        this.taskRunner = new TaskRunner(config, commandHandler, storage);

        taskRunner.start();
    }

    public CSVStorage getStorage() {
        return storage;
    }
}
