package nl.tijsbeek.discord.commands;

import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import org.jetbrains.annotations.NotNull;

public interface UserContextCommand extends InteractionCommand {

    void onUserContextInteraction(@NotNull UserContextInteractionEvent event);
}