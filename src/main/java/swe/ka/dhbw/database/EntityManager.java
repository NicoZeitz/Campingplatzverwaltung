package swe.ka.dhbw.database;

import de.dhbwka.swe.utils.model.IPersistable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// UNIMPLEMENTED: connection to database
public class EntityManager<T extends IPersistable> {
    private final Map<Object, T> allElements = new HashMap<>();

    public List<T> find(final Class<?> c) {
        var list = new ArrayList<T>();
        for (final var entry : this.allElements.entrySet()) {
            if (entry.getValue().getClass().equals(c)) {
                list.add(entry.getValue());
            }
        }
        return list;
    }

    public boolean contains(final T element) {
        for (final var entry : this.allElements.entrySet()) {
            if (entry.getKey().equals(element.getPrimaryKey())) {
                return true;
            }
        }
        return false;
    }

    public void remove(final T element) {
        this.allElements.remove(element.getPrimaryKey());
    }

    public void persist(final T element) {
        this.allElements.put(element.getPrimaryKey(), element);
    }
}
