package nl.tijsbeek.discord.commands.commands.slash;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.*;
import nl.tijsbeek.discord.commands.InteractionCommandVisibility;
import nl.tijsbeek.discord.commands.abstractions.AbstractSlashCommand;
import nl.tijsbeek.database.tables.GuildSettings;
import nl.tijsbeek.utils.EmbedUtils;
import nl.tijsbeek.utils.LocaleHelper;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.ResourceBundle;

public final class SettingsCommand extends AbstractSlashCommand {

    private static final String SET_COMMAND_GROUP = "set";
    private static final String GET_COMMAND_GROUP = "get";

    private static final String REPORT_LOG_CHANNEL_SUB_COMMAND = "report-log-channel";
    private static final String NEW_REPORT_LOG_CHANNEL_OPTION = "new-report-log-channel";


    public SettingsCommand() {
        super(Commands.slash("settings", "Allows setting certain settings of the bot"), InteractionCommandVisibility.GUILD_ONLY);

        List<SubcommandData> setSubCommands = List.of(
                new SubcommandData(REPORT_LOG_CHANNEL_SUB_COMMAND, "The channel to log reports to")
                        .addOptions(List.of(
                                new OptionData(OptionType.CHANNEL, NEW_REPORT_LOG_CHANNEL_OPTION,
                                        "The channel to log reports to.", true)
                                        .setChannelTypes(ChannelType.NEWS, ChannelType.GUILD_NEWS_THREAD, ChannelType.GUILD_PRIVATE_THREAD,
                                                ChannelType.GUILD_PUBLIC_THREAD, ChannelType.TEXT))

                        ));

        List<SubcommandData> getSubCommands = List.of(
                new SubcommandData(REPORT_LOG_CHANNEL_SUB_COMMAND, "The channel to log reports to")
        );

        List<SubcommandGroupData> subCommandsGroup = List.of(
                new SubcommandGroupData(SET_COMMAND_GROUP, "Sets a value").addSubcommands(setSubCommands),
                new SubcommandGroupData(GET_COMMAND_GROUP, "Gets a value").addSubcommands(getSubCommands)
        );

        getData().addSubcommandGroups(subCommandsGroup);

        addRequiredUserPermission(Permission.MANAGE_SERVER);
    }

    @Override
    @Contract(pure = true)
    public void onSlashCommandInteraction(@NotNull final SlashCommandInteractionEvent event) {

        GuildSettings guildSettings = database.getGuildSettingsDatabase().retrieveById(event.getGuild().getIdLong());

        switch (event.getSubcommandGroup()) {
            case GET_COMMAND_GROUP -> getSubCommand(event, guildSettings);

            case SET_COMMAND_GROUP -> setSubCommand(event, guildSettings);
        }
    }

    private void setSubCommand(@NotNull final SlashCommandInteractionEvent event, @NotNull final GuildSettings guildSettings) {
        ResourceBundle resource = LocaleHelper.getBotResource(event.getUserLocale());

        String name;
        String newValue;

        //noinspection SwitchStatementWithTooFewBranches
        switch (event.getSubcommandName()) {
            case REPORT_LOG_CHANNEL_SUB_COMMAND -> {
                name = REPORT_LOG_CHANNEL_SUB_COMMAND;

                GuildMessageChannel textChannel = event.getOption(NEW_REPORT_LOG_CHANNEL_OPTION, OptionMapping::getAsChannel).asGuildMessageChannel();

                newValue = textChannel.getAsMention();

                guildSettings.setReportChannelId(textChannel.getIdLong());
            }
            default -> throw new IllegalStateException("Unexpected value: " + event.getSubcommandName());
        }

        EmbedBuilder builder = EmbedUtils.createBuilder(event.getMember())
                .setTitle(resource.getString("command.settings.set.title").formatted(name))
                .setDescription(resource.getString("command.settings.set.description").formatted(newValue));


        database.getGuildSettingsDatabase().replace(guildSettings);

        event.replyEmbeds(builder.build()).queue();
    }

    private void getSubCommand(@NotNull final SlashCommandInteractionEvent event, @NotNull final GuildSettings guildSettings) {
        ResourceBundle resource = LocaleHelper.getBotResource(event.getUserLocale());

        String name;
        String value;

        //noinspection SwitchStatementWithTooFewBranches
        switch (event.getSubcommandName()) {
            case REPORT_LOG_CHANNEL_SUB_COMMAND -> {
                name = REPORT_LOG_CHANNEL_SUB_COMMAND;
                value = mentionTextChannelById(guildSettings.getReportChannelId());
            }
            default -> throw new IllegalStateException("Unexpected value: " + event.getSubcommandName());
        }

        EmbedBuilder builder = EmbedUtils.createBuilder(event.getMember())
                .setTitle(resource.getString("command.settings.get.title").formatted(name))
                .setDescription(resource.getString("command.settings.get.description").formatted(value));


        database.getGuildSettingsDatabase().replace(guildSettings);

        event.replyEmbeds(builder.build()).queue();
    }

    @NotNull
    @Contract(pure = true)
    private static String mentionTextChannelById(final long id) {
        if (0L == id) {
            return "empty";
        } else {
            return "<#" + id + ">";
        }
    }
}
