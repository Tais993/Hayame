package nl.tijsbeek.database.databases;

import com.diffplug.common.base.Errors;
import nl.tijsbeek.discord.components.ComponentEntity;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Implements 2 helper functions which drastically improve developer experience when dealing with Database requests.
 *
 * @param <Entity>
 *
 * @see #withReturn(String, Consumer, Function)
 * @see #withoutReturn(String, Consumer)
 */
public abstract class AbstractDatabase<Entity> implements IDatabase<Entity> {

    private final DataSource dataSource;

    protected AbstractDatabase(final DataSource dataSource) {
        this.dataSource = Objects.requireNonNull(dataSource, "DataSource may not be null!");
    }

    /**
     * This is a try to work against all the duplications that existed in the old system.
     *
     * <p>This method prepares the given SQL, following with a consumer to allow the developer to set arguments.
     * <br>After which a Function of ResultSet comes, which maps the ResultSet into T.
     *
     * <p>This is {@link T} instead of {@link Entity} to also allow exceptions, example would be returnal of only the ID, like {@link ComponentDatabase#insertAndReturnId(ComponentEntity)} does.
     *
     * <p><b>The {@link ResultSet} returned by the mapper has already been set on the first row!</b>
     *
     * @param sql the SQL to prepare
     * @param argumentInserter consumer which adds arguments to the {@link PreparedStatement}
     * @param mapper maps the {@link ResultSet} to {@link T} (which is often {@link Entity})
     *
     * @return {@link T} to return
     *
     * @param <T> the return value, often equal to {@link Entity}
     *
     * @see #withoutReturn(String, Consumer)
     */
    @Nullable
    protected <T> T withReturn(@Language("SQL") final String sql, final @NotNull Consumer<? super PreparedStatement> argumentInserter,
                               final @NotNull Function<? super ResultSet, T> mapper) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            argumentInserter.accept(statement);

            statement.execute();

            ResultSet resultSet = statement.getResultSet();

            if (!resultSet.next()) {
                return null;
            }

            return mapper.apply(resultSet);

        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This is a try to work against all the duplications that existed in the old system.
     *
     * <p>This method prepares the given SQL, following with a consumer to allow the developer to set arguments.
     * <br>This doesn't allow a return value, if you want to return something use {@link #withReturn(String, Consumer, Function)} instead.
     *
     * @param sql the SQL to prepare
     * @param argumentInserter consumer which adds arguments to the {@link PreparedStatement}
     *
     * @see #withReturn(String, Consumer, Function)
     */
    protected void withoutReturn(@Language("SQL") final String sql, final @NotNull Consumer<? super PreparedStatement> argumentInserter) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            argumentInserter.accept(statement);

            statement.execute();

        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns a consumer which set's the first parameter to the given ID as a Long.
     * <br>This method offers no customizable, purely because it's rarely needed differently.
     *
     * @param id the ID to set the first parameter to
     *
     * @return a consumer which sets the first parameter to the given ID
     *
     * @see #setIdStringConsumer(String)
     */
    public static Consumer<PreparedStatement> setIdLongConsumer(final long id) {
        return Errors.rethrow().wrap(statement -> {
            statement.setLong(1, id);
        });
    }

    /**
     * Returns a consumer which set's the first parameter to the given ID as a String.
     * <br>This method offers no customizable, purely because it's rarely needed differently.
     *
     * <br>This method is equal to {@link #setIdStringConsumer(String)}, but just includes a {@link String#valueOf(long)} for easier development.
     *
     * @param id the ID to set the first parameter to
     *
     * @return a consumer which sets the first parameter to the given ID
     *
     * @see #setIdStringConsumer(String)
     * @see #setIdLongConsumer(long)
     */
    public static Consumer<PreparedStatement> setIdStringConsumer(final long id) {
        return setIdStringConsumer(String.valueOf(id));
    }


    /**
     * Returns a consumer which set's the first parameter to the given ID as a String.
     * <br>This method offers no customizable, purely because it's rarely needed differently.
     *
     * @param id the ID to set the first parameter to
     *
     * @return a consumer which sets the first parameter to the given ID
     *
     * @see #setIdStringConsumer(long)
     * @see #setIdLongConsumer(long)
     */
    public static Consumer<PreparedStatement> setIdStringConsumer(final String id) {
        return Errors.rethrow().wrap(statement -> {
            statement.setString(1, String.valueOf(id));
        });
    }
}