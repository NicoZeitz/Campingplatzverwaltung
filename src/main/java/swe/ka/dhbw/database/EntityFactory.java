package swe.ka.dhbw.database;

import de.dhbwka.swe.utils.model.IPersistable;

public class EntityFactory {
    private static EntityFactory instance;

    private EntityFactory() {
    }

    public static synchronized EntityFactory getInstance() {
        if (instance == null) {
            instance = new EntityFactory();
        }
        return instance;
    }

    public IPersistable createElement(Class<?> c, String[] csvData) {
        // UNIMPLEMENTED:
        return null;
    }
}
