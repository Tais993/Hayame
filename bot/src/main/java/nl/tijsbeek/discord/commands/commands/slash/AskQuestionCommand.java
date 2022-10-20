package nl.tijsbeek.discord.commands.commands.slash;

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.Modal;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import nl.tijsbeek.discord.commands.InteractionCommandVisibility;
import nl.tijsbeek.discord.commands.abstractions.AbstractSlashCommand;
import org.jetbrains.annotations.NotNull;

public class AskQuestionCommand extends AbstractSlashCommand {
    public AskQuestionCommand() {
        super(Commands.slash("ask-question", "ask a question"), InteractionCommandVisibility.PRIVATE);

        addEnabledGuilds(707295470661140562L);
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        Modal modal = Modal.create(generateId(), "Ask a question!")
                .addActionRow(TextInput.create("question", "Your question", TextInputStyle.SHORT).build())
                .addActionRow(TextInput.create("full_description", "Longer form of question", TextInputStyle.PARAGRAPH).build())
                .addActionRow(TextInput.create("code", "Your code", TextInputStyle.PARAGRAPH).build())
                .build();

        event.replyModal(
                modal
        ).queue();
    }

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        String question = event.getValue("question").getAsString();
        String fullDescription = event.getValue("full_description").getAsString();
        String code = event.getValue("code").getAsString();

        event.getChannel().asTextChannel().createThreadChannel(question)
                .flatMap(channel -> channel.sendMessage("""
                        **%s**
                        %s
                        
                        ```java
                        %s
                        ```
                        """.formatted(question, fullDescription, code)))
                .flatMap(message -> event.reply("Successfully asked question!").setEphemeral(true))
                .queue();
    }
}
