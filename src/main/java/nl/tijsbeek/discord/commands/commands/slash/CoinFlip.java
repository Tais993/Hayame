package nl.tijsbeek.discord.commands.commands.slash;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import nl.tijsbeek.discord.commands.InteractionCommandVisibility;
import nl.tijsbeek.discord.commands.abstractions.AbstractSlashCommand;
import nl.tijsbeek.utils.EmbedUtils;
import nl.tijsbeek.utils.LocaleHelper;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ThreadLocalRandom;

public class CoinFlip extends AbstractSlashCommand {
    public CoinFlip() {
        super(Commands.slash("coin-flip", "Flips a coin for you"), InteractionCommandVisibility.GUILD_ONLY);
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        ResourceBundle resourceBundle = LocaleHelper.getSlashCommandResource(event.getUserLocale());

        event.replyEmbeds(getHeadOrTails(resourceBundle, event.getMember()))
                .addActionRow(Button.primary(generateId("retry"), resourceBundle.getString("coinflip.retryButton")))
                .queue();
    }

    @Override
    public void onButtonInteraction(@NotNull final ButtonInteractionEvent event) {
        ResourceBundle resourceBundle = LocaleHelper.getSlashCommandResource(event.getUserLocale());
        List<String> argumentsComponent = getArgumentsComponent(event);

        if ("retry".equals(argumentsComponent.get(0))) {
            event.editMessageEmbeds(getHeadOrTails(resourceBundle, event.getMember())).queue();
        } else {
            throw new IllegalStateException("Unknown button press");
        }
    }

    private static @NotNull MessageEmbed getHeadOrTails(@NotNull final ResourceBundle resourceBundle, @NotNull final Member member) {
        boolean isHeads = ThreadLocalRandom.current().nextBoolean();

        EmbedBuilder embedBuilder = EmbedUtils.createBuilder(member);

        if (isHeads) {
            embedBuilder.setDescription(resourceBundle.getString("coinflip.head"));
        } else {
            embedBuilder.setDescription(resourceBundle.getString("coinflip.tails"));
        }

        return embedBuilder.build();
    }
}
