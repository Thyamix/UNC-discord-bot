package com.thyamix.handlers;

import com.thyamix.config.BotConfig;
import com.thyamix.enums.StoredType;
import com.thyamix.tasks.StockpileStatusTask;
import com.thyamix.utils.CSVStorage;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.util.List;

public class CommandHandler extends ListenerAdapter {
    private final BotConfig config;

    private final TextChannel refreshChannel;
    private final Role stockpileRole;
    private final Guild guild;

    private final StockpileStatusTask stockpileStatusTask;

    public CommandHandler(BotConfig config) {
        this.refreshChannel = config.getJda().getTextChannelById(config.getStockpileChannelId());
        this.stockpileRole = config.getGuild().getRoleById(config.getStockpileRoleId());
        this.guild = config.getGuild();

        this.config = config;

        this.stockpileStatusTask = new StockpileStatusTask(config, this);

        List<CommandData> commands = List.of(
                Commands.slash("refresh", "Refreshes the stockpile for foxhole.")
        );

        guild.updateCommands()
                .addCommands(commands)
                .queue();
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        switch (event.getName()) {
            case "refresh" -> handleRefresh(event);
        }
    }

    private void handleRefresh(SlashCommandInteractionEvent e) {
        if (e.getMember().getRoles().contains(this.guild.getRoleById(config.getStockpileRoleId()))) {
            e.reply("Thank you for refreshing stockpile").setEphemeral(true).queue();

            this.refreshChannel.sendMessage("Stockpile refreshed. I will ping you all once it requires refreshing again.").queue();

            this.stockpileStatusTask.getStorage().addEntry(StoredType.REFRESH, e.getUser().getId(), e.getUser().getName(), System.currentTimeMillis() / 1000);
        } else {
            e.reply("You do not have the stockpile role. You cannot refresh stockpile.").setEphemeral(true).queue();
        }
    }

    public void alert(String timeLeft) {
        this.refreshChannel.sendMessage(this.stockpileRole.getAsMention() + String.format("You need to refresh the stockpile. You have %s left", timeLeft)).queue();
    }

    public void failed() {
        this.refreshChannel.sendMessage(this.stockpileRole.getAsMention() + "You have failed to refresh stockpile. It has now likely expired.").queue();
    }
}
