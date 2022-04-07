package nl.tijsbeek.discord.components;

import com.zaxxer.hikari.HikariDataSource;
import nl.tijsbeek.database.Database;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * The database handler for components.
 */
public final class ComponentDatabase {
    private static final Logger logger = LoggerFactory.getLogger(ComponentDatabase.class);

    private final HikariDataSource dataSource;

    @Contract(pure = true)
    public ComponentDatabase(@NotNull final HikariDataSource dataSource) {
        this.dataSource = Objects.requireNonNull(dataSource, "Datasource cannot be null");
    }

    /**
     * Creates an ID, and inserts it into the DB
     *
     * @param expirationDate the date for when the component should expire
     * @param arguments the arguments of the ID, can be empty
     *
     * @return the ID
     */
    @NotNull
    public String createId(@Nullable String listenerId, @Nullable Integer commandType, @Nullable final LocalDateTime expirationDate, @NotNull String... arguments) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                     INSERT INTO discordbot.component (listener_id, command_type, expire_date, arguments)
                     VALUES (?, ?, ?, ?)
                     RETURNING id
                     """)) {

            statement.setObject(1, listenerId);
            statement.setObject(2, commandType);
            statement.setObject(3, expirationDate);
            statement.setObject(4, Database.argumentsToCsvString(arguments));

            statement.execute();

            //noinspection JDBCResourceOpenedButNotSafelyClosed - it's automatically closed by the statement
            ResultSet resultSet = statement.getResultSet();
            resultSet.first();
            return Objects.requireNonNull(resultSet.getString(1), "Somehow the ID returned by the DB is null, something went extremely wrong");
        } catch (SQLException e) {
            logger.error("Something went wrong while inserting a component into the DB.", e);
            throw new RuntimeException(e);
        }
    }


    /**
     * Removes component information by its ID
     *
     * @param id the ID to remove the component from
     */
    public void remove(@NotNull final String id) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                     DELETE FROM discordbot.component
                     WHERE id = ?
                     """)) {

            statement.setObject(1, id);

            statement.execute();

        } catch (SQLException e) {
            logger.error("Something went wrong while inserting a component into the DB.", e);
        }
    }


    /**
     * Retrieves component information based of its ID.
     *
     * @param id the ID of the component to retrieve
     *
     * @return the {@link ComponentEntity}
     */
    @NotNull
    @Contract("_ -> new")
    public ComponentEntity retrieveComponentEntity(@NotNull final String id) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                     SELECT *
                     FROM discordbot.component
                     WHERE id = ?
                     """)) {

            statement.setInt(1, Integer.parseInt(id));

            statement.execute();

            //noinspection JDBCResourceOpenedButNotSafelyClosed - it's automatically closed by the statement
            ResultSet resultSet = statement.getResultSet();

            resultSet.first();

            String listenerId = resultSet.getObject(2, String.class);
            int commandType = resultSet.getInt(3);
            LocalDateTime expireDate = resultSet.getObject(4, LocalDateTime.class);
            List<String> arguments = Database.csvStringToArguments(resultSet.getObject(5, String.class));
            return new ComponentEntity(id, listenerId, commandType, expireDate, arguments);

        } catch (SQLException e) {
            logger.error("Something went wrong while retrieving a component from the DB.", e);
            return ComponentEntity.empty(id);
        }
    }

    @NonNls
    @NotNull
    @Override
    @Contract(pure = true)
    public String toString() {
        return "ComponentDatabase{" +
                "dataSource=" + dataSource +
                '}';
    }
}