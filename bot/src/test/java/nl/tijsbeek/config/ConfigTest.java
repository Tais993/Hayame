package nl.tijsbeek.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.intellij.lang.annotations.Language;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConfigTest {

    @Test
    void byString() throws JsonProcessingException {
        String discordToken = "FAKE-DISCORD-TOKEN";
        int prometheusBotPort = 9091;
        String prometheusPort = "9091";
        String grafanaPort = "3000";
        String grafanaKey = "FAKE-GRAFANA-KEY";
        String databasePort = "3306";
        String databaseUsername = "root";
        String databasePassword = "FAKE-DB-PASSWORD";

        @Language("json") String json = """
                {
                  "discord_token" : "%s",
                  "prometheus_bot_port" : "%s", 
                  "prometheus_port" : "%s",
                  "grafana_port" : "%s",
                  "grafana_key" : "%s",
                  "database_port" : "%s",
                  "database_username" : "%s",
                  "database_password" : "%s"
                }
                """.formatted(discordToken, prometheusBotPort, prometheusPort, grafanaPort, grafanaKey, databasePort, databaseUsername, databasePassword);

        Config config = Config.byString(json);

        assertEquals(discordToken, config.getDiscordToken());
        assertEquals(prometheusBotPort, config.getPrometheusBotPort());
        assertEquals(prometheusPort, config.getPrometheusPort());
        assertEquals(grafanaPort, config.getGrafanaPort());
        assertEquals(grafanaKey, config.getGrafanaKey());
        assertEquals(databasePort, config.getDatabasePort());
        assertEquals(databaseUsername, config.getDatabaseUsername());
        assertEquals(databasePassword, config.getDatabasePassword());
    }
}
