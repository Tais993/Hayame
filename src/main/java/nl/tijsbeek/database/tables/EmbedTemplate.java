package nl.tijsbeek.database.tables;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.time.Instant;
import java.time.temporal.TemporalAccessor;
import java.util.Collections;
import java.util.List;

public class EmbedTemplate {


    private final TemporalAccessor timestamp;
    private final String authorName;
    private final String authorUrl;
    private final String authorIconUrl;
    private final Color color;
    private final String footerUrl;
    private final String imageUrl;
    private final String thumbnailUrl;
    private final List<String> mentions;


    public EmbedTemplate(final TemporalAccessor timestamp, final String authorName, final String authorUrl,
                         final String authorIconUrl, final Color color, final String footerUrl,
                         final String imageUrl, final String thumbnailUrl, final List<String> mentions) {

        this.timestamp = timestamp;
        this.authorName = authorName;
        this.authorUrl = authorUrl;
        this.authorIconUrl = authorIconUrl;
        this.color = color;
        this.footerUrl = footerUrl;
        this.imageUrl = imageUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.mentions = Collections.unmodifiableList(mentions);
    }

    public TemporalAccessor getTimestamp() {
        return timestamp;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getAuthorUrl() {
        return authorUrl;
    }

    public String getAuthorIconUrl() {
        return authorIconUrl;
    }

    public Color getColor() {
        return color;
    }

    public int getColorRGB() {
        return null != color ? color.getRGB() : 0;
    }

    public String getFooterUrl() {
        return footerUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public List<String> getMentions() {
        return mentions;
    }


    /**
     * <b>This doesn't set the footer URL!</b>
     *
     * @return an {@link EmbedBuilder} with the variables of this template
     */
    @NotNull
    public EmbedBuilder toEmbedBuilder() {
        return new EmbedBuilder()
                .setTimestamp(Instant.now())
                .setAuthor(authorName, authorUrl, authorIconUrl)
                .setColor(color)
                .setImage(imageUrl)
                .setThumbnail(thumbnailUrl);
    }


    public static class EmbedTemplateBuilder {
        private TemporalAccessor timestamp;
        private String authorName;
        private String authorUrl;
        private String authorIconUrl;
        private Color color;
        private String footerUrl;
        private String imageUrl;
        private String thumbnailUrl;
        private List<String> mentions;

        public EmbedTemplateBuilder setTimestamp(final TemporalAccessor timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public EmbedTemplateBuilder setAuthor(final User author) {
            setAuthorName(author.getAsTag());
            setAuthorUrl(author.getAvatarUrl());
            setAuthorIconUrl(author.getAvatarUrl());
            return this;
        }

        public EmbedTemplateBuilder setAuthorName(final String authorName) {
            this.authorName = authorName;
            return this;
        }

        public EmbedTemplateBuilder setAuthorUrl(final String authorUrl) {
            this.authorUrl = authorUrl;
            return this;
        }

        public EmbedTemplateBuilder setAuthorIconUrl(final String authorIconUrl) {
            this.authorIconUrl = authorIconUrl;
            return this;
        }

        public EmbedTemplateBuilder setColor(final Color color) {
            this.color = color;
            return this;
        }

        public EmbedTemplateBuilder setFooterUrl(final String footerUrl) {
            this.footerUrl = footerUrl;
            return this;
        }

        public EmbedTemplateBuilder setImageUrl(final String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public EmbedTemplateBuilder setThumbnailUrl(final String thumbnailUrl) {
            this.thumbnailUrl = thumbnailUrl;
            return this;
        }

        public EmbedTemplateBuilder setMentionables(final List<? extends IMentionable> mentionables) {
            this.mentions = Collections.unmodifiableList(mentionables).stream().map(IMentionable::getAsMention).toList();
            return this;
        }

        public EmbedTemplateBuilder setMentions(final List<String> mentions) {
            this.mentions = Collections.unmodifiableList(mentions);
            return this;
        }

        public EmbedTemplate createEmbedTemplate() {
            return new EmbedTemplate(timestamp, authorName, authorUrl,
                     authorIconUrl, color, footerUrl,
                    imageUrl, thumbnailUrl, mentions);
        }

        @NonNls
        @NotNull
        @Override
        public String toString() {
            return "EmbedTemplateBuilder{" +
                    "timestamp=" + timestamp +
                    ", authorName='" + authorName + '\'' +
                    ", authorUrl='" + authorUrl + '\'' +
                    ", authorIconUrl='" + authorIconUrl + '\'' +
                    ", color=" + color +
                    ", footerUrl='" + footerUrl + '\'' +
                    ", imageUrl='" + imageUrl + '\'' +
                    ", thumbnailUrl='" + thumbnailUrl + '\'' +
                    ", mentions=" + mentions +
                    '}';
        }
    }
}