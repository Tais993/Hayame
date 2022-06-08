package nl.tijsbeek.database.databases;

import nl.tijsbeek.database.tables.UserProfile;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;

public class UserProfileDatabase extends AbstractDatabase<UserProfile> implements IStringDatabase<UserProfile> {
    private static final Logger logger = LoggerFactory.getLogger(UserProfileDatabase.class);

    @Contract(pure = true)
    UserProfileDatabase(@NotNull final Database database) {
        super(database.getDataSource());
    }

    @Override
    public UserProfile retrieveById(long id) {
        return withReturn("""
                SELECT *
                FROM discordbot.user_profiles
                WHERE id = ?
                """, setIdLongConsumer(id), this::resultSetToUserProfiles);
    }

    @Override
    public UserProfile deleteById(long id) {
        return null;
    }

    @Override
    public void insert(@NotNull UserProfile userProfileDatabase) {

    }

    @Override
    public void replace(@NotNull UserProfile userProfileDatabase) {

    }


    @Contract(pure = true)
    private @Nullable UserProfile resultSetToUserProfiles(ResultSet resultSet) {
        return null;
    }
}
