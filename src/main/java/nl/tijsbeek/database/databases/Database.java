package nl.tijsbeek.database.databases;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.ICSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import nl.tijsbeek.config.Config;
import org.flywaydb.core.Flyway;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Contains all existing {@link IDatabase IDatabase's}, and the {@link javax.sql.DataSource}.
 *
 * This class is also responsible for DB migration at the moment of speaking, this will be moved to Gradle eventually.
 */
public class Database {
    private static final Logger logger = LoggerFactory.getLogger(Database.class);
    private static final String DB_SCHEMA_BOT = "discordbot";

    private final HikariDataSource dataSource;

    private final EmbedDatabase embedDatabase;
    private final ComponentDatabase componentDatabase;
    private final GuildSettingsDatabase guildSettingsDatabase;

    public Database(@NotNull final Config config) {
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
                        .locations("classpath:/db/migration").load();
        flyway.migrate();


        embedDatabase = new EmbedDatabase(this);
        componentDatabase = new ComponentDatabase(this);
        guildSettingsDatabase = new GuildSettingsDatabase(this);
    }

    /**
     * Writes an array of arguments to a CSV string.
     *
     * @param arguments an array of arguments
     *
     * @return the arguments as a CSV string
     */
    @Nullable
    public static String argumentsToCsvString(@NotNull final String... arguments) {
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
     * Writes an array of arguments to a CSV string.
     *
     * @param arguments an array of arguments
     *
     * @return the arguments as a CSV string
     */
    @Nullable
    public static String argumentsToCsvString(@NotNull final Collection<String> arguments) {
        return argumentsToCsvString(arguments.toArray(String[]::new));
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
    public static List<String> csvStringToArguments(@Language("csv") @Nullable final String csv) {
        if (csv == null) {
            return List.of();
        }

        try (CSVReader reader = new CSVReader(new StringReader(csv))) {
            return List.of(reader.readNext());
        } catch (IOException | CsvValidationException e) {
            logger.error("Something went wrong while reading component arguments to a CSV String.", e);
            return Collections.emptyList();
        }
    }

    public HikariDataSource getDataSource() {
        return dataSource;
    }


    public EmbedDatabase getEmbedDatabase() {
        return embedDatabase;
    }

    public ComponentDatabase getComponentDatabase() {
        return componentDatabase;
    }

    public GuildSettingsDatabase getGuildSettingsDatabase() {
        return guildSettingsDatabase;
    }

    @NotNull
    @NonNls
    @Override
    public String toString() {
        return "Database{" +
                "dataSource=" + dataSource +
                ", embedDatabase=" + embedDatabase +
                ", componentDatabase=" + componentDatabase +
                ", guildSettingsDatabase=" + guildSettingsDatabase +
                '}';
    }
}