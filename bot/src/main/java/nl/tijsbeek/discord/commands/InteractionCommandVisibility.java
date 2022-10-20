package nl.tijsbeek.discord.commands;

import nl.tijsbeek.discord.commands.abstractions.AbstractInteractionCommand;

/**
 * The visibility of the command.
 */
public enum InteractionCommandVisibility {
    GLOBAL,
    GUILD_ONLY,

    /**
     * When set to private, use {@link InteractionCommand InteractionCommand's} addEnabledGuilds to register guilds.
     */
    PRIVATE
}
