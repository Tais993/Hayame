package nl.tijsbeek.database.databases;

import nl.tijsbeek.discord.components.ComponentEntity;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;

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
    protected <T> T withReturn(@Language("SQL") final String sql, final @NotNull Consumer<? super PreparedStatement> argumentInserter,
                               final @NotNull Function<? super ResultSet, T> mapper) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            argumentInserter.accept(statement);

            statement.execute();

            return mapper.apply(statement.getResultSet());

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
}
