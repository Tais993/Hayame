package nl.tijsbeek.discord.commands.commands.context.user;

import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import nl.tijsbeek.discord.commands.InteractionCommandState;
import nl.tijsbeek.discord.commands.InteractionCommandVisibility;
import nl.tijsbeek.discord.commands.UserContextCommand;
import nl.tijsbeek.discord.commands.abstractions.AbstractInteractionCommand;
import org.jetbrains.annotations.NotNull;

public class AaaaaUCommand extends AbstractInteractionCommand implements UserContextCommand {
    public AaaaaUCommand() {
        super(Commands.user("aaaaa"), InteractionCommandVisibility.GUILD_ONLY, InteractionCommandState.ENABLED);

    }

    @Override
    public void onUserContextInteraction(@NotNull UserContextInteractionEvent event) {
        event.reply("grrrr").queue();

        for (int i = 0; i < 100; i++) {
            System.out.println("a");
        }
    }
}