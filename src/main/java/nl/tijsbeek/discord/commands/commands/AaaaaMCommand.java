package nl.tijsbeek.discord.commands.commands;

import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import nl.tijsbeek.discord.commands.InteractionCommandState;
import nl.tijsbeek.discord.commands.InteractionCommandVisibility;
import nl.tijsbeek.discord.commands.MessageContextCommand;
import nl.tijsbeek.discord.commands.abstractions.AbstractInteractionCommand;
import org.jetbrains.annotations.NotNull;

public class AaaaaMCommand extends AbstractInteractionCommand implements MessageContextCommand {
    public AaaaaMCommand() {
        super(Commands.message("aaaaa"), InteractionCommandVisibility.GUILD_ONLY, InteractionCommandState.ENABLED);

    }

    @Override
    public void onMessageContextInteraction(@NotNull MessageContextInteractionEvent event) {
        event.reply("grrrr").queue();

        for (int i = 0; i < 100; i++) {
            System.out.println("a");
        }
    }
}