package nl.tijsbeek.discord.commands.commands.slash;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.Modal;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;
import nl.tijsbeek.database.databases.Database;
import nl.tijsbeek.database.databases.EmbedDatabase;
import nl.tijsbeek.discord.commands.InteractionCommandVisibility;
import nl.tijsbeek.discord.commands.abstractions.AbstractSlashCommand;
import nl.tijsbeek.database.tables.EmbedTemplate;
import nl.tijsbeek.utils.LocaleHelper;
import nl.tijsbeek.utils.StreamUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public final class EmbedCommand extends AbstractSlashCommand {
    private static final Logger logger = LoggerFactory.getLogger(EmbedCommand.class);

    private static final String TIMESTAMP_OPTION = "timestamp";

    private static final String AUTHOR_OPTION = "author";
    private static final String AUTHOR_NAME_OPTION = "author-name";
    private static final String AUTHOR_URL_OPTION = "author-url";
    private static final String AUTHOR_ICON_URL_OPTION = "author-icon-url";
    private static final String AUTHOR_ICON_OPTION = "author-icon";

    private static final String COLOUR_OPTION = "colour";
    private static final String CUSTOM_COLOUR_OPTION = "custom-colour";

    private static final String FOOTER_URL_OPTION = "footer-url";

    private static final String IMAGE_OPTION = "image";
    private static final String IMAGE_URL_OPTION = "image-url";

    private static final String THUMBNAIL_OPTION = "thumbnail";
    private static final String THUMBNAIL_URL_OPTION = "thumbnail-url";

    private static final String WHO_TO_PING_OPTION = "to-ping";
    private static final String TITLE_COMPONENT_ID = "title";
    private static final String DESCRIPTION_COMPONENT_ID = "description";
    private static final String FOOTER_TEXT_COMPONENT_ID = "footer_text";

    private EmbedDatabase embedDatabase;

    public EmbedCommand() {
        super(Commands.slash("embed", "Allows you to generate embeds, once submitted pop-up comes asking for the string values."), InteractionCommandVisibility.GUILD_ONLY);


        // TODO support fields
        getData().addOption(OptionType.BOOLEAN, TIMESTAMP_OPTION, "Include timestamp")

                .addOption(OptionType.USER, AUTHOR_OPTION, "The author, the url becomes the PFP (overrides `Author name`, `Author url` and `Author icon url`)")
                .addOption(OptionType.STRING, AUTHOR_NAME_OPTION, "The author's name")
                .addOption(OptionType.STRING, AUTHOR_URL_OPTION, "The URL that will be opened once the author's name will be clicked.")
                .addOption(OptionType.ATTACHMENT, AUTHOR_ICON_OPTION, "The author's icon (overrides author icon url)")
                .addOption(OptionType.STRING, AUTHOR_ICON_URL_OPTION, "The author's icon url")

                .addOptions(generateColourOption())
                .addOption(OptionType.STRING, CUSTOM_COLOUR_OPTION, "Hex or RGB (a.e for green, rgb; `0, 255, 0` or `-16711936`, or hex; `#00ff00`")

                .addOption(OptionType.STRING, FOOTER_URL_OPTION, "Footer url (the URL that will be opened once the footer text is pressed)")

                .addOption(OptionType.ATTACHMENT, IMAGE_OPTION, "The image")
                .addOption(OptionType.STRING, IMAGE_URL_OPTION, "The URL for the image")
                .addOption(OptionType.ATTACHMENT, THUMBNAIL_OPTION, "The thumbnail")
                .addOption(OptionType.STRING, THUMBNAIL_URL_OPTION, "The URL for the thumbnail")

                .addOptions(generateOptionalVarArgList(new OptionData(OptionType.MENTIONABLE, WHO_TO_PING_OPTION, "Who/what to ping"), 10));


        addRequiredBotPermission(Permission.MESSAGE_SEND);
        addRequiredBotPermission(Permission.MESSAGE_EMBED_LINKS);

        addRequiredUserPermission(Permission.MANAGE_SERVER);
    }

    @Override
    public void setDatabase(final Database database) {
        super.setDatabase(database);
        this.embedDatabase = database.getEmbedDatabase();
    }

    private static @NotNull OptionData generateColourOption() {
        return new OptionData(OptionType.STRING, COLOUR_OPTION, "The name of the colour, overwrites %s.".formatted(CUSTOM_COLOUR_OPTION))
                .addChoice("Black", String.valueOf(Color.BLACK.getRGB()))
                .addChoice("Blue", String.valueOf(Color.BLUE.getRGB()))
                .addChoice("Cyan", String.valueOf(Color.CYAN.getRGB()))
                .addChoice("Green", String.valueOf(Color.GREEN.getRGB()))
                .addChoice("Gray", String.valueOf(Color.GRAY.getRGB()))
                .addChoice("Dark Gray", String.valueOf(Color.DARK_GRAY.getRGB()))
                .addChoice("Light Gray", String.valueOf(Color.LIGHT_GRAY.getRGB()))
                .addChoice("Magenta", String.valueOf(Color.MAGENTA.getRGB()))
                .addChoice("Orange", String.valueOf(Color.ORANGE.getRGB()))
                .addChoice("Pink", String.valueOf(Color.PINK.getRGB()))
                .addChoice("Red", String.valueOf(Color.RED.getRGB()))
                .addChoice("White", String.valueOf(Color.WHITE.getRGB()))
                .addChoice("Yellow", String.valueOf(Color.YELLOW.getRGB()));
    }

    @Nullable
    private static Color getEffectiveColor(@NotNull final SlashCommandInteraction interaction) {
        OptionMapping color = interaction.getOption(COLOUR_OPTION);
        OptionMapping customColor = interaction.getOption(CUSTOM_COLOUR_OPTION);

        String colorString = getEffectiveStringOption(color, customColor);

        return stringToRgbColor(interaction, colorString);
    }

    @Contract("!null, _ -> !null; null, !null -> !null; null, null -> null")
    private static @Nullable String getEffectiveStringOption(final @Nullable OptionMapping optionOne, final @Nullable OptionMapping optionTwo) {
        if (null != optionOne) {
            return optionOne.getAsString();
        } else if (null != optionTwo) {
            return optionTwo.getAsString();
        } else {
            return null;
        }
    }

    /**
     * Parses the given string to a {@link Color}, and handles failures.
     * <p>
     * This allows 3 syntaxes, a.e for green, rgb; `0, 255, 0` or `-16711936`, or hex; `#00ff00`"
     *
     * @param interaction the {@link IReplyCallback interaction} to reply to on failure
     * @param colorString a {@link String} of a color, following the syntax explained above
     * @return a {@link Color} or null
     */
    @Nullable
    @Contract("_, null -> null")
    private static Color stringToRgbColor(@NotNull final IReplyCallback interaction, @Nullable final String colorString) {
        ResourceBundle locale = LocaleHelper.getBotResource(interaction.getUserLocale());

        if (null == colorString || colorString.isBlank()) {
            return null;
        } else if (colorString.contains(",")) {
            List<Integer> rgbValues = Arrays.stream(colorString.split(","))
                    .map(String::trim)
                    .map(Integer::parseInt)
                    .toList();

            if (3 != rgbValues.size()) {
                interaction.reply(locale.getString("command.embed.error.invalid.custom-rgb")).queue();
            }

            return new Color(rgbValues.get(0), rgbValues.get(1), rgbValues.get(2));
        } else if (colorString.startsWith("#")) {
            try {
                return Color.decode(colorString);
            } catch (final NumberFormatException e) {
                interaction.reply(locale.getString("command.embed.error.invalid.hex")).queue();
                return null;
            }

        } else {
            Integer rgb = toInt(colorString);

            if (null == rgb) {
                interaction.reply(locale.getString("command.embed.error.invalid.int-rgb")).queue();
                return null;
            }

            return new Color(rgb);
        }
    }

    /**
     * Parses a {@link String} to {@link Integer}, this returns null when the given {@link String} isn't a valid {@link Integer}.
     *
     * @param s the {@link String} to parse
     * @return an {@link Integer} or null
     */
    private static @Nullable Integer toInt(@NotNull final String s) {
        try {
            return Integer.parseInt(s);
        } catch (final NumberFormatException e) {
            return null;
        }
    }

    @Override
    public void onSlashCommandInteraction(@NotNull final SlashCommandInteractionEvent event) {
        ResourceBundle locale = LocaleHelper.getBotResource(event.getUserLocale());

        Color colour = getEffectiveColor(event);

        if (event.isAcknowledged()) {
            return;
        }

        List<IMentionable> mentionables = varArgOptionsToList(event.getOptions(), OptionMapping::getAsMentionable, WHO_TO_PING_OPTION);

        EmbedTemplate.EmbedTemplateBuilder builder = new EmbedTemplate.EmbedTemplateBuilder();

        boolean includeTimestamp = Boolean.TRUE.equals(event.getOption(TIMESTAMP_OPTION, OptionMapping::getAsBoolean));

        builder.setTimestamp(includeTimestamp);


        User author = event.getOption(AUTHOR_OPTION, OptionMapping::getAsUser);

        if (null != author) {
            builder.setAuthor(author);
        } else {
            builder.setAuthorName(event.getOption(AUTHOR_NAME_OPTION, OptionMapping::getAsString));
            builder.setAuthorUrl(event.getOption(AUTHOR_URL_OPTION, OptionMapping::getAsString));

            String authorIconUrl = event.getOption(AUTHOR_ICON_OPTION, option -> option.getAsAttachment().getUrl());

            if (null == authorIconUrl) {
                authorIconUrl = event.getOption(AUTHOR_ICON_URL_OPTION, OptionMapping::getAsString);
            }

            builder.setAuthorIconUrl(authorIconUrl);
        }

        String footerUrl = event.getOption(FOOTER_URL_OPTION, OptionMapping::getAsString);

        builder.setColor(colour)
                .setFooterUrl(footerUrl)
                .setImageUrl(attachmentUrl(event.getOption(IMAGE_OPTION), event.getOption(IMAGE_URL_OPTION)))
                .setThumbnailUrl(attachmentUrl(event.getOption(THUMBNAIL_OPTION), event.getOption(THUMBNAIL_URL_OPTION)))
                .setMentionables(mentionables);


        String id = generateId();

        builder.setId(id);

        embedDatabase.insert(builder.createEmbedTemplate());

        Modal modal = Modal.create(id, "Embed content")
                .addActionRow(TextInput.create(TITLE_COMPONENT_ID, locale.getString("command.embed.modal.title"), TextInputStyle.SHORT).setMaxLength(MessageEmbed.TITLE_MAX_LENGTH).setRequired(true).build())
                .addActionRow(TextInput.create(DESCRIPTION_COMPONENT_ID, locale.getString("command.embed.modal.description"), TextInputStyle.PARAGRAPH).setMaxLength(MessageEmbed.DESCRIPTION_MAX_LENGTH).build())
                .addActionRow(TextInput.create(FOOTER_TEXT_COMPONENT_ID, locale.getString("command.embed.modal.footer"), TextInputStyle.PARAGRAPH).setMaxLength(MessageEmbed.TEXT_MAX_LENGTH).build())
                .build();

        event.replyModal(modal).queue();
    }

    /**
     * If the attachment isn't null, it returns the attachments URL.
     * If the attachment is null, it returns the urlString or null.
     *
     * @param attachment nullable {@link OptionMapping} of type {@link OptionType#ATTACHMENT}
     * @param urlString  nullable {@link OptionMapping} of type {@link OptionType#STRING}
     * @return the URL or null
     */
    @Nullable
    @Contract("!null, _ -> !null; null, !null -> !null; null, null -> null")
    private static String attachmentUrl(@Nullable final OptionMapping attachment, @Nullable final OptionMapping urlString) {
        if (null != attachment) {
            return attachment.getAsAttachment().getUrl();
        } else if (null != urlString) {
            return urlString.getAsString();
        } else {
            return null;
        }
    }


    @Override
    public void onModalInteraction(@NotNull final ModalInteractionEvent event) {
        ResourceBundle locale = LocaleHelper.getBotResource(event.getUserLocale());


        EmbedTemplate embedTemplate = embedDatabase.retrieveById(event.getModalId());


        EmbedBuilder builder = embedTemplate.toEmbedBuilder();


        event.getValues().forEach((ModalMapping modalMapping) -> {
            String content = modalMapping.getAsString();

            switch (modalMapping.getId()) {
                case TITLE_COMPONENT_ID -> builder.setTitle(content);
                case DESCRIPTION_COMPONENT_ID -> builder.setDescription(content);
                case FOOTER_TEXT_COMPONENT_ID -> builder.setFooter(content, embedTemplate.getFooterUrl());
            }
        });


        event.getMessageChannel()
                .sendMessageEmbeds(builder.build())
                .setContent(StreamUtils.toJoinedString(embedTemplate.getMentions().stream(), ","))
                .queue();

        event.reply(locale.getString("command.embed.success")).setEphemeral(true).queue();

        embedDatabase.deleteById(event.getModalId());
    }
}
