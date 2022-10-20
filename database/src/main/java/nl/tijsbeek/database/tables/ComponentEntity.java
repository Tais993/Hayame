package nl.tijsbeek.database.tables;

import org.jetbrains.annotations.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * The component's info represented as a DB entity
 */
public class ComponentEntity {

    private final String id;
    private final String listenerId;
    private final LocalDateTime expireDate;
    private final List<String> arguments;

    /**
     * Creates an instance.
     *
     * @param id the ID of the component
     * @param listenerId the ID of the listener, listening to this component
     * @param expireDate the date for when the component expires
     * @param arguments a list of all arguments
     */
    @Contract(pure = true)
    public ComponentEntity(@NotNull final String id, @Nullable final String listenerId, @Nullable final LocalDateTime expireDate, @NotNull final List<String> arguments) {
        this.id = Objects.requireNonNull(id, "The given id cannot be null");
        this.listenerId = listenerId;
        this.expireDate = expireDate;
        this.arguments = Collections.unmodifiableList(Objects.requireNonNull(arguments, "The given arguments cannot be null"));
    }

    /**
     * Creates an instance.
     *
     * @param listenerId the ID of the listener, listening to this component
     * @param expireDate the date for when the component expires
     * @param arguments a list of all arguments
     */
    @Contract(pure = true)
    public ComponentEntity(@Nullable final String listenerId, @Nullable final LocalDateTime expireDate, @NotNull final List<String> arguments) {
        this.id = null;
        this.listenerId = listenerId;
        this.expireDate = expireDate;
        this.arguments = Collections.unmodifiableList(Objects.requireNonNull(arguments, "The given arguments cannot be null"));
    }

    /**
     * Creates an empty instance where {@link #getListenerId()} and {@link #expireDate} return null.
     *
     * @param id the ID of the component
     *
     * @return an empty instance
     */
    @NotNull
    @Contract("_ -> new")
    public static ComponentEntity empty(@NotNull final String id) {
        return new ComponentEntity(id, null, null,Collections.emptyList());
    }

    /**
     * The ID of the component.
     *
     * @return the ID
     */
    @Nullable
    public String getId() {
        return id;
    }

    /**
     * The ID of the listener, or the command's name if a command.
     *
     * @return the listener's ID
     */
    @Nullable
    public String getListenerId() {
        return listenerId;
    }

    /**
     * The expiration date.
     *
     * @return the expiration date as a {@link LocalDateTime}
     *
     * @see #isExpired()
     */
    @Nullable
    public LocalDateTime getExpireDate() {
        return expireDate;
    }

    /**
     * Whenever this component is expired, false when null.
     *
     * @return whenever the component is expired
     */
    public boolean isExpired() {
        if (expireDate == null) {
            return false;
        } else {
            return LocalDateTime.now().isAfter(expireDate);
        }
    }

    /**
     * The components arguments.
     *
     * @return the arguments as an unmodifiable {@link List}
     */
    @NotNull
    @Unmodifiable
    public List<String> getArguments() {
        return arguments;
    }

    @NonNls
    @NotNull
    @Override
    public String toString() {
        return "ComponentEntity{" +
                "id='" + id + '\'' +
                ", expireDate=" + expireDate +
                ", arguments=" + arguments +
                '}';
    }
}
