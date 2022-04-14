package nl.tijsbeek.database.databases;

import com.diffplug.common.base.Errors;
import nl.tijsbeek.database.tables.CustomAuditLogEntry;
import nl.tijsbeek.database.tables.CustomAuditLogEntry.CustomAuditLogEntryBuilder;
import nl.tijsbeek.database.tables.CustomAuditLogEntry.Type;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class AuditLogDatabase extends AbstractDatabase<CustomAuditLogEntry> {
    protected AuditLogDatabase(@NotNull final Database database) {
        super(Objects.requireNonNull(database, "Database may not be null").getDataSource());
    }

    @Override
    public CustomAuditLogEntry retrieveById(final long id) {
        return withReturn("""
                SELECT *
                FROM discordbot.audit_log
                WHERE case_id = ?
                """, setIdLongConsumer(id), AuditLogDatabase::resultSetToAuditLogEntry);
    }

    public List<CustomAuditLogEntry> retrieveByTargetId(final long id) {
        return withReturn("""
                SELECT *
                FROM discordbot.audit_log
                WHERE target = ?
                """, setIdLongConsumer(id), AuditLogDatabase::resultSetToAuditLogEntries);
    }

    public List<CustomAuditLogEntry> retrieveByTargetIdAndType(final long id, final Type type) {
        return withReturn("""
                SELECT *
                FROM discordbot.audit_log
                WHERE target = ? AND type = ?
                """, Errors.rethrow().wrap(preparedStatement -> {
            setIdLongConsumer(id).accept(preparedStatement);
            preparedStatement.setInt(2, type.getKey());
        }) , AuditLogDatabase::resultSetToAuditLogEntries);
    }


    public List<CustomAuditLogEntry> retrieveByAuthorId(final long id) {
        return withReturn("""
                SELECT *
                FROM discordbot.audit_log
                WHERE author = ?
                """, setIdLongConsumer(id), AuditLogDatabase::resultSetToAuditLogEntries);
    }


    @Override
    public CustomAuditLogEntry deleteById(final long id) {
        return withReturn("""
                DELETE FROM discordbot.audit_log
                WHERE case_id = ?
                RETURNING *
                """, setIdLongConsumer(id), AuditLogDatabase::resultSetToAuditLogEntry);
    }

    @Override
    public void insert(@NotNull final CustomAuditLogEntry customAuditLogEntry) {
        withoutReturn("""
                INSERT INTO discordbot.audit_log(author, target, reason, type, expire_time, creation_time, attachment_urls)
                VALUES (?,?,?,?,?,?,?)
                """, statementSetter(customAuditLogEntry));
    }

    @Override
    public void replace(@NotNull final CustomAuditLogEntry customAuditLogEntry) {
        withoutReturn("""
                REPLACE discordbot.audit_log(author, target, reason, type, expire_time, creation_time, attachment_urls)
                VALUES (?,?,?,?,?,?,?)
                """, statementSetter(customAuditLogEntry));
    }


    private static Consumer<? super PreparedStatement> statementSetter(final CustomAuditLogEntry customAuditLogEntry) {
        return Errors.rethrow().wrap(preparedStatement -> {
            int i = 1;
            preparedStatement.setLong(i++, customAuditLogEntry.author());
            preparedStatement.setLong(i++, customAuditLogEntry.target());
            preparedStatement.setString(i++, customAuditLogEntry.reason());
            preparedStatement.setInt(i++, customAuditLogEntry.type().getKey());
            preparedStatement.setTimestamp(i++, Timestamp.from(customAuditLogEntry.expireTime()));
            preparedStatement.setTimestamp(i++, Timestamp.from(customAuditLogEntry.creationTime()));
            preparedStatement.setString(i++, Database.argumentsToCsvString(customAuditLogEntry.attachmentUrls()));
        });
    }


    private static @NotNull CustomAuditLogEntry resultSetToAuditLogEntry(@NotNull final ResultSet resultSet) {
        try {
            resultSet.first();

            return new CustomAuditLogEntryBuilder()
                    .setCaseId(resultSet.getLong("case_id"))
                    .setAuthor(resultSet.getLong("author"))
                    .setTarget(resultSet.getLong("target"))
                    .setReason(resultSet.getString("reason"))
                    .setType(Type.byKey(resultSet.getInt("type")))
                    .setExpireTime(resultSet.getTimestamp("expire_time"))
                    .setCreationTime(resultSet.getTimestamp("creation_time"))
                    .setAttachmentUrls(Database.csvStringToArguments(resultSet.getString("attachment_urls")))
                    .createAuditLogEntry();

        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<CustomAuditLogEntry> resultSetToAuditLogEntries(@NotNull final ResultSet resultSet) {
        List<CustomAuditLogEntry> auditLogEntries = new ArrayList<>(10);

        try {
            while (!resultSet.next()) {
                auditLogEntries.add(
                        new CustomAuditLogEntryBuilder()
                                .setCaseId(resultSet.getLong("case_id"))
                                .setAuthor(resultSet.getLong("author"))
                                .setTarget(resultSet.getLong("target"))
                                .setReason(resultSet.getString("reason"))
                                .setType(Type.byKey(resultSet.getInt("type")))
                                .setExpireTime(resultSet.getTimestamp("expire_time"))
                                .setCreationTime(resultSet.getTimestamp("creation_time"))
                                .setAttachmentUrls(Database.csvStringToArguments(resultSet.getString("attachment_urls")))
                                .createAuditLogEntry()
                );
            }

            return auditLogEntries;

        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
