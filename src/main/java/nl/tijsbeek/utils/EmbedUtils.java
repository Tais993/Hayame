package nl.tijsbeek.utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;

public class EmbedUtils {

    @NotNull
    @Contract(pure = true)
    public static EmbedBuilder createBuilder(@NotNull final Member member) {
        // TODO: link to profile on author press
        return new EmbedBuilder()
                .setAuthor(member.getEffectiveName(), null, member.getEffectiveAvatarUrl())
                .setTimestamp(Instant.now());
    }
}
