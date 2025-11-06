package com.thyamix;

import com.thyamix.config.BotConfig;
import com.thyamix.handlers.CommandHandler;
import com.thyamix.utils.TaskRunner;


public class DiscordBot {
    private final CommandHandler commandHandler;

    private final BotConfig config;

    public DiscordBot() {
        this.config = new BotConfig();

        this.commandHandler = new CommandHandler(this.config);

        this.config.getJda().addEventListener(this.commandHandler);
    }
}
