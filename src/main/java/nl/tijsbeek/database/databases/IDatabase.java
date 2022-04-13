package nl.tijsbeek.database.databases;

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
    public Entity retrieveById(int id);

    /**
     * Deletes the entity by the given ID, and returns the (deleted) entity.
     *
     * @param id the entity to delete
     *
     * @return the deleted entity
     */
    public Entity deleteById(int id);

    /**
     * Inserts the given entity, to the database.
     *
     * @param entity the entity
     */
    public void insert(Entity entity);
}