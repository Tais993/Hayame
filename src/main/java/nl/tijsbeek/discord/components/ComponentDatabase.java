package nl.tijsbeek.discord.components;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.ICSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import nl.tijsbeek.config.Config;
import org.flywaydb.core.Flyway;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * The database handler for components.
 */
public final class ComponentDatabase {
    private static final Logger logger = LoggerFactory.getLogger(ComponentDatabase.class);
    private static final String DB_SCHEMA_BOT = "discordbot";

    private final HikariDataSource dataSource;

    public ComponentDatabase(@NotNull final Config config) {

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl("jdbc:mariadb://localhost:%s/".formatted(config.getDatabasePort()));
        hikariConfig.setUsername(config.getDatabaseUsername());
        hikariConfig.setPassword(config.getDatabasePassword());
        hikariConfig.addDataSourceProperty("passwordCharacterEncoding", "UTF-8");
        hikariConfig.setSchema(DB_SCHEMA_BOT);
        dataSource = new HikariDataSource(hikariConfig);

        Flyway flyway =
                Flyway.configure()
                        .schemas(DB_SCHEMA_BOT)
                        .dataSource(dataSource)
                        .locations("classpath:/db/").load();
        flyway.migrate();

    }

    /**
     * Writes an array of arguments to a CSV string.
     *
     * @param arguments an array of arguments
     *
     * @return the arguments as a CSV string
     */
    @Nullable
    private static String argumentsToCsvString(@NotNull final String... arguments) {
        StringWriter writer = new StringWriter();

        try (ICSVWriter csvWriter = new CSVWriter(writer)) {
            csvWriter.writeNext(arguments);
            return writer.toString();
        } catch (IOException e) {
            logger.error("Something went wrong while writing component arguments to a CSV String.", e);
            return null;
        }
    }

    /**
     * Writes a CSV string to a {@link List} of arguments
     *
     * @param csv a CSV string
     *
     * @return the CSV String as a {@link List} of arguments
     */
    @NotNull
    @Unmodifiable
    private static List<String> csvStringToArguments(@Language("csv") @NotNull final String csv) {
        try (CSVReader reader = new CSVReader(new StringReader(csv))) {
            return List.of(reader.readNext());
        } catch (IOException | CsvValidationException e) {
            logger.error("Something went wrong while reading component arguments to a CSV String.", e);
            return Collections.emptyList();
        }
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
            statement.setObject(4, argumentsToCsvString(arguments));

            statement.execute();

            //noinspection JDBCResourceOpenedButNotSafelyClosed - it's automatically closed by the statement
            ResultSet resultSet = statement.getResultSet();
            resultSet.first();
            return Objects.requireNonNull(resultSet.getString(1), "Somehow the ID returned by  the DB is null, something went exremely wrong");
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
            LocalDateTime epireDate = resultSet.getObject(4, LocalDateTime.class);
            List<String> arguments = csvStringToArguments(resultSet.getObject(5, String.class));
            return new ComponentEntity(id, listenerId, commandType, epireDate, arguments);

        } catch (SQLException e) {
            logger.error("Something went wrong while retrieving a component from the DB.", e);
            return ComponentEntity.empty(id);
        }
    }
}