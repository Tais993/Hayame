package nl.tijsbeek.discord.commands;

import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import nl.tijsbeek.discord.commands.abstractions.AbstractInteractionCommand;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a Discord message context-command.
 *
 * @see AbstractInteractionCommand
 */
public interface MessageContextCommand extends InteractionCommand {

    /**
     * The {@link MessageContextInteractionEvent} that triggers when someone runs a message context-command.
     *
     * @param event the {@link MessageContextInteractionEvent}
     */
    void onMessageContextInteraction(@NotNull MessageContextInteractionEvent event);
}