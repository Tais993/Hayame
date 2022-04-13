package nl.tijsbeek.database.databases;

import com.diffplug.common.base.Errors;
import nl.tijsbeek.database.tables.EmbedTemplate;
import nl.tijsbeek.database.tables.EmbedTemplate.EmbedTemplateBuilder;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.sql.*;
import java.time.Instant;
import java.util.function.Consumer;

public class EmbedDatabase extends AbstractDatabase<EmbedTemplate> implements IStringDatabase<EmbedTemplate> {
    private static final Logger logger = LoggerFactory.getLogger(EmbedDatabase.class);

    @Contract(pure = true)
    protected EmbedDatabase(@NotNull final Database database) {
        super(database.getDataSource());
    }

    @Override
    public EmbedTemplate retrieveById(final int id) {
        return withReturn("""
                SELECT *
                FROM discordbot.embeds
                WHERE id = ?
                """, setIdConsumer(id), EmbedDatabase::resultSetToEmbedTemplate);
    }

    @Override
    public EmbedTemplate deleteById(final int id) {
        return withReturn("""
                DELETE FROM discordbot.embeds
                WHERE id = ?
                RETURNING *
                """, setIdConsumer(id), EmbedDatabase::resultSetToEmbedTemplate);
    }

    @Override
    public void insert(final EmbedTemplate embedTemplate) {
        withoutReturn("""
                INSERT INTO discordbot.embeds(id, timestamp, author_name, author_url, author_icon_url, colour, footer_url, image_url, thumbnail_url, who_what_to_ping)
                VALUES (?,?,?,?,?,?,?,?,?,?)
                """, Errors.rethrow().wrap(statement -> {
            statement.setString(1, String.valueOf(embedTemplate.getId()));

            statement.setBoolean(2, embedTemplate.getTimestamp());
            statement.setString(3, embedTemplate.getAuthorName());
            statement.setString(4, embedTemplate.getAuthorUrl());
            statement.setString(5, embedTemplate.getAuthorIconUrl());
            statement.setInt(6, embedTemplate.getColorRGB());
            statement.setString(7, embedTemplate.getFooterUrl());
            statement.setString(8, embedTemplate.getImageUrl());
            statement.setString(9, embedTemplate.getThumbnailUrl());
            statement.setString(10, Database.argumentsToCsvString(embedTemplate.getMentions()));
        }));
    }


    private static Consumer<PreparedStatement> setIdConsumer(final int id) {
        return Errors.rethrow().wrap(statement -> {
            statement.setString(1, String.valueOf(id));
        });
    }

    private static EmbedTemplate resultSetToEmbedTemplate(@NotNull final ResultSet resultSet) {
        try {

            resultSet.first();

            EmbedTemplateBuilder builder = new EmbedTemplateBuilder();

            builder.setId(resultSet.getInt(1));

            builder.setTimestamp(resultSet.getBoolean(2));
            builder.setAuthorName(resultSet.getString(3));
            builder.setAuthorUrl(resultSet.getString(4));
            builder.setAuthorIconUrl(resultSet.getString(5));
            builder.setColor(new Color(resultSet.getInt(6)));
            builder.setFooterUrl(resultSet.getString(7));
            builder.setImageUrl(resultSet.getString(8));
            builder.setThumbnailUrl(resultSet.getString(9));
            builder.setMentions(Database.csvStringToArguments(resultSet.getString(10)));

            return builder.createEmbedTemplate();

        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }
}