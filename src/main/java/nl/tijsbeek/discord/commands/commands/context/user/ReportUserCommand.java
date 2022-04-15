package nl.tijsbeek.discord.commands.commands.context.user;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.text.Modal;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import nl.tijsbeek.discord.commands.InteractionCommandVisibility;
import nl.tijsbeek.discord.commands.UserContextCommand;
import nl.tijsbeek.discord.commands.abstractions.AbstractInteractionCommand;
import nl.tijsbeek.utils.DiscordClientAction;
import nl.tijsbeek.utils.LocaleHelper;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;
import java.util.ResourceBundle;

import static nl.tijsbeek.discord.commands.commands.slash.ReportSlashCommand.handleReportLogChannel;
import static nl.tijsbeek.utils.MentionUtils.mentionUserById;

public class ReportUserCommand extends AbstractInteractionCommand implements UserContextCommand {
    public ReportUserCommand() {
        super(Commands.user("report"), InteractionCommandVisibility.GUILD_ONLY);
    }


    @Override
    public void onUserContextInteraction(@NotNull UserContextInteractionEvent event) {
        ResourceBundle resource = LocaleHelper.getBotResource(event.getUserLocale());

        MessageChannel messageChannel = handleReportLogChannel(database, event);

        if (messageChannel == null) {
            return;
        }

        String reporterId = event.getMember().getId();
        String reporteeId = event.getTarget().getId();


        String customId = generateId(reporterId, reporteeId);


        Modal modal = Modal.create(customId, "Report")
                .addActionRow(TextInput.create("reason", "reason", TextInputStyle.SHORT).build())
                .addActionRow(TextInput.create("attachments", "attachment URL's", TextInputStyle.SHORT).build())
                .build();


        event.replyModal(modal).queue();
    }

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        ResourceBundle resource = LocaleHelper.getBotResource(event.getUserLocale());

        MessageChannel messageChannel = handleReportLogChannel(database, event);

        if (messageChannel == null) {
            return;
        }


        List<String> argumentsComponent = getArgumentsComponent(event.getModalId());

        String reporterId = argumentsComponent.get(0);
        String reporteeId = argumentsComponent.get(1);


        EmbedBuilder builder = new EmbedBuilder()
                .setColor(Color.RED)
                .setTitle(resource.getString("command.report.title"))
                .setDescription(resource.getString("command.report.message").formatted(
                        mentionUserById(reporteeId), reporteeId,
                        mentionUserById(reporterId), reporterId,
                        event.getValue("attachments").getAsString(),
                        event.getValue("reason").getAsString()
                ));


        messageChannel.sendMessageEmbeds(builder.build())
                .setActionRow(List.of(
                        DiscordClientAction.General.USER.asLinkButton(resource.getString("command.report.reporter.profile"), reporterId),
                        DiscordClientAction.General.USER.asLinkButton(resource.getString("command.report.reportee.profile"), reporteeId)
                )).queue();

        event.reply(resource.getString("command.report.success")).setEphemeral(true).queue();
    }
}