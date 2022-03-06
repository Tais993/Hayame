package nl.tijsbeek.discord.components;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.ICSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import nl.tijsbeek.config.Config;
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

public final class ComponentDatabase {
    private static final Logger logger = LoggerFactory.getLogger(ComponentDatabase.class);

    private final HikariDataSource dataSource;

    public ComponentDatabase(@NotNull final Config config) {

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl("jdbc:mariadb://localhost:%s/discordbot".formatted(config.getDatabasePort()));
        hikariConfig.setUsername(config.getDatabaseUsername());
        hikariConfig.setPassword(config.getDatabasePassword());
        hikariConfig.addDataSourceProperty("passwordCharacterEncoding", "UTF-8");
        dataSource = new HikariDataSource(hikariConfig);
    }

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

    @NotNull
    @Unmodifiable
    private static List<String> csvStringToArguments(@NotNull final String csv) {
        try (CSVReader reader = new CSVReader(new StringReader(csv))) {
            return List.of(reader.readNext());
        } catch (IOException | CsvValidationException e) {
            logger.error("Something went wrong while reading component arguments to a CSV String.", e);
            return Collections.emptyList();
        }
    }

    @Nullable
    public String createId(@NotNull final LocalDateTime localDateTime, String... arguments) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                     INSERT INTO discordbot.component (expire_date, arguments)
                     VALUES (?, ?)
                     RETURNING id
                     """)) {

            statement.setObject(1, localDateTime);
            statement.setObject(2, argumentsToCsvString(arguments));

            statement.execute();

            //noinspection JDBCResourceOpenedButNotSafelyClosed - it's automatically closed by the statement
            ResultSet resultSet = statement.getResultSet();
            resultSet.first();
            return resultSet.getString(1);
        } catch (SQLException e) {
            logger.error("Something went wrong while inserting a component into the DB.", e);
            throw new RuntimeException(e);
        }
    }


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


    @NotNull
    @Contract("_ -> new")
    public ComponentEntity retrieveComponentEntity(@NotNull final String id) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                     SELECT *
                     FROM discordbot.component
                     WHERE id = ?
                     """)) {

            statement.setObject(1, id);

            statement.execute();

            //noinspection JDBCResourceOpenedButNotSafelyClosed - it's automatically closed by the statement
            ResultSet resultSet = statement.getResultSet();

            String listenerId = resultSet.getObject(2, String.class);
            LocalDateTime epireDate = resultSet.getObject(3, LocalDateTime.class);
            List<String> arguments = csvStringToArguments(resultSet.getObject(4, String.class));
            return new ComponentEntity(id, listenerId, epireDate, arguments);

        } catch (SQLException e) {
            logger.error("Something went wrong while retrieving a component from the DB.", e);
            return ComponentEntity.empty(id);
        }
    }
}