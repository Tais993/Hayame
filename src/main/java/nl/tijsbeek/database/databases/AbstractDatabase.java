package nl.tijsbeek.database.databases;

import org.intellij.lang.annotations.Language;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class AbstractDatabase<Entity> implements IDatabase<Entity> {

    private final DataSource dataSource;

    protected AbstractDatabase(final DataSource dataSource) {
        this.dataSource = Objects.requireNonNull(dataSource, "DataSource may not be null!");
    }

    protected Entity withReturn(@Language("SQL") String sql, Consumer<PreparedStatement> argumentInserter, Function<ResultSet, Entity> mapper) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            argumentInserter.accept(statement);

            statement.execute();

            return mapper.apply(statement.getResultSet());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected void withoutReturn(@Language("SQL") String sql, Consumer<PreparedStatement> argumentInserter) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            argumentInserter.accept(statement);

            statement.execute();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
