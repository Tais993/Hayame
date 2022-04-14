package nl.tijsbeek.database.databases;

import com.diffplug.common.base.Errors;
import nl.tijsbeek.database.tables.GuildSettings;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GuildSettingsDatabase extends AbstractDatabase<GuildSettings> {

    GuildSettingsDatabase(final @NotNull Database database) {
        super(database.getDataSource());
    }


    @Override
    public GuildSettings retrieveById(long id) {
        return withReturn("""
                SELECT *
                FROM discordbot.guild_settings
                WHERE guild_id = ?
                """, setIdLongConsumer(id), GuildSettingsDatabase::resultSetToGuildSettings);
    }


    @Override
    public GuildSettings deleteById(long id) {
        return withReturn("""
                DELETE FROM discordbot.guild_settings
                WHERE guild_id = ?
                RETURNING *
                """, setIdLongConsumer(id), GuildSettingsDatabase::resultSetToGuildSettings);
    }

    /**
     * This inserts the given settings when it didn't exist beforehand.
     * Use {@link #replace(GuildSettings)} for replacing.
     *
     * @param guildSettings the entity
     */
    @Override
    public void insert(@NotNull final GuildSettings guildSettings) {
        withoutReturn("""
                INSERT INTO discordbot.guild_settings (guild_id, reports_log_channel)
                SELECT ?, ? FROM DUAL
                WHERE NOT EXISTS (SELECT * FROM discordbot.guild_settings
                      WHERE guild_id=?);
                """, Errors.rethrow().wrap(statement -> {

            statement.setLong(1, guildSettings.getGuildId());
            statement.setLong(2, guildSettings.getReportChannelId());

            statement.setLong(3, guildSettings.getGuildId());
        }));
    }

    @Override
    public void replace(@NotNull final GuildSettings guildSettings) {
        withoutReturn("""
                REPLACE INTO discordbot.guild_settings (guild_id, reports_log_channel)
                VALUES (?, ?)
                """, Errors.rethrow().wrap(statement -> {
            statement.setLong(1, guildSettings.getGuildId());
            statement.setLong(2, guildSettings.getReportChannelId());
        }));
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