package nl.tijsbeek.discord.commands.commands.slash;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import nl.tijsbeek.discord.commands.InteractionCommandState;
import nl.tijsbeek.discord.commands.InteractionCommandVisibility;
import nl.tijsbeek.discord.commands.abstractions.AbstractSlashCommand;
import org.jetbrains.annotations.NotNull;

public class TestCommand extends AbstractSlashCommand {
    public TestCommand() {
        super(Commands.slash("test", "aa"), InteractionCommandVisibility.PRIVATE, InteractionCommandState.ENABLED);

        addEnabledGuilds(707295470661140562L);

        addRequiredBotPermission(Permission.ADMINISTRATOR);
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        event.reply("pogu").queue();
    }
}