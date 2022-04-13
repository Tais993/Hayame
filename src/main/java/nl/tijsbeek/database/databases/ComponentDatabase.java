package nl.tijsbeek.database.databases;

import com.diffplug.common.base.Errors;
import nl.tijsbeek.discord.components.ComponentEntity;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
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
    public ComponentDatabase(@NotNull final Database database) {
        super(database.getDataSource());
    }

    @Override
    public ComponentEntity retrieveById(final long id) {
        return withReturn("""
                SELECT *
                FROM discordbot.embeds
                WHERE id = ?
                """, setIdConsumer(id), ComponentDatabase::resultSetToComponentEntity);
    }

    @Override
    public ComponentEntity deleteById(final long id) {
        return withReturn("""
                DELETE FROM discordbot.embeds
                WHERE id = ?
                RETURNING *
                """, setIdConsumer(id), ComponentDatabase::resultSetToComponentEntity);
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
    public void insert(final ComponentEntity componentEntity) {
        withoutReturn("""
                INSERT INTO discordbot.component (listener_id, command_type, expire_date, arguments)
                VALUES (?, ?, ?, ?)
                """, setComponentEntity(componentEntity));
    }

    /**
     * See {@link #insert(ComponentEntity)}, but then it returns the ID of the entity.
     *
     * @param componentEntity the {@link ComponentEntity} to insert
     *
     * @return the id of the component
     */
    public String insertAndReturnId(final ComponentEntity componentEntity) {
        return withReturn("""
                INSERT INTO discordbot.component (listener_id, command_type, expire_date, arguments)
                VALUES (?, ?, ?, ?)
                RETURNING id
                """, setComponentEntity(componentEntity),
                Errors.rethrow().wrap((ResultSet resultSet) -> resultSet.getString(1)));
    }



    private static Consumer<PreparedStatement> setIdConsumer(final long id) {
        return Errors.rethrow().wrap(statement -> {
            statement.setString(1, String.valueOf(id));
        });
    }

    @NotNull
    @Contract("_ -> new")
    private static ComponentEntity resultSetToComponentEntity(@NotNull final ResultSet resultSet) {
        try {

            String id = resultSet.getString(1);
            String listenerId = resultSet.getString(2);
            int commandType = resultSet.getInt(3);
            LocalDateTime expireDate = resultSet.getObject(4, LocalDateTime.class);
            List<String> arguments = Database.csvStringToArguments(resultSet.getString(5));

            return new ComponentEntity(id, listenerId, commandType, expireDate, arguments);

        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static Consumer<PreparedStatement> setComponentEntity(@NotNull final ComponentEntity componentEntity) {
        return Errors.rethrow().wrap(statement -> {
            statement.setObject(1, componentEntity.getListenerId());
            statement.setObject(2, componentEntity.getCommandType());
            statement.setObject(3, componentEntity.getExpireDate());
            statement.setObject(4, Database.argumentsToCsvString(componentEntity.getArguments()));
        });
    }
}