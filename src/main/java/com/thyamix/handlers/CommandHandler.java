package com.thyamix.handlers;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.util.List;

public class CommandHandler extends ListenerAdapter {
    public CommandHandler(Guild guild) {
        List<CommandData> commands = List.of(
                Commands.slash("refresh", "Refreshes the stockpile for foxhole.")
        );

        guild.updateCommands()
                .addCommands(commands)
                .queue();

        System.out.println("test");
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        System.out.println("test");
        switch (event.getName()) {
            case "refresh" -> handleRefresh(event);
        }
    }

    private void handleRefresh(SlashCommandInteractionEvent e) {

        e.reply("test").queue();
    }
}
