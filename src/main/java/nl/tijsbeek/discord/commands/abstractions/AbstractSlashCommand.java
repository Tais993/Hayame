package nl.tijsbeek.discord.commands.abstractions;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import nl.tijsbeek.discord.commands.InteractionCommandState;
import nl.tijsbeek.discord.commands.InteractionCommandVisibility;
import nl.tijsbeek.discord.commands.SlashCommand;
import org.jetbrains.annotations.NotNull;

/**
 * An abstraction of {@link SlashCommand} with the intention to improve developer experience.
 * <p/>
 * This implements methods like {@link #getData()}, {@link #getVisibility()} and {@link #getState()} for you. Their values are
 * inserted within the constructor.
 * <p/>
 * Methods like {@link #addEnabledGuilds(Long...)}, {@link #addRequiredBotPermission(Permission...)} and {@link #addRequiredUserPermission(Permission...)} exist to improve the experience.
 */
public abstract class AbstractSlashCommand extends AbstractInteractionCommand implements SlashCommand {
    private final SlashCommandData commandData;

    protected AbstractSlashCommand(@NotNull final SlashCommandData commandData, @NotNull final InteractionCommandVisibility visibility) {
        this(commandData, visibility, InteractionCommandState.ENABLED);
    }

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