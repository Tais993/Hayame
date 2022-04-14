package nl.tijsbeek.discord.commands.commands.context.message;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.context.MessageContextInteraction;
import nl.tijsbeek.database.tables.GuildSettings;
import nl.tijsbeek.discord.commands.InteractionCommandVisibility;
import nl.tijsbeek.discord.commands.MessageContextCommand;
import nl.tijsbeek.discord.commands.abstractions.AbstractInteractionCommand;
import nl.tijsbeek.utils.DiscordClientAction;
import nl.tijsbeek.utils.LocaleHelper;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;
import java.util.ResourceBundle;

public class ReportMessageCommand extends AbstractInteractionCommand implements MessageContextCommand {
    public ReportMessageCommand() {
        super(Commands.message("report"), InteractionCommandVisibility.GUILD_ONLY);
    }

    @Override
    public void onMessageContextInteraction(@NotNull MessageContextInteractionEvent event) {
        ResourceBundle resource = LocaleHelper.getResource(event.getUserLocale(), "ReportCommand");

        GuildSettings guildSettings = database.getGuildSettingsDatabase().retrieveById(event.getGuild().getIdLong());

        MessageChannel messageChannel = event.getJDA().getChannelById(MessageChannel.class, guildSettings.getReportChannelId());

        if (null == messageChannel) {
            event.reply(resource.getString("invalid.channel")).setEphemeral(true).queue();
            return;
        }

        Message targetMessage = event.getTarget();

        String targetGuildId = targetMessage.getGuild().getId();
        String targetChannelId = targetMessage.getChannel().getId();
        String targetMessageId = targetMessage.getId();

        User reporter = event.getUser();
        User reportee = targetMessage.getAuthor();

        EmbedBuilder builder = new EmbedBuilder()
                .setColor(Color.RED)
                .setTitle(resource.getString("report"))
                .setDescription(resource.getString("message.message").formatted(
                        reportee.getAsMention(), reportee.getId(),
                        reporter.getAsMention(), reporter.getId()
                ));

        EmbedBuilder targetMessageEmbed = new EmbedBuilder()
                .setColor(Color.RED)
                .setTitle(resource.getString("message.message.content.title"))
                .setDescription(targetMessage.getContentRaw());


        messageChannel.sendMessageEmbeds(builder.build(), targetMessageEmbed.build())
                .setActionRow(List.of(
                        DiscordClientAction.General.USER.asLinkButton(resource.getString("reporter.profile"), reporter.getId()),
                        DiscordClientAction.General.USER.asLinkButton(resource.getString("reportee.profile"), reportee.getId()),
                        DiscordClientAction.Channels.GUILD_CHANNEL_MESSAGE.asLinkButton(resource.getString("message.link"),  targetGuildId, targetChannelId, targetMessageId)
                )).queue();

        event.reply(resource.getString("success")).setEphemeral(true).queue();
    }
}
