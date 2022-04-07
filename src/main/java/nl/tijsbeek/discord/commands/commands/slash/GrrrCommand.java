package nl.tijsbeek.discord.commands.commands.slash;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import nl.tijsbeek.discord.commands.InteractionCommandState;
import nl.tijsbeek.discord.commands.InteractionCommandVisibility;
import nl.tijsbeek.discord.commands.abstractions.AbstractSlashCommand;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

public class GrrrCommand extends AbstractSlashCommand {

    public GrrrCommand() {
        super(Commands.slash("grrrrr", "aa"), InteractionCommandVisibility.GUILD_ONLY, InteractionCommandState.ENABLED);
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        event.reply("grrrr")
                .queue();

        for (int i = 0; i < 20000; i++) {
            System.out.println("a");
        }
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        event.editMessage("Aa").queue();
    }
}