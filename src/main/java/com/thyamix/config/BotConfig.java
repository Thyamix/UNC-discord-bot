package com.thyamix.config;

import com.moandjiezana.toml.Toml;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class BotConfig {
    private static final String BOT_TOKEN = System.getenv("BOT_TOKEN");

    private final String guildId;
    private final String stockpileRoleId;
    private final String stockpileChannelId;

    private final long pollingRate;
    private final long secondsInHour;

    private final @NotNull JDA jda;
    private final @NotNull Guild guild;

    public BotConfig() {
        Toml toml = new Toml().read(new File("config.toml"));

        this.guildId = toml.getString("guild_id");
        this.stockpileRoleId = toml.getString("roles.stockpile_id");
        this.stockpileChannelId = toml.getString("channels.stockpile_id");

        this.pollingRate = toml.getLong("testing.polling");
        this.secondsInHour = toml.getLong("testing.hour");

        if (BOT_TOKEN == null || BOT_TOKEN.isEmpty()) {
            throw new NullPointerException("Bot token is null or empty");
        }

        try {
            this.jda = JDABuilder.createDefault(BOT_TOKEN).build().awaitReady();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        this.guild = jda.getGuildById(this.getGuildId());
    }

    public long getPollingRate() {
        return pollingRate;
    }

    public long getSecondsInHour() {
        return secondsInHour;
    }

    public String getGuildId() {
        return guildId;
    }

    @NotNull
    public Guild getGuild() {
        return guild;
    }

    public String getStockpileRoleId() {
        return stockpileRoleId;
    }

    public String getStockpileChannelId() {
        return stockpileChannelId;
    }

    @NotNull
    public JDA getJda() {
        return jda;
    }
}
