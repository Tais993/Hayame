package nl.tijsbeek.discord.commands.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import nl.tijsbeek.discord.commands.InteractionCommandState;
import nl.tijsbeek.discord.commands.InteractionCommandVisibility;
import nl.tijsbeek.discord.commands.abstractions.AbstractSlashCommand;
import org.jetbrains.annotations.NotNull;

public class GrrrCommand extends AbstractSlashCommand {
    public GrrrCommand() {
        super(Commands.slash("grrrrr", "aa"), InteractionCommandVisibility.GUILD_ONLY, InteractionCommandState.ENABLED);

    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        event.reply("grrrr").queue();

        for (int i = 0; i < 20000; i++) {
            System.out.println("a");
        }
    }
}