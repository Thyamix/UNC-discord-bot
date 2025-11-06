package com.thyamix.tasks;

import com.thyamix.config.BotConfig;
import com.thyamix.enums.StoredType;
import com.thyamix.handlers.CommandHandler;
import com.thyamix.utils.CSVStorage;
import com.thyamix.utils.TaskRunner;

import java.util.Optional;

public class StockpileStatusTask implements Task {
    private final long hour;

    private final CSVStorage storage;
    private final TaskRunner taskRunner;
    private final CommandHandler commandHandler;

    public StockpileStatusTask(BotConfig config, CommandHandler commandHandler) {
        this.storage = new CSVStorage("RefreshChecker");
        this.commandHandler = commandHandler;
        this.taskRunner = new TaskRunner(config, this, commandHandler, storage);

        this.hour = config.getSecondsInHour();

        taskRunner.start();
    }

    public void run() {
        Optional<String[]> entryOptional = storage.getLastEntry();
        String[] entry;
        if (entryOptional.isPresent()) {
            entry = entryOptional.get();
        } else {
            return;
        }

        long now = System.currentTimeMillis() / 1000;

        long secondsAgo = now - Long.parseLong(entry[3]);

        switch (StoredType.valueOf(entry[0])) {
            case StoredType.REFRESH -> handleRefresh(secondsAgo);
            case StoredType.FIRSTPING -> handleFirstPing(secondsAgo);
            case StoredType.SECONDPING -> handleSecondPing(secondsAgo);
            case StoredType.LASTPING -> handleLastPing(secondsAgo);
        }
    }

    private void handleRefresh(long secondsAgo) {
        if (secondsAgo >= hour * 36) {
            this.commandHandler.alert("12 hours");
            storage.addEntry(StoredType.FIRSTPING, "", "Bot", System.currentTimeMillis() / 1000);
        }
    }

    private void handleFirstPing(long secondsAgo) {
        if (secondsAgo >= hour * 8) {
            this.commandHandler.alert("4 hours");
            storage.addEntry(StoredType.SECONDPING, "", "Bot", System.currentTimeMillis() / 1000);
        }
    }

    private void handleSecondPing(long secondsAgo) {
        if (secondsAgo >= hour * 3) {
            this.commandHandler.alert("1 hour");
            storage.addEntry(StoredType.LASTPING, "", "Bot", System.currentTimeMillis() / 1000);
        }
    }

    private void handleLastPing(long secondsAgo) {
        if (secondsAgo >= hour) {
            this.commandHandler.failed();
            storage.addEntry(StoredType.EXPIRED, "", "Bot", System.currentTimeMillis() / 1000);
        }
    }

    public CSVStorage getStorage() {
        return storage;
    }
}
