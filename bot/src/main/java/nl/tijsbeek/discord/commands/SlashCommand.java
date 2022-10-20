package nl.tijsbeek.discord.commands;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import nl.tijsbeek.discord.commands.abstractions.AbstractInteractionCommand;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a Discord slash-command.
 *
 * @see AbstractInteractionCommand
 */
public interface SlashCommand extends InteractionCommand {

    /**
     * The {@link SlashCommandData} that will be registered to Discord, this instance can be modified to add options, subcommands and more.
     *
     * @return the {@link SlashCommandData} of the command
     *
     * @see SlashCommandData#addOptions(OptionData...)
     * @see SlashCommandData#addSubcommands(SubcommandData...)
     * @see SlashCommandData#addSubcommandGroups(SubcommandGroupData...)
     */
    @NotNull
    @Override
    SlashCommandData getData();

    /**
     * The {@link SlashCommandInteractionEvent} that triggers when someone runs a slash-command.
     *
     * @param event the {@link SlashCommandInteractionEvent}
     */
    void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event);


    /**
     * The {@link CommandAutoCompleteInteractionEvent} that triggers when someone is typing in an autocompletable option.
     *
     * @param event the {@link CommandAutoCompleteInteractionEvent}
     *
     * @see OptionData#setAutoComplete(boolean)
     */
    void onCommandAutoCompleteInteractionEvent(@NotNull CommandAutoCompleteInteractionEvent event);
}