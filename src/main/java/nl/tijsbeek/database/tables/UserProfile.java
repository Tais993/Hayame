package nl.tijsbeek.database.tables;

import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import nl.tijsbeek.database.databases.Databases;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class UserProfile {
    private final Databases databases;

    private final long userId;

    private String description;

    private List<Field> fields = Collections.emptyList();

    @Contract(pure = true)
    public UserProfile(final Databases databases, final long userId) {
        this.databases = databases;
        this.userId = userId;
    }

    public UserProfile setDescription(final String description) {
        this.description = description;
        return this;
    }

    public UserProfile setFields(final @NotNull List<? extends Field> fields) {
        this.fields = new ArrayList<>(Objects.requireNonNull(fields, "Fields cannot be null!"));
        return this;
    }

    public UserProfile setField(final int number, @NotNull final String title, @NotNull final String content) {
        fields.set(number, new Field(title, content, false));
        return this;
    }

    public long getUserId() {
        return userId;
    }

    public @Nullable String getDescription() {
        return description;
    }

    public @NotNull List<Field> getFields() {
        return Collections.unmodifiableList(fields);
    }

    public @NotNull List<UserSocial> retrieveSocials() {
        return databases.getUserSocialDatabase().retrieveById(userId);
    }

    public void replaceSocials(@NotNull final List<@NotNull UserSocial> socials) {
        databases.getUserSocialDatabase().replace(socials);
    }
}
