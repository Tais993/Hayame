package nl.tijsbeek.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import nl.tijsbeek.config.Config;
import nl.tijsbeek.discord.components.ComponentDatabase;
import org.flywaydb.core.Flyway;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Database {
    private static final Logger logger = LoggerFactory.getLogger(Database.class);
    private static final String DB_SCHEMA_BOT = "discordbot";

    private final HikariDataSource dataSource;

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
                        .locations("classpath:/db/").load();
        flyway.migrate();
    }

    public HikariDataSource getDataSource() {
        return dataSource;
    }
}
