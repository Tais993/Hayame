package nl.tijsbeek.database.databases;

import com.diffplug.common.base.Errors;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import nl.tijsbeek.database.tables.UserProfile;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class UserProfileDatabase extends AbstractDatabase<UserProfile> implements IStringDatabase<UserProfile> {
    private static final Logger logger = LoggerFactory.getLogger(UserProfileDatabase.class);

    private final Databases databases;

    @Contract(pure = true)
    UserProfileDatabase(@NotNull final Databases databases) {
        super(databases.getDataSource());

        this.databases = databases;
    }

    @Override
    public UserProfile retrieveById(final long id) {
        return withReturn("""
                SELECT *
                FROM discordbot.user_profiles
                WHERE id = ?
                """, setIdLongConsumer(id), this::resultSetToUserProfile);
    }

    @Override
    public UserProfile deleteById(final long id) {
        return withReturn("""
                DELETE FROM discordbot.user_profiles
                WHERE id = ?
                RETURNING *
                """, setIdLongConsumer(id), this::resultSetToUserProfile);
    }

    @Override
    public void insert(@NotNull final UserProfile userProfile) {
        withoutReturn("""
                INSERT INTO discordbot.user_profiles (id, description, field_one_name, field_one_content, field_two_name, field_two_content, field_three_name, field_three_content)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?);
                """, setUserProfile(userProfile));
    }

    @Override
    public void replace(@NotNull final UserProfile userProfile) {
        withoutReturn("""
                REPLACE INTO discordbot.user_profiles (id, description, field_one_name, field_one_content, field_two_name, field_two_content, field_three_name, field_three_content)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """, setUserProfile(userProfile));
    }


    @Contract(pure = true)
    private UserProfile resultSetToUserProfile(@NotNull final ResultSet resultSetParam) {
        return Errors.rethrow().wrap((ResultSet resultSet) -> {

            List<MessageEmbed.Field> fields = new ArrayList<>(3);

            fields.add(new Field(
                    resultSet.getString("field_one_name"),
                    resultSet.getString("field_one_content"),
                    false
            ));

            fields.add(new Field(
                    resultSet.getString("field_two_name"),
                    resultSet.getString("field_two_content"),
                    false
            ));

            fields.add(new Field(
                    resultSet.getString("field_three_name"),
                    resultSet.getString("field_three_content"),
                    false
            ));

            return new UserProfile(databases, resultSet.getLong("id"))
                    .setDescription(resultSet.getString("description"))
                    .setFields(fields);
        }).apply(resultSetParam);
    }

    private static Consumer<? super PreparedStatement> setUserProfile(final UserProfile userProfile) {
        return Errors.rethrow().wrap(statement -> {
            int i = 1;

            statement.setLong(i++, userProfile.getUserId());

            statement.setString(i++, userProfile.getDescription());

            for (final MessageEmbed.Field field : userProfile.getFields()) {
                statement.setString(i++, field.getName());
                statement.setString(i++, field.getValue());
            }
        });
    }
}
