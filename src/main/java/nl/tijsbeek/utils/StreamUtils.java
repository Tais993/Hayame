package nl.tijsbeek.utils;

import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class StreamUtils {

    @NotNull
    public static String toJoinedString(@NotNull final Stream<String> stream) {
        return stream.collect(Collectors.joining(",", "(", ")"));
    }
}