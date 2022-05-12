package nl.tijsbeek.database.databases;

import com.diffplug.common.base.Errors;
import nl.tijsbeek.discord.components.ComponentEntity;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Consumer;

/**
 * The database handler for components.
 */
public final class ComponentDatabase extends AbstractDatabase<ComponentEntity> implements IStringDatabase<ComponentEntity> {
    private static final Logger logger = LoggerFactory.getLogger(ComponentDatabase.class);

    @Contract(pure = true)
    ComponentDatabase(@NotNull final Database database) {
        super(database.getDataSource());
    }

    @Override
    public @Nullable ComponentEntity retrieveById(final long id) {
        return withReturn("""
                SELECT *
                FROM discordbot.component
                WHERE id = ?
                """, setIdLongConsumer(id), ComponentDatabase::resultSetToComponentEntity);
    }

    @Override
    public @Nullable ComponentEntity deleteById(final long id) {
        return withReturn("""
                DELETE FROM discordbot.component
                WHERE id = ?
                RETURNING *
                """, setIdLongConsumer(id), ComponentDatabase::resultSetToComponentEntity);
    }

    /**
     * Inserts the given {@link ComponentEntity} to the DB, returns nothing.
     * Since the component's ID gets generated in this method, you might want to use {@link #insertAndReturnId(ComponentEntity)} instead.
     *
     * @param componentEntity the {@link ComponentEntity} to insert
     *
     * @see #insertAndReturnId(ComponentEntity)
     */
    @Override
    public void insert(final @NotNull ComponentEntity componentEntity) {
        withoutReturn("""
                INSERT INTO discordbot.component (listener_id, expire_date, arguments)
                VALUES (?, ?, ?)
                """, setComponentEntity(componentEntity));
    }

    @Override
    public void replace(@NotNull final ComponentEntity componentEntity) {
        withoutReturn("""
                REPLACE INTO discordbot.component (listener_id, expire_date, arguments)
                VALUES (?, ?, ?)
                """, setComponentEntity(componentEntity));
    }

    /**
     * See {@link #insert(ComponentEntity)}, but then it returns the ID of the entity.
     *
     * @param componentEntity the {@link ComponentEntity} to insert
     *
     * @return the id of the component
     */
    public @Nullable String insertAndReturnId(final ComponentEntity componentEntity) {
        return withReturn("""
                INSERT INTO discordbot.component (listener_id, expire_date, arguments)
                VALUES (?, ?, ?)
                RETURNING id
                """, setComponentEntity(componentEntity),
                Errors.rethrow().wrap((ResultSet resultSet) -> resultSet.getString(1)));
    }

    @NotNull
    @Contract("_ -> new")
    private static ComponentEntity resultSetToComponentEntity(@NotNull final ResultSet resultSet) {
        try {
            int index = 1;

            String id = resultSet.getString(index++);
            String listenerId = resultSet.getString(index++);
            LocalDateTime expireDate = resultSet.getObject(index++, LocalDateTime.class);
            List<String> arguments = Database.csvStringToArguments(resultSet.getString(index++));

            return new ComponentEntity(id, listenerId, expireDate, arguments);

        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static Consumer<PreparedStatement> setComponentEntity(@NotNull final ComponentEntity componentEntity) {
        return Errors.rethrow().wrap(statement -> {
            statement.setString(1, componentEntity.getListenerId());
            statement.setObject(2, componentEntity.getExpireDate());
            statement.setString(3, Database.argumentsToCsvString(componentEntity.getArguments()));
        });
    }
}