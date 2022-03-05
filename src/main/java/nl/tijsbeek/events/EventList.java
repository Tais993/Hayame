package nl.tijsbeek.events;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class EventList {

    @NotNull
    public static List<CustomEventListener> getCommands() {
        List<CustomEventListener> commands = new ArrayList<>(20);

        // TEMPORARY PLACEHOLDER AGAINST WARNINGS
        commands.contains(null);

        return commands;
    }
}