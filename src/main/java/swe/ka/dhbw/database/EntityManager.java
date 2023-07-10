package swe.ka.dhbw.database;

import de.dhbwka.swe.utils.model.IPersistable;

import java.util.*;

// UNIMPLEMENTED: connection to database
public class EntityManager {
    private static EntityManager instance;
    private final Map<Class<? extends IPersistable>, Map<Object, IPersistable>> allElements = new HashMap<>();

    private EntityManager() {
    }

    public static synchronized EntityManager getInstance() {
        if (instance == null) {
            instance = new EntityManager();
        }
        return instance;
    }

    public List<IPersistable> getAll() {
        return this.allElements.values()
                .stream()
                .flatMap(map -> map.values().stream())
                .toList();
    }

    public <Entity extends IPersistable> List<Entity> find(final Class<Entity> c) {
        final var allOfClass = this.allElements.get(c);
        if (allOfClass == null) {
            return new ArrayList<>();
        }

        return allOfClass.values()
                .stream()
                .map(entity -> (Entity) entity)
                .toList();
    }

    public <Entity extends IPersistable> Optional<Entity> findOne(final Class<Entity> c, final Object primaryKey) {
        return Optional.ofNullable(this.allElements.get(c))
                .map(map -> (Entity) map.get(primaryKey));
    }

    public <Entity extends IPersistable> boolean contains(final Entity element) {
        final var allOfClass = this.allElements.get(element.getClass());
        if (allOfClass == null) {
            return false;
        }

        return allOfClass.containsKey(element.getPrimaryKey());
    }

    public <Entity extends IPersistable> void remove(final Entity element) {
        var allOfClass = this.allElements.get(element.getClass());
        if (allOfClass != null) {
            allOfClass.remove(element.getPrimaryKey());
        }
    }

    public <Entity extends IPersistable> void persist(final Entity element) {
        var allOfClass = this.allElements.get(element.getClass());
        if (allOfClass == null) {
            allOfClass = new HashMap<>();
            this.allElements.put(element.getClass(), allOfClass);
        }

        allOfClass.put(element.getPrimaryKey(), element);
    }
}
