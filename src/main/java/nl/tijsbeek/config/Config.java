package nl.tijsbeek.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public class Config {

    private final String discordToken;
    private final String databasePort;
    private final String databaseUsername;
    private final String databasePassword;

    @JsonCreator
    @Contract(pure = true)
    public Config(@JsonProperty("discord_token") String discordToken,
                  @JsonProperty("database_port") String databasePort,
                  @JsonProperty("database_username") String databaseUsername,
                  @JsonProperty("database_password") String databasePassword) {

        this.discordToken = discordToken;
        this.databasePort = databasePort;
        this.databaseUsername = databaseUsername;
        this.databasePassword = databasePassword;
    }

    public String getDiscordToken() {
        return discordToken;
    }

    public String getDatabasePort() {
        return databasePort;
    }

    public String getDatabaseUsername() {
        return databaseUsername;
    }

    public String getDatabasePassword() {
        return databasePassword;
    }

    @NotNull
    public static Config loadInstance(@NotNull final String configUrl) throws IOException {
        return new ObjectMapper().readValue(new File(configUrl), Config.class);
    }
}