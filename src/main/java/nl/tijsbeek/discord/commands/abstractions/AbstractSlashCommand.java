package nl.tijsbeek.discord.commands.abstractions;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import nl.tijsbeek.discord.commands.InteractionCommandState;
import nl.tijsbeek.discord.commands.InteractionCommandVisibility;
import nl.tijsbeek.discord.commands.SlashCommand;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractSlashCommand extends AbstractInteractionCommand implements SlashCommand {
    private final SlashCommandData commandData;

    protected AbstractSlashCommand(@NotNull final SlashCommandData commandData, @NotNull final InteractionCommandVisibility visibility, @NotNull final InteractionCommandState state) {
        super(commandData, visibility, state);

        this.commandData = commandData;
    }

    @Override
    @SuppressWarnings("SuspiciousGetterSetter")
    public @NotNull SlashCommandData getData() {
        return commandData;
    }

    @Override
    public void onCommandAutoCompleteInteractionEvent(@NotNull CommandAutoCompleteInteractionEvent event) {
        
    }
}