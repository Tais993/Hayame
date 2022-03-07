package nl.tijsbeek.discord.components;

import org.jetbrains.annotations.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class ComponentEntity {

    private final String id;
    private final String listenerId;
    private final LocalDateTime expireDate;
    private final List<String> arguments;

    @Contract(pure = true)
    public ComponentEntity(@NotNull final String id, @Nullable final String listenerId, @Nullable final LocalDateTime expireDate, @NotNull final List<String> arguments) {
        this.id = id;
        this.listenerId = listenerId;
        this.expireDate = expireDate;
        this.arguments = Collections.unmodifiableList(arguments);
    }

    @NotNull
    @Contract("_ -> new")
    public static ComponentEntity empty(@NotNull final String id) {
        return new ComponentEntity(id, null, null, Collections.emptyList());
    }

    @NotNull
    public String getId() {
        return id;
    }

    @Nullable
    public String getListenerId() {
        return listenerId;
    }

    @Nullable
    public LocalDateTime getExpireDate() {
        return expireDate;
    }

    public boolean isExpired() {
        if (expireDate == null) {
            return false;
        } else {
            return LocalDateTime.now().isAfter(expireDate);
        }
    }

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