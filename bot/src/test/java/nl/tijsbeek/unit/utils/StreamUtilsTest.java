package nl.tijsbeek.unit.utils;

import nl.tijsbeek.utils.StreamUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StreamUtilsTest {

    @Test
    @DisplayName("Stream to joined String with default delimiter & (pre/suf)fix")
    void toJoinedString() {
        String result = "(test, YEP, pog)";

        Stream<String> stream = Stream.of("test", "YEP", "pog");

        assertEquals(result, StreamUtils.toJoinedString(stream));
    }

    @Test
    @DisplayName("Stream to joined String with custom delimiter")
    void testToJoinedString() {
        String result = "test, YEP, pog";

        Stream<String> stream = Stream.of("test", "YEP", "pog");

        assertEquals(result, StreamUtils.toJoinedString(stream, ", "));
    }
}