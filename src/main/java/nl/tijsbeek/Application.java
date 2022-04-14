package nl.tijsbeek;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import nl.tijsbeek.config.Config;
import nl.tijsbeek.database.databases.Database;
import nl.tijsbeek.discord.system.CommandHandler;
import nl.tijsbeek.discord.system.EventHandler;
import nl.tijsbeek.discord.system.ListenersList;
import nl.tijsbeek.grafana.GrafanaSetup;
import nl.tijsbeek.prometheus.MetricsHandler;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.io.IOException;

/**
 * Contains the entry point of the application.
 */
public final class Application {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);
    public static final String HAMAYE_CONFIG_LOCATION = "hamaye.config.location";

    @Contract(pure = true)
    private Application() {}

    /**
     * The entry point.
     *
     * @param args a {@link String} array
     *
     * @throws LoginException {@link JDABuilder#build()}
     * @throws IOException {@link Config#loadInstance(String)}
     */
    public static void main(@NotNull final String @NotNull [] args) throws LoginException, IOException, InterruptedException {
        String configLocation = System.getProperty(HAMAYE_CONFIG_LOCATION);

        if (configLocation == null) {
            configLocation = System.getenv(HAMAYE_CONFIG_LOCATION);
        }

        if (configLocation == null) {
            System.out.println(System.getenv());
            throw new IllegalArgumentException("Missing config location! Set a system env variable (%s)".formatted(HAMAYE_CONFIG_LOCATION));
        }

        Config config = Config.loadInstance(configLocation);

        Database database = new Database(config);

        ListenersList listenersList = new ListenersList(database);

        CommandHandler commandHandler = new CommandHandler(database, listenersList);
        EventHandler eventHandler = new EventHandler(listenersList);

        MetricsHandler matricsHandler = new MetricsHandler(commandHandler, config);

        JDABuilder.create(config.getDiscordToken(), eventHandler.getGatewayIntents())
                .enableIntents(GatewayIntent.GUILD_MEMBERS)
                .enableCache(eventHandler.getCacheFlags())
                .setMemberCachePolicy(MemberCachePolicy.NONE)
                .addEventListeners(commandHandler, eventHandler, matricsHandler)
                .build();

        try {
            new GrafanaSetup(commandHandler, config);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}