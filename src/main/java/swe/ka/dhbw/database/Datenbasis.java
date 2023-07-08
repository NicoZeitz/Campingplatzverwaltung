package swe.ka.dhbw.database;

import de.dhbwka.swe.utils.model.IPersistable;

public interface Datenbasis {
    void create(Class<?> c, IPersistable data);

    IPersistable[] read(Class<?> c);

    void update(Class<?> c, IPersistable data);

    void delete(Class<?> c, IPersistable data);
}
