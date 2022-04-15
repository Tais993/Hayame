package nl.tijsbeek.database.databases;

public interface IStringDatabase<Entity> extends IDatabase<Entity> {

    /**
     * Retrieves the entity by the given ID.
     *
     * @param id the ID of the entity
     *
     * @return the entity
     */
    default Entity retrieveById(String id) {
        return retrieveById(Long.parseLong(id));
    }

    /**
     * Deletes the entity by the given ID, and returns the (deleted) entity.
     *
     * @param id the entity to delete
     *
     * @return the deleted entity
     */
    default Entity deleteById(String id) {
        return deleteById(Long.parseLong(id));
    }
}