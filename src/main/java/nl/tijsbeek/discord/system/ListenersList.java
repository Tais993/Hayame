package nl.tijsbeek.discord.system;

import nl.tijsbeek.database.databases.Database;
import nl.tijsbeek.discord.commands.InteractionCommand;
import nl.tijsbeek.discord.commands.commands.context.message.AaaaaMCommand;
import nl.tijsbeek.discord.commands.commands.context.message.ReportMessageCommand;
import nl.tijsbeek.discord.commands.commands.context.user.AaaaaUCommand;
import nl.tijsbeek.discord.commands.commands.slash.*;
import nl.tijsbeek.discord.events.CustomEventListener;
import nl.tijsbeek.discord.events.listeners.GuildSettingsListener;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Contains a list of all {@link InteractionCommand InteractionCommands} and {@link CustomEventListener CustomEventListeners}.
 */
public class ListenersList {

    private final List<InteractionCommand> commands;
    private final List<CustomEventListener> eventListeners;

    /**
     * Creates an instance.
     */
    public ListenersList(@NotNull final Database database) {
        List<CustomEventListener> eventListeners = new ArrayList<>(20);

        eventListeners.add(new GuildSettingsListener(database));


        List<InteractionCommand> commands = new ArrayList<>(20);

        commands.add(new TestCommand());
        commands.add(new GrrrCommand());
        commands.add(new AaaaaMCommand());
        commands.add(new AaaaaUCommand());
        commands.add(new AskQuestionCommand());
        commands.add(new CoinFlip());
        commands.add(new EmbedCommand());
        commands.add(new SettingsCommand());
        commands.add(new ReportSlashCommand());
        commands.add(new ReportMessageCommand());

        this.eventListeners = eventListeners;
        this.commands = commands;
    }

    /**
     * Returns all {@link InteractionCommand InteractionCommands} in a {@link List}.
     *
     * @return a {@link List} of {@link InteractionCommand InteractionCommands}
     */
    @NotNull
    @UnmodifiableView
    @Contract(pure = true)
    public List<InteractionCommand> getCommands() {
        return Collections.unmodifiableList(commands);
    }

    /**
     * Returns all {@link CustomEventListener CustomEventListeners} in a {@link List}.
     *
     * @return a {@link List} of {@link CustomEventListener CustomEventListeners}
     */
    @NotNull
    @UnmodifiableView
    @Contract(pure = true)
    public List<CustomEventListener> getEventListeners() {
        return Collections.unmodifiableList(eventListeners);
    }
}