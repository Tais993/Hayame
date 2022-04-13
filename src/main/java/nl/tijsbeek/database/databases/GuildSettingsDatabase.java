package nl.tijsbeek.database.databases;

import com.diffplug.common.base.Errors;
import nl.tijsbeek.database.tables.EmbedTemplate;
import nl.tijsbeek.database.tables.GuildSettings;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Consumer;

public class GuildSettingsDatabase extends AbstractDatabase<GuildSettings> {

    public GuildSettingsDatabase(final @NotNull Database database) {
        super(database.getDataSource());
    }


    @Override
    public GuildSettings retrieveById(long id) {
        return withReturn("""
                SELECT *
                FROM discordbot.guild_settings
                WHERE guild_id = ?
                """, setIdConsumer(id), GuildSettingsDatabase::resultSetToGuildSettings);
    }


    @Override
    public GuildSettings deleteById(long id) {
        return withReturn("""
                DELETE FROM discordbot.guild_settings
                WHERE guild_id = ?
                RETURNING *
                """, setIdConsumer(id), GuildSettingsDatabase::resultSetToGuildSettings);
    }

    @Override
    public void insert(@NotNull final GuildSettings guildSettings) {
        withoutReturn("""
                INSERT INTO discordbot.guild_settings (guild_id, reports_log_channel)
                VALUES (?, ?)
                """, Errors.rethrow().wrap(statement -> {
            statement.setLong(1, guildSettings.getGuildId());
            statement.setLong(2, guildSettings.getReportChannelId());
        }));
    }


    private static Consumer<PreparedStatement> setIdConsumer(final long id) {
        return Errors.rethrow().wrap(statement -> {
            statement.setLong(1, id);
        });
    }

    private static @NotNull GuildSettings resultSetToGuildSettings(@NotNull final ResultSet resultSet) {
        try {
            resultSet.first();

            GuildSettings guildSettings = new GuildSettings(resultSet.getLong("guild_id"));

            guildSettings.setReportChannelId(resultSet.getLong("reports_log_channel"));

            return guildSettings;

        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }
}