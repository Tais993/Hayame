package nl.tijsbeek.database.databases;

import org.jetbrains.annotations.NotNull;

/**
 * Interface with universal methods to work against the bloat that existed before.
 *
 * @param <Entity> the DB's entity
 *
 * @see IStringDatabase
 */
public interface IDatabase<Entity> {

    /**
     * Retrieves the entity by the given ID.
     *
     * @param id the ID of the entity
     *
     * @return the entity
     */
    Entity retrieveById(long id);

    /**
     * Deletes the entity by the given ID, and returns the (deleted) entity.
     *
     * @param id the entity to delete
     *
     * @return the deleted entity
     */
    Entity deleteById(long id);

    /**
     * Inserts the given entity, to the database.
     *
     * The handling on duplication is fully up-to the implementation, this can be either an error or ignore.
     * Use {@link #replace(Object)} if you know an entrance exists already.
     *
     * @param entity the entity
     */
    void insert(@NotNull Entity entity);

    /**
     * Inserts updates the given entity, to the database.
     * <b>This replaces the old entry, so use correctly!</b>
     *
     * <p>There are no plans for adding a way to only update individual values in a generic way, the implementation might offer a way to.
     *
     * @param entity the entity
     */
    void replace(@NotNull Entity entity);
}