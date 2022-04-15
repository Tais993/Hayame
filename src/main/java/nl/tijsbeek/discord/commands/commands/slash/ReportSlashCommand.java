package nl.tijsbeek.discord.commands.commands.slash;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import nl.tijsbeek.database.databases.Database;
import nl.tijsbeek.database.tables.GuildSettings;
import nl.tijsbeek.discord.commands.InteractionCommandVisibility;
import nl.tijsbeek.discord.commands.abstractions.AbstractSlashCommand;
import nl.tijsbeek.utils.DiscordClientAction;
import nl.tijsbeek.utils.LocaleHelper;
import nl.tijsbeek.utils.StreamUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class ReportSlashCommand extends AbstractSlashCommand {

    public static final String USER_OPTION = "user";
    public static final String REASON_OPTION = "reason";
    public static final String ATTACHMENT_OPTION = "attachment";
    public static final String ATTACHMENT_URL_OPTION = "urls";

    public ReportSlashCommand() {
        super(Commands.slash("report", "Report an user"), InteractionCommandVisibility.GUILD_ONLY);

        getData()
                .addOption(OptionType.USER, USER_OPTION, "The user to report, ID is valid as well", true)
                .addOption(OptionType.STRING, REASON_OPTION, "The reason for the report", true)
                .addOptions(generateOptionalVarArgList(new OptionData(OptionType.ATTACHMENT, ATTACHMENT_OPTION, "Possible evidence or other attachments"), 3))
                .addOption(OptionType.STRING, ATTACHMENT_URL_OPTION, "Possible evidence or other attachment URL's, seperated by comma!");
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        ResourceBundle resource = LocaleHelper.getBotResource(event.getUserLocale());

        MessageChannel messageChannel = handleReportLogChannel(database, event);

        if (messageChannel == null) {
            return;
        }


        User reporter = event.getUser();

        User reportedUser = event.getOption(USER_OPTION, OptionMapping::getAsUser);
        String reason = event.getOption(REASON_OPTION, OptionMapping::getAsString);

        String attachmentString = event.getOption(ATTACHMENT_URL_OPTION, "", OptionMapping::getAsString);

        List<String> attachments = varArgOptionsToList(event.getOptions(), optionMapping -> optionMapping.getAsAttachment().getUrl(), ATTACHMENT_OPTION);
        attachments.addAll(Arrays.asList(attachmentString.split(",")));


        EmbedBuilder builder = new EmbedBuilder()
                .setColor(Color.RED)
                .setTitle(resource.getString("command.report.title"))
                .setDescription(resource.getString("command.report.message").formatted(
                                reportedUser.getAsMention(), reportedUser.getId(),
                        reporter.getAsMention(), reporter.getId(),
                        StreamUtils.toJoinedString(attachments.stream()), reason
                ));


        messageChannel.sendMessageEmbeds(builder.build())
                        .setActionRow(List.of(
                                DiscordClientAction.General.USER.asLinkButton(resource.getString("command.report.reporter.profile"), event.getMember().getId()),
                                DiscordClientAction.General.USER.asLinkButton(resource.getString("command.report.reportee.profile"), reportedUser.getId())
                        )).queue();

        event.reply(resource.getString("command.report.success")).setEphemeral(true).queue();
    }


    /**
     * Check or the log channel has been set for reports, if unset it handels the event and returns null.
     *
     * @param database the {@link Database} of the bot
     * @param event the {@link IReplyCallback} to reply to on failure
     *
     * @return null or a {@link MessageChannel} to log report to.
     */
    public static @Nullable MessageChannel handleReportLogChannel(@NotNull Database database, @NotNull final IReplyCallback event) {
        GuildSettings guildSettings = database.getGuildSettingsDatabase().retrieveById(event.getGuild().getIdLong());
        ResourceBundle resource = LocaleHelper.getBotResource(event.getUserLocale());

        MessageChannel messageChannel = event.getJDA().getChannelById(MessageChannel.class, guildSettings.getReportChannelId());

        if (null == messageChannel) {
            event.reply(resource.getString("command.report.invalid.channel")).setEphemeral(true).queue();
            return null;
        }

        return messageChannel;
    }
}