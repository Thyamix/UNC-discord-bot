package com.thyamix;

import com.thyamix.handlers.CommandHandler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;

public class DiscordBot {
    private static final String BOT_TOKEN = System.getenv("BOT_TOKEN");
    private static final String GUILD_ID = System.getenv("GUILD_ID");

    private final @NotNull JDA jda;
    private final @NotNull Guild guild;
    private final CommandHandler commandHandler;

    public DiscordBot() {
        if (BOT_TOKEN == null || BOT_TOKEN.isEmpty()) {
            throw new NullPointerException("Bot token is null or empty");
        }

        try {
            this.jda = JDABuilder.createDefault(BOT_TOKEN).build().awaitReady();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        this.guild = jda.getGuildById(GUILD_ID);

        this.commandHandler = new CommandHandler(guild);

        jda.addEventListener(this.commandHandler);
    }
}
