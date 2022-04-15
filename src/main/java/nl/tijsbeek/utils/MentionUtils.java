package nl.tijsbeek.utils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class MentionUtils {

    /**
     * Mentions the given user by their ID
     *
     * @param id the ID to mention
     *
     * @return a mention
     */
    @NotNull
    @Contract(pure = true)
    public static String mentionUserById(@NotNull final String id) {
        return "<@" + id + ">";
    }
}