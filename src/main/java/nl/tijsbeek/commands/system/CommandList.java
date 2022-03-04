package nl.tijsbeek.commands.system;

import nl.tijsbeek.commands.InteractionCommand;

import java.util.ArrayList;
import java.util.List;

public class CommandList {
    public static List<InteractionCommand> getCommands() {
        List<InteractionCommand> commands = new ArrayList<>(20);

        // TEMPORARY PLACEHOLDER AGAINST WARNINGS
        commands.contains(null);

        return commands;
    }
}
