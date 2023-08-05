package swe.ka.dhbw.database;

import de.dhbwka.swe.utils.model.IPersistable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EntityManager {
    private static EntityManager instance;
    private final List<? extends IPersistable> allElements = new ArrayList<>();

    private EntityManager() {
    }

    public static synchronized EntityManager getInstance() {
        if (instance == null) {
            instance = new EntityManager();
        }
        return instance;
    }


    public <Entity extends IPersistable> boolean contains(final Entity element) {
        return this.findOne(element.getClass(), element.getPrimaryKey()).isPresent();
    }

    public List<? extends IPersistable> find() {
        return this.allElements
                .stream()
                .toList();
    }

    @SuppressWarnings("unchecked")
    public <Entity extends IPersistable> List<Entity> find(final Class<Entity> c) {
        return (List<Entity>) this.allElements
                .stream()
                .filter(entity -> this.isSubclass(entity, c))
                .toList();
    }

    @SuppressWarnings("unchecked")
    public <Entity extends IPersistable> Optional<Entity> findOne(final Class<Entity> c, final Object primaryKey) {
        return (Optional<Entity>) this.allElements
                .stream()
                .filter(entity -> this.isSubclass(entity, c))
                .filter(entity -> entity.getPrimaryKey().toString().equals(primaryKey.toString()))
                .findFirst();
    }

    public int generateNextPrimaryKey(final Class<?> c) {
        return this.allElements
                .stream()
                .filter(entity -> this.isSubclass(entity, c))
                .mapToInt(entity -> (int) entity.getPrimaryKey())
                .max()
                .orElse(0) + 1;
    }

    @SuppressWarnings("unchecked")
    public <Entity extends IPersistable> void persist(final Entity element) {
        if (this.contains(element)) {
            return;
        }

        ((List<Entity>) this.allElements).add(element);
    }

    public <Entity extends IPersistable> void remove(final Entity element) {
        var i = 0;
        for (final var entity : this.allElements) {
            if (this.isSubclass(entity, element.getClass()) && entity.getPrimaryKey().equals(element.getPrimaryKey())) {
                this.allElements.remove(i);
                break;
            }
            i++;
        }
    }

    private boolean isSubclass(final Object object, final Class<?> superclass) {
        for (Class<?> clazz = object.getClass(); clazz != null; clazz = clazz.getSuperclass()) {
            if (clazz == superclass) {
                return true;
            }
        }
        return false;
    }
}
