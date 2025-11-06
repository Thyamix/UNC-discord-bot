package com.thyamix.utils;

import com.thyamix.config.BotConfig;
import com.thyamix.enums.StoredType;
import com.thyamix.handlers.CommandHandler;

import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TaskRunner {
    private final long hour;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final CSVStorage storage;
    private final CommandHandler commandHandler;

    private final BotConfig config;

    public TaskRunner(BotConfig config, CommandHandler commandHandler, CSVStorage storage) {
        this.storage = storage;
        this.commandHandler = commandHandler;
        this.config = config;

        this.hour = config.getSecondsInHour();
    }

    public void start() {
        scheduler.scheduleAtFixedRate(() -> {
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

    }, 0, this.config.getPollingRate(), TimeUnit.SECONDS);
    }

    private void handleRefresh(long secondsAgo) {
        if (secondsAgo >= hour * 36) {
            commandHandler.alert("12 hours");
            storage.addEntry(StoredType.FIRSTPING, "", "Bot", System.currentTimeMillis() / 1000);
        }
    }
    private void handleFirstPing(long secondsAgo) {
        if (secondsAgo >= hour * 8) {
            commandHandler.alert("4 hours");
            storage.addEntry(StoredType.SECONDPING, "", "Bot", System.currentTimeMillis() / 1000);
        }
    }
    private void handleSecondPing(long secondsAgo) {
        if (secondsAgo >= hour * 3) {
            commandHandler.alert("1 hour");
            storage.addEntry(StoredType.LASTPING, "", "Bot", System.currentTimeMillis() / 1000);
        }
    }
    private void handleLastPing(long secondsAgo) {
        if (secondsAgo >= hour) {
            commandHandler.failed();
            storage.addEntry(StoredType.EXPIRED, "", "Bot", System.currentTimeMillis() / 1000);
        }
    }

    public void stop() {
        scheduler.shutdownNow();
    }
}
