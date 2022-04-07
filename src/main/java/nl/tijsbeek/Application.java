package nl.tijsbeek;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import nl.tijsbeek.config.Config;
import nl.tijsbeek.database.Database;
import nl.tijsbeek.discord.components.ComponentDatabase;
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
    public static void main(@NotNull final String @NotNull [] args) throws LoginException, IOException {
        if (1 > args.length) {
            throw new IllegalArgumentException("Missing config location!");
        }

        String configLocation = args[0];

        Config config = Config.loadInstance(configLocation);

        Database database = new Database(config);

        ListenersList listenersList = new ListenersList();

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