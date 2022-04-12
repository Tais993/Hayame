package nl.tijsbeek.database.databases;

public interface IDatabase<Entity> {

    public Entity retrieveById(int id);
    public Entity deleteById(int id);
    public Entity insert(Entity entity);
}