package nl.tijsbeek.utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.time.temporal.TemporalAccessor;

/**
 * Contains some utils for {@link EmbedBuilder} and {@link MessageEmbed} creation.
 */
public class EmbedUtils {

    /**
     * Creates a {@link EmbedBuilder} based of the given {@link Member}.
     * <br/>
     * This sets the {@link EmbedBuilder#setAuthor(String)} and {@link EmbedBuilder#setTimestamp(TemporalAccessor)}.
     *
     * @param member the {@link Member} to base the {@link EmbedBuilder} on
     *
     * @return the created {@link EmbedBuilder}
     */
    @NotNull
    @Contract(pure = true)
    public static EmbedBuilder createBuilder(@NotNull final Member member) {
        // TODO: link to profile on author press
        return new EmbedBuilder()
                .setAuthor(member.getEffectiveName(), null, member.getEffectiveAvatarUrl())
                .setTimestamp(Instant.now());
    }
}