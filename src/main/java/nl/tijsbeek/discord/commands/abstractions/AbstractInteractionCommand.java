package nl.tijsbeek.discord.commands.abstractions;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.components.ActionComponent;
import net.dv8tion.jda.api.interactions.components.ComponentInteraction;
import nl.tijsbeek.database.Database;
import nl.tijsbeek.discord.commands.InteractionCommand;
import nl.tijsbeek.discord.commands.InteractionCommandState;
import nl.tijsbeek.discord.commands.InteractionCommandVisibility;
import nl.tijsbeek.discord.components.ComponentDatabase;
import org.jetbrains.annotations.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * An abstraction of {@link InteractionCommand} with the intention to improve developer experience.
 * <p/>
 * This implements methods like {@link #getData()}, {@link #getVisibility()} and {@link #getState()} for you. Their values are
 * inserted within the constructor.
 * <p/>
 * Methods like {@link #addEnabledGuilds(Long...)}, {@link #addRequiredBotPermission(Permission...)} and {@link #addRequiredUserPermission(Permission...)} exist to improve the experience.
 */
public abstract class AbstractInteractionCommand implements InteractionCommand {
    private Database database;
    private ComponentDatabase componentDatabase;

    private final CommandData data;
    private final InteractionCommandVisibility visibility;
    private final Collection<Long> enabledGuilds = new ArrayList<>(0);
    private final Collection<Permission> requiredUserPermission = new ArrayList<>(0);
    private final Collection<Permission> requiredBotPermission = new ArrayList<>(0);

    private final InteractionCommandState state;

    protected AbstractInteractionCommand(@NotNull final CommandData data, @NotNull final InteractionCommandVisibility visibility) {
        this(data, visibility, InteractionCommandState.ENABLED);
    }

    protected AbstractInteractionCommand(@NotNull final CommandData data, @NotNull final InteractionCommandVisibility visibility,
                                         @NotNull final InteractionCommandState state) {
        this.data = data;
        this.visibility = visibility;
        this.state = state;
    }

    public void setDatabase(final Database database) {
        this.database = database;
        this.componentDatabase = new ComponentDatabase(database.getDataSource());
    }

    /**
     * Adds the given guild ID's to a List that will be returned when calling {@link #getEnabledGuilds()}
     *
     * @param guildIds an array of guild ids
     */
    protected final void addEnabledGuilds(@NotNull final Long... guildIds) {
        enabledGuilds.addAll(List.of(guildIds));
    }

    /**
     * Adds the given {@link Permission permissions} to a List that will be returned when calling {@link #getRequiredUserPermission()}
     *
     * @param userPermissions an array of {@link Permission permissions} the user needs
     */
    protected final void addRequiredUserPermission(@NotNull final Permission... userPermissions) {
        requiredUserPermission.addAll(List.of(userPermissions));
    }

    /**
     * Adds the given {@link Permission permissions} to a List that will be returned when calling {@link #getRequiredBotPermission()}
     *
     * @param botPermissions an array of {@link Permission permissions} the bot needs
     */
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

    /**
     * Generates a component/modal ID.
     * <br/>
     * Shortcut for {@link ComponentDatabase#createId(String, Integer, LocalDateTime, String...)}.
     * The component doesn't expire, you can set an expiration date using {@link #generateId(LocalDateTime, String...)}.
     *
     * @param arguments the arguments
     * @return the ID
     * @see #generateId(LocalDateTime, String...)
     */
    public @NotNull String generateId(@NotNull final String... arguments) {
        return generateId(null, arguments);
    }

    /**
     * Generates a component/modal ID.
     * <br/>
     * Shortcut for {@link ComponentDatabase#createId(String, Integer, LocalDateTime, String...)}.
     *
     * @param expirationDate the date for the component to expire
     * @param arguments      the arguments
     * @return the ID
     * @see #generateId(String...)
     */
    public @NotNull String generateId(@Nullable final LocalDateTime expirationDate, @NotNull final String... arguments) {
        return componentDatabase.createId(getName(), getType().getId(), expirationDate, arguments);
    }

    /**
     * Get the argument of the given {@link ComponentInteraction}
     *
     * @param event the event to get the ID from
     *
     * @return an unmodifiable {@link List} with the arguments
     *
     * @see ComponentDatabase#retrieveComponentEntity(String)
     */
    public List<String> getArgumentsComponent(@NotNull final ComponentInteraction event) {
        Objects.requireNonNull(event, "The given event cannot be null");

        return getArgumentsComponent(event.getComponentId());
    }

    /**
     * Get the argument of the given {@link ActionComponent}
     *
     * @param component the component to get the ID from
     *
     * @return an unmodifiable {@link List} with the arguments
     *
     * @throws IllegalArgumentException when the given Component has no ID
     *
     * @see ComponentDatabase#retrieveComponentEntity(String)
     */
    public List<String> getArgumentsComponent(@NotNull final ActionComponent component) {
        Objects.requireNonNull(component, "The given ActionComponent cannot be null");

        if (null == component.getId()) {
            throw new IllegalArgumentException("The given component does not have an ID! All components should have an ID");
        }

        return getArgumentsComponent(component.getId());
    }

    /**
     * Returns the arguments of the given ID.
     *
     * @param id the ID to retrieve the arguments from
     *
     * @return an unmodifiable {@link List} with the arguments
     *
     * @see ComponentDatabase#retrieveComponentEntity(String)
     */
    @NotNull
    @Unmodifiable
    public List<String> getArgumentsComponent(@NotNull final String id) {
        Objects.requireNonNull(id, "The given ID cannot be null");

        return componentDatabase.retrieveComponentEntity(id).getArguments();
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