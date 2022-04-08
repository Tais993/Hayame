package nl.tijsbeek.database.databases;

import com.zaxxer.hikari.HikariDataSource;
import nl.tijsbeek.database.Database;
import nl.tijsbeek.database.tables.EmbedTemplate;
import nl.tijsbeek.database.tables.EmbedTemplate.EmbedTemplateBuilder;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.sql.*;
import java.time.Instant;
import java.util.Objects;

public class EmbedDatabase {
    private static final Logger logger = LoggerFactory.getLogger(EmbedDatabase.class);

    private final HikariDataSource dataSource;

    @Contract(pure = true)
    public EmbedDatabase(@NotNull final HikariDataSource dataSource) {
        this.dataSource = Objects.requireNonNull(dataSource, "Datasource cannot be null");
    }

    public void insertEmbedTemplate(@NotNull final EmbedTemplate embedTemplate, String id) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                     INSERT INTO discordbot.embeds(id, timestamp, author_name, author_url, author_icon_url, color, footer_url, image_url, thumbnail_url, who_what_to_ping)
                     VALUES (?,?,?,?,?,?,?,?,?,?)
                     """)) {

            statement.setString(1, id);

            Timestamp timestamp = null;

            if (null != embedTemplate.getTimestamp()) {
                timestamp = Timestamp.from(Instant.from(embedTemplate.getTimestamp()));
            }

            statement.setTimestamp(2, timestamp);
            statement.setString(3, embedTemplate.getAuthorName());
            statement.setString(4, embedTemplate.getAuthorUrl());
            statement.setString(5, embedTemplate.getAuthorIconUrl());
            statement.setInt(6, embedTemplate.getColorRGB());
            statement.setString(7, embedTemplate.getFooterUrl());
            statement.setString(8, embedTemplate.getImageUrl());
            statement.setString(9, embedTemplate.getThumbnailUrl());
            statement.setString(10, Database.argumentsToCsvString(embedTemplate.getMentions()));

            statement.execute();
        } catch (SQLException e) {
            logger.error("Something went wrong while inserting an embed into the DB.", e);
            throw new RuntimeException(e);
        }
    }

    @NotNull
    public EmbedTemplate retrieveEmbedTemplate(String id) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                        SELECT *
                        FROM discordbot.embeds
                        WHERE id = ?
                        """)) {

            return getEmbedTemplateFromStatement(id, statement);
        } catch (SQLException e) {
            logger.error("Something went wrong while retrieving an embed into the DB.", e);
            throw new RuntimeException(e);
        }
    }

    @NotNull
    public EmbedTemplate deleteEmbedTemplate(String id) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                     DELETE FROM discordbot.component
                     WHERE id = ?
                     RETURNING *
                     """)) {

            return getEmbedTemplateFromStatement(id, statement);
        } catch (SQLException e) {
            logger.error("Something went wrong while deleting an embed from the DB.", e);
            throw new RuntimeException(e);
        }
    }

    @NotNull
    private EmbedTemplate getEmbedTemplateFromStatement(String id, PreparedStatement statement) throws SQLException {
        statement.setString(1, id);

        statement.execute();

        ResultSet resultSet = statement.getResultSet();

        resultSet.first();

        EmbedTemplateBuilder builder = new EmbedTemplateBuilder();


        Timestamp timestamp = resultSet.getTimestamp(2);
        Instant instant = null;

        if (null != timestamp) {
            instant = timestamp.toInstant();
        }

        builder.setTimestamp(instant);
        builder.setAuthorName(resultSet.getString(3));
        builder.setAuthorUrl(resultSet.getString(4));
        builder.setAuthorIconUrl(resultSet.getString(5));
        builder.setColor(new Color(resultSet.getInt(6)));
        builder.setFooterUrl(resultSet.getString(7));
        builder.setImageUrl(resultSet.getString(8));
        builder.setThumbnailUrl(resultSet.getString(9));
        builder.setMentions(Database.csvStringToArguments(resultSet.getString(10)));

        return builder.createEmbedTemplate();
    }
}
