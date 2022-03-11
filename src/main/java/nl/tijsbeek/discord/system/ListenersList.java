package nl.tijsbeek.discord.system;

import nl.tijsbeek.discord.commands.InteractionCommand;
import nl.tijsbeek.discord.commands.commands.context.message.AaaaaMCommand;
import nl.tijsbeek.discord.commands.commands.context.user.AaaaaUCommand;
import nl.tijsbeek.discord.commands.commands.slash.GrrrCommand;
import nl.tijsbeek.discord.commands.commands.slash.TestCommand;
import nl.tijsbeek.discord.events.CustomEventListener;
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
    public ListenersList() {
        List<CustomEventListener> eventListeners = new ArrayList<>(20);

        // TEMPORARY PLACEHOLDER AGAINST WARNINGS
        eventListeners.contains(null);


        List<InteractionCommand> commands = new ArrayList<>(20);

        // TEMPORARY PLACEHOLDER AGAINST WARNINGS
        commands.add(new TestCommand());
        commands.add(new GrrrCommand());
        commands.add(new AaaaaMCommand());
        commands.add(new AaaaaUCommand());


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