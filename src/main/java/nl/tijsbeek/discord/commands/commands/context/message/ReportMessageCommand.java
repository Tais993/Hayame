package nl.tijsbeek.discord.commands.commands.context.message;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.text.Modal;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import nl.tijsbeek.database.tables.GuildSettings;
import nl.tijsbeek.discord.commands.InteractionCommandVisibility;
import nl.tijsbeek.discord.commands.MessageContextCommand;
import nl.tijsbeek.discord.commands.abstractions.AbstractInteractionCommand;
import nl.tijsbeek.utils.DiscordClientAction;
import nl.tijsbeek.utils.LocaleHelper;
import org.jetbrains.annotations.Contract;
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
            event.reply(resource.getString("command.report.invalid.channel")).setEphemeral(true).queue();
            return;
        }



        Modal modal = Modal.create("1", "Report")
                .addActionRow(TextInput.create("reason", "reason", TextInputStyle.SHORT));




//        String targetGuildId = targetMessage.getGuild().getId();
//        String targetChannelId = targetMessage.getChannel().getId();
//        String targetMessageId = targetMessage.getId();



    }

    @Override
    public void onModalInteraction(@NotNull final ModalInteractionEvent event) {
        ResourceBundle resource = LocaleHelper.getBotResource(event.getUserLocale());

        GuildSettings guildSettings = database.getGuildSettingsDatabase().retrieveById(event.getGuild().getIdLong());

        MessageChannel messageChannel = event.getJDA().getChannelById(MessageChannel.class, guildSettings.getReportChannelId());

        if (null == messageChannel) {
            event.reply(resource.getString("command.report.invalid.channel")).setEphemeral(true).queue();
            return;
        }


        List<String> argumentsComponent = getArgumentsComponent(event.getModalId());

        String targetGuildId = argumentsComponent.get(1);
        String targetChannelId = argumentsComponent.get(2);
        String targetMessageId = argumentsComponent.get(3);
        String reporterId = argumentsComponent.get(4);
        String reporteeId = argumentsComponent.get(5);
        String rawMessageContent = argumentsComponent.get(6);


        EmbedBuilder builder = new EmbedBuilder()
                .setColor(Color.RED)
                .setTitle(resource.getString("command.report.report"))
                .setDescription(resource.getString("command.report.message").formatted(
                        mentionUserById(reporteeId), reporteeId,
                        mentionUserById(reporterId), reporterId,
                        event.getValue("reason").getAsString()
                ));

        EmbedBuilder targetMessageEmbed = new EmbedBuilder()
                .setColor(Color.RED)
                .setTitle(resource.getString("command.report.message.message.content.title"))
                .setDescription(rawMessageContent);



        messageChannel.sendMessageEmbeds(builder.build(), targetMessageEmbed.build())
                .setActionRow(List.of(
                        DiscordClientAction.General.USER.asLinkButton(resource.getString("command.report.reporter.profile"), reporterId),
                        DiscordClientAction.General.USER.asLinkButton(resource.getString("command.report.reportee.profile"), reporteeId),
                        DiscordClientAction.Channels.GUILD_CHANNEL_MESSAGE.asLinkButton(resource.getString("command.report.message.link"),  targetGuildId, targetChannelId, targetMessageId)
                )).queue();

        event.reply(resource.getString("command.report.success")).setEphemeral(true).queue();
    }

    @NotNull
    @Contract(pure = true)
    private static String mentionUserById(@NotNull final String id) {
        return "<@" + id + ">";
    }
}
