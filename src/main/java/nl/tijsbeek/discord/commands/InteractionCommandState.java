package nl.tijsbeek.discord.commands;

/**
 * The state of the command, when set to {@link #DISABLED} the command won't be registered on Discord.
 */
public enum InteractionCommandState {
    ENABLED,
    DISABLED
}