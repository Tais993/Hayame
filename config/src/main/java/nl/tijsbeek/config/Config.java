package nl.tijsbeek.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

/**
 * Contains the config's variables
 */
public class Config {

    private final String discordToken;
    private final int prometheusBotPort;
    private final String prometheusPort;
    private final String grafanaPort;
    private final String grafanaKey;
    private final String databasePort;
    private final String databaseUsername;
    private final String databasePassword;

    /**
     * Creates an instance
     *
     * @param discordToken the discord-token
     * @param prometheusBotPort the port to run the bot server for prometheus on
     * @param prometheusPort the port prometheus runs on
     * @param databasePort the port of the DB
     * @param grafanaPort the port of Grafana
     * @param grafanaKey an API key of Grafana
     * @param databaseUsername the username of the DB
     * @param databasePassword the password of the DB
     */
    @JsonCreator
    @Contract(pure = true)
    public Config(@JsonProperty("discord_token") String discordToken,
                  @JsonProperty("prometheus_bot_port") String prometheusBotPort,
                  @JsonProperty("prometheus_port") String prometheusPort,
                  @JsonProperty("grafana_port") String grafanaPort,
                  @JsonProperty("grafana_key") String grafanaKey,
                  @JsonProperty("database_port") String databasePort,
                  @JsonProperty("database_username") String databaseUsername,
                  @JsonProperty("database_password") String databasePassword) {

        this.discordToken = discordToken;
        this.prometheusBotPort = Integer.parseInt(prometheusBotPort);
        this.prometheusPort = prometheusPort;
        this.grafanaPort = grafanaPort;
        this.grafanaKey = grafanaKey;
        this.databasePort = databasePort;
        this.databaseUsername = databaseUsername;
        this.databasePassword = databasePassword;
    }

    /**
     * The Discord token
     * @return the Discord token
     */
    public String getDiscordToken() {
        return discordToken;
    }

    /**
     * the port to the bot server for prometheus on
     * @return the port to the bot server for prometheus on
     */
    public int getPrometheusBotPort() {
        return prometheusBotPort;
    }

    /**
     * the port prometheus runs on
     * @return the port prometheus runs on
     */
    public String getPrometheusPort() {
        return prometheusPort;
    }

    /**
     * The port of Grafana
     * @return the port of Grafana
     */
    public String getGrafanaPort() {
        return grafanaPort;
    }

    /**
     * An API key of grafana
     * @return an API key of Grafana
     */
    public String getGrafanaKey() {
        return grafanaKey;
    }

    /**
     * The port of the DB
     * @return the port of the DB
     */
    public String getDatabasePort() {
        return databasePort;
    }

    /**
     * The username of the DB
     * @return the username of the DB
     */
    public String getDatabaseUsername() {
        return databaseUsername;
    }

    /**
     * The password of the DB
     * @return the password of the DB
     */
    public String getDatabasePassword() {
        return databasePassword;
    }

    /**
     * Creates an instance based of the URL of the file.
     *
     * @param configUrl a valid URL
     *
     * @return the Config
     *
     * @throws IOException if a low-level I/O error occurs.
     */
    @NotNull
    public static Config loadInstance(@NotNull final String configUrl) throws IOException {
        return new ObjectMapper().readValue(new File(configUrl), Config.class);
    }

    /**
     * Creates an instance based of the given JSON.
     *
     * @param configJson a valid JSON
     *
     * @return the Config
     *
     * @throws JsonProcessingException when invalid syntax is found
     */
    @NotNull
    public static Config byString(@Language("json") @NotNull final String configJson) throws JsonProcessingException {
        return new ObjectMapper().readValue(configJson, Config.class);
    }
}
