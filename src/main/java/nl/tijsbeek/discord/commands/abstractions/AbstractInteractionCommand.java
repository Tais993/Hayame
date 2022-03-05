package nl.tijsbeek.discord.commands.abstractions;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import nl.tijsbeek.discord.commands.InteractionCommand;
import nl.tijsbeek.discord.commands.InteractionCommandState;
import nl.tijsbeek.discord.commands.InteractionCommandVisibility;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class AbstractInteractionCommand implements InteractionCommand {

    private final CommandData data;
    private final InteractionCommandVisibility visibility;
    private final Collection<Long> enabledGuilds = new ArrayList<>(0);
    private final Collection<Permission> requiredUserPermission = new ArrayList<>(0);
    private final Collection<Permission> requiredBotPermission = new ArrayList<>(0);

    private final InteractionCommandState state;

    protected AbstractInteractionCommand(@NotNull final CommandData data, @NotNull final InteractionCommandVisibility visibility,
                                         @NotNull final InteractionCommandState state) {
        this.data = data;
        this.visibility = visibility;
        this.state = state;
    }

    protected final void addEnabledGuilds(@NotNull final Long... guildIds) {
        enabledGuilds.addAll(List.of(guildIds));
    }

    protected final void addRequiredUserPermission(@NotNull final Permission... userPermissions) {
        requiredUserPermission.addAll(List.of(userPermissions));
    }

    protected final void addRequiredBotPermission(@NotNull final Permission... botPermissions) {
        requiredBotPermission.addAll(List.of(botPermissions));
    }

    @NotNull
    @Override
    public CommandData getData() {
        return data;
    }

    @NotNull
    @Override
    public InteractionCommandVisibility getVisibility() {
        return visibility;
    }


    @NotNull
    @Override
    @UnmodifiableView
    public Collection<Long> getEnabledGuilds() {
        return Collections.unmodifiableCollection(enabledGuilds);
    }

    @NotNull
    @Override
    @UnmodifiableView
    public Collection<Permission> getRequiredUserPermission() {
        return Collections.unmodifiableCollection(requiredUserPermission);
    }

    @NotNull
    @Override
    @UnmodifiableView
    public Collection<Permission> getRequiredBotPermission() {
        return Collections.unmodifiableCollection(requiredBotPermission);
    }

    @NotNull
    @Override
    public InteractionCommandState getState() {
        return state;
    }


    @Override
    public void onSelectMenuInteraction(@NotNull final SelectMenuInteractionEvent event) {
    }

    @Override
    public void onButtonInteraction(@NotNull final ButtonInteractionEvent event) {
    }

    @Override
    public void onModalInteraction(@NotNull final ModalInteractionEvent event) {
    }

    @NonNls
    @NotNull
    @Override
    public String toString() {
        return "AbstractInteractionCommand{" +
                "data=" + data +
                ", visibility=" + visibility +
                ", enabledGuilds=" + enabledGuilds +
                ", requiredUserPermission=" + requiredUserPermission +
                ", requiredBotPermission=" + requiredBotPermission +
                ", state=" + state +
                '}';
    }
}