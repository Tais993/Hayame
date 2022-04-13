package nl.tijsbeek.database.databases;

public interface IStringDatabase<Entity> extends IDatabase<Entity> {

    default Entity retrieveById(String id) {
        return retrieveById(Integer.parseInt(id));
    }

    default Entity deleteById(String id) {
        return deleteById(Integer.parseInt(id));
    }
}