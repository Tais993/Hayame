package nl.tijsbeek;

import net.dv8tion.jda.api.JDABuilder;
import nl.tijsbeek.commands.system.CommandHandler;
import nl.tijsbeek.events.EventHandler;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;

public final class Application {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    @Contract(pure = true)
    private Application() {}

    public static void main(@NotNull final String @NotNull [] args) throws LoginException {
        if (args.length <= 0) {
            throw new IllegalArgumentException("Missing token!");
        }

        String token = args[0];

        CommandHandler commandHandler = new CommandHandler();
        EventHandler eventHandler = new EventHandler();

        JDABuilder.create(token, eventHandler.getGatewayIntents())
                .enableCache(eventHandler.getCacheFlags())
                .addEventListeners(commandHandler, eventHandler)
                .build();
    }
}