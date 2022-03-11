package nl.tijsbeek.utils;

import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Contains some utils for {@link Stream Streams}.
 */
public final class StreamUtils {

    /**
     * Collects the {@link Stream} of {@link String} within {@code ()} with {@code ,} as the delimiter.
     *
     * @param stream the {@link Stream} to collect
     *
     * @return the joined {@link String}
     */
    @NotNull
    public static String toJoinedString(@NotNull final Stream<String> stream) {
        return stream.collect(Collectors.joining(",", "(", ")"));
    }

    /**
     * Collects the {@link Stream} of {@link String} within {@code ()} with {@code ,} as the delimiter.
     *
     * @param stream the {@link Stream} to collect
     * @param delimiter the delimiter
     *
     * @return the joined {@link String}
     */
    @NotNull
    public static String toJoinedString(@NotNull final Stream<String> stream, @NotNull final String delimiter) {
        return stream.collect(Collectors.joining(delimiter, "", ""));
    }
}