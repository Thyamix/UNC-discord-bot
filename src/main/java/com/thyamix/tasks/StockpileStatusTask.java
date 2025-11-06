package com.thyamix.tasks;

import com.thyamix.config.BotConfig;
import com.thyamix.enums.StoredType;
import com.thyamix.handlers.CommandHandler;
import com.thyamix.utils.CSVStorage;
import com.thyamix.utils.TaskRunner;

import java.util.Objects;
import java.util.Optional;

public class StockpileStatusTask implements Task {
    private final long hourInSeconds; //used for testing. When running should always be 3600 for seconds in 1 hour.

    private final CSVStorage refreshStorage;
    private final CSVStorage refreshAlertStorage;
    private final CommandHandler commandHandler;
    private final TaskRunner taskRunner;

    public StockpileStatusTask(BotConfig config, CommandHandler commandHandler) {
        this.refreshStorage = new CSVStorage("RefreshStorage");
        this.refreshAlertStorage = new CSVStorage("RefreshAlertStorage");
        this.commandHandler = commandHandler;
        this.taskRunner = new TaskRunner(config, this, commandHandler, refreshStorage);
        this.hourInSeconds = config.getSecondsInHour();

        this.taskRunner.start();
    }

    public void run() {
        Optional<String[]> entryOptional = refreshStorage.getLastEntry();
        String[] refreshEntry;
        if (entryOptional.isPresent()) {
            refreshEntry = entryOptional.get();
        } else {
            return;
        }

        long now = System.currentTimeMillis() / 1000;

        long secondsAgo = now - Long.parseLong(refreshEntry[3]);

        String[] refreshAlertEntry;
        entryOptional = refreshAlertStorage.getLastEntry();
        if (entryOptional.isPresent()) {
            refreshAlertEntry = entryOptional.get();
            if (Objects.equals(refreshAlertEntry[0], "type")) {
                handleRefresh(secondsAgo);
            }
            if (Long.parseLong(refreshAlertEntry[3]) > Long.parseLong(refreshEntry[3])) {
                switch (StoredType.valueOf(refreshAlertEntry[0])) {
                    case StoredType.FIRSTPING -> handleFirstPing(secondsAgo);
                    case StoredType.SECONDPING -> handleSecondPing(secondsAgo);
                    case StoredType.LASTPING -> handleLastPing(secondsAgo);
                }
                return;
            }
        }
        handleRefresh(secondsAgo);
    }

    private void handleRefresh(long secondsAgo) {
        if (secondsAgo >= hourInSeconds * 36 && secondsAgo < hourInSeconds * 44) {
            this.commandHandler.alert("less than 12 hours");
            refreshAlertStorage.addEntry(StoredType.FIRSTPING, "", "Bot", System.currentTimeMillis() / 1000);
        }
        if (secondsAgo >= hourInSeconds * 44 && secondsAgo < hourInSeconds * 47) {
            this.commandHandler.alert("less than 4 hours");
            refreshAlertStorage.addEntry(StoredType.SECONDPING, "", "Bot", System.currentTimeMillis() / 1000);
        }
        if (secondsAgo >= hourInSeconds * 47 && secondsAgo < hourInSeconds * 48) {
            this.commandHandler.alert("less than one 1 hour");
            refreshAlertStorage.addEntry(StoredType.LASTPING, "", "Bot", System.currentTimeMillis() / 1000);
        }
        if (secondsAgo >= 48) {
            this.commandHandler.failed();
            refreshAlertStorage.addEntry(StoredType.EXPIRED, "", "Bot", System.currentTimeMillis() / 1000);
        }
    }

    private void handleFirstPing(long secondsAgo) {
        if (secondsAgo >= hourInSeconds * 44 && secondsAgo < hourInSeconds * 47) {
            this.commandHandler.alert("less than 4 hours");
            refreshAlertStorage.addEntry(StoredType.SECONDPING, "", "Bot", System.currentTimeMillis() / 1000);
        }
        if (secondsAgo >= hourInSeconds * 47 && secondsAgo < hourInSeconds * 48) {
            this.commandHandler.alert("less than one 1 hour");
            refreshAlertStorage.addEntry(StoredType.LASTPING, "", "Bot", System.currentTimeMillis() / 1000);
        }
        if (secondsAgo >= 48) {
            this.commandHandler.failed();
            refreshAlertStorage.addEntry(StoredType.EXPIRED, "", "Bot", System.currentTimeMillis() / 1000);
        }
    }

    private void handleSecondPing(long secondsAgo) {
        if (secondsAgo >= hourInSeconds * 47 && secondsAgo < hourInSeconds * 48) {
            this.commandHandler.alert("less than one 1 hour");
            refreshAlertStorage.addEntry(StoredType.LASTPING, "", "Bot", System.currentTimeMillis() / 1000);
        }
        if (secondsAgo >= 48) {
            this.commandHandler.failed();
            refreshAlertStorage.addEntry(StoredType.EXPIRED, "", "Bot", System.currentTimeMillis() / 1000);
        }
    }

    private void handleLastPing(long secondsAgo) {
        if (secondsAgo >= 48) {
            this.commandHandler.failed();
            refreshAlertStorage.addEntry(StoredType.EXPIRED, "", "Bot", System.currentTimeMillis() / 1000);
        }
    }

    public CSVStorage getRefreshStorage() {
        return refreshStorage;
    }
}
