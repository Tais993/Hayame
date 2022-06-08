package nl.tijsbeek.database.tables;

import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class UserProfile {
    private final long userId;

    private String description;

    private List<? super Field> fields = Collections.emptyList();
    private Map<String, UserSocial> socials = Collections.emptyMap();

    @Contract(pure = true)
    public UserProfile(final long userId) {
        this.userId = userId;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public void setFields(@NotNull final List<? super Field> fields) {
        this.fields = new ArrayList<>(Objects.requireNonNull(fields, "Fields cannot be null!"));
    }

    public void setField(final int number, @NotNull final String title, @NotNull final String content) {
        fields.set(number, new Field(title, content, false));
    }

    public void setSocials(@NotNull final Collection<? extends UserSocial> socials) {
        this.socials = Objects.requireNonNull(socials, "Socials cannot be null!").stream()
                .collect(Collectors.toMap(UserSocial::getPlatformName, Function.identity()));
    }


    public long getUserId() {
        return userId;
    }

    public @Nullable String getDescription() {
        return description;
    }

    public @NotNull List<? super Field> getFields() {
        return Collections.unmodifiableList(fields);
    }

    public @NotNull Map<String, UserSocial> getSocials() {
        return Collections.unmodifiableMap(socials);
    }

    public @NotNull Collection<UserSocial> getSocialsCollection() {
        return socials.values();
    }
}
