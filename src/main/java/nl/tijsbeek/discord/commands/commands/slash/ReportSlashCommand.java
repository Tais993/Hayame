package nl.tijsbeek.discord.commands.commands.slash;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import nl.tijsbeek.database.tables.GuildSettings;
import nl.tijsbeek.discord.commands.InteractionCommandVisibility;
import nl.tijsbeek.discord.commands.abstractions.AbstractSlashCommand;
import nl.tijsbeek.utils.DiscordClientAction;
import nl.tijsbeek.utils.LocaleHelper;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
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
                .addOption(OptionType.STRING, REASON_OPTION, "The reason for the report", true);
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        ResourceBundle resource = LocaleHelper.getBotResource(event.getUserLocale());

        GuildSettings guildSettings = database.getGuildSettingsDatabase().retrieveById(event.getGuild().getIdLong());

        MessageChannel messageChannel = event.getJDA().getChannelById(MessageChannel.class, guildSettings.getReportChannelId());

        if (null == messageChannel) {
            event.reply(resource.getString("command.report.invalid.channel")).setEphemeral(true).queue();
            return;
        }


        User reporter = event.getUser();

        User reportedUser = event.getOption(USER_OPTION, OptionMapping::getAsUser);
        String reason = event.getOption(REASON_OPTION, OptionMapping::getAsString);

        EmbedBuilder builder = new EmbedBuilder()
                .setColor(Color.RED)
                .setTitle(resource.getString("command.report.report"))
                .setDescription(resource.getString("command.report.message.slash").formatted(
                                reportedUser.getAsMention(), reportedUser.getId(),
                        reporter.getAsMention(), reporter.getId(),
                        reason
                ));


        messageChannel.sendMessageEmbeds(builder.build())
                        .setActionRow(List.of(
                                DiscordClientAction.General.USER.asLinkButton(resource.getString("command.report.reporter.profile"), event.getMember().getId()),
                                DiscordClientAction.General.USER.asLinkButton(resource.getString("command.report.reportee.profile"), reportedUser.getId())
                        )).queue();

        event.reply(resource.getString("command.report.success")).setEphemeral(true).queue();
    }
}
