package nl.tijsbeek.discord.commands;

import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import nl.tijsbeek.discord.commands.abstractions.AbstractInteractionCommand;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a Discord user context-command.
 *
 * @see AbstractInteractionCommand
 */
public interface UserContextCommand extends InteractionCommand {

    /**
     * The {@link UserContextInteractionEvent} that triggers when someone runs a user context-command.
     *
     * @param event the {@link UserContextInteractionEvent}
     */
    void onUserContextInteraction(@NotNull UserContextInteractionEvent event);
}