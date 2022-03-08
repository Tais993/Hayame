package nl.tijsbeek;

import net.dv8tion.jda.api.JDABuilder;
import nl.tijsbeek.config.Config;
import nl.tijsbeek.discord.components.ComponentDatabase;
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
        if (args.length < 1) {
            throw new IllegalArgumentException("Missing config location!");
        }

        String configLocation = args[0];

        Config config = Config.loadInstance(configLocation);

        ComponentDatabase componentHandler = new ComponentDatabase(config);

//        ListenersList listenersList = new ListenersList();
//
//        CommandHandler commandHandler = new CommandHandler(componentHandler, listenersList);
//        EventHandler eventHandler = new EventHandler(listenersList);
//
//        JDABuilder.create(config.getDiscordToken(), eventHandler.getGatewayIntents())
//                .enableCache(eventHandler.getCacheFlags())
//                .addEventListeners(commandHandler, eventHandler)
//                .build();
    }
}