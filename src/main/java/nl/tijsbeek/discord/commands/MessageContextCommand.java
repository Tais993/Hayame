package nl.tijsbeek.discord.commands;

import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import org.jetbrains.annotations.NotNull;

public interface MessageContextCommand extends InteractionCommand {

    void onMessageContextInteraction(@NotNull MessageContextInteractionEvent event);
}