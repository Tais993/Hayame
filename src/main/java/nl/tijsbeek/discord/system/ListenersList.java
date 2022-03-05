package nl.tijsbeek.discord.system;

import nl.tijsbeek.discord.commands.InteractionCommand;
import nl.tijsbeek.discord.events.CustomEventListener;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListenersList {

    private final List<InteractionCommand> commands;
    private final List<CustomEventListener> eventListeners;

    public ListenersList() {
        List<CustomEventListener> eventListeners = new ArrayList<>(20);

        // TEMPORARY PLACEHOLDER AGAINST WARNINGS
        eventListeners.contains(null);


        List<InteractionCommand> commands = new ArrayList<>(20);

        // TEMPORARY PLACEHOLDER AGAINST WARNINGS
        commands.contains(null);


        this.eventListeners = eventListeners;
        this.commands = commands;
    }

    @NotNull
    @UnmodifiableView
    @Contract(pure = true)
    public List<InteractionCommand> getCommands() {
        return Collections.unmodifiableList(commands);
    }

    @NotNull
    @UnmodifiableView
    @Contract(pure = true)
    public List<CustomEventListener> getEventListeners() {
        return Collections.unmodifiableList(eventListeners);
    }
}