package nl.tijsbeek.discord.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.jetbrains.annotations.NotNull;

public interface SlashCommand extends InteractionCommand {

    @NotNull
    @Override
    SlashCommandData getData();

    void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event);
}