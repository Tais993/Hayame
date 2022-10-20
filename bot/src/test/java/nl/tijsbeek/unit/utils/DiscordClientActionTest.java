package nl.tijsbeek.unit.utils;

import nl.tijsbeek.utils.DiscordClientAction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DiscordClientActionTest {

    private static final String URL = "custom-url/test/{ARGUMENT}";
    private static final String RAW_FORMATTED_URL = "discord://-/custom-url/test/{ARGUMENT}";
    private static final String ARGUMENT = "argument";
    private static final String FORMATTED_URL_WITH_ARGUMENT = "discord://-/custom-url/test/argument";

    @Test
    @DisplayName("Creates action of custom URL, and checks whenever it throws when formatting without arguments.")
    void ofCustomUrl() {
        DiscordClientAction clientAction = DiscordClientAction.ofCustomUrl(URL);

        assertEquals(RAW_FORMATTED_URL,  clientAction.getRawUrl());
        assertThrows(IllegalArgumentException.class, clientAction::formatUrl);
    }

    @Test
    @DisplayName("Creates action of custom URL, and checks whenever the argument gets correctly inserted.")
    void formatUrl() {
        DiscordClientAction clientAction = DiscordClientAction.ofCustomUrl(URL);

        assertEquals(FORMATTED_URL_WITH_ARGUMENT, clientAction.formatUrl(ARGUMENT));
        assertThrows(IllegalArgumentException.class, clientAction::formatUrl);
    }
}