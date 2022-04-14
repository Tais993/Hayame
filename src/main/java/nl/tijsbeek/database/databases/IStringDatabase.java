package nl.tijsbeek.database.databases;

public interface IStringDatabase<Entity> extends IDatabase<Entity> {

    default Entity retrieveById(String id) {
        return retrieveById(Long.parseLong(id));
    }

    default Entity deleteById(String id) {
        return deleteById(Long.parseLong(id));
    }
}