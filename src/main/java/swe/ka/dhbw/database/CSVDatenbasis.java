package swe.ka.dhbw.database;

import de.dhbwka.swe.utils.model.IPersistable;
import de.dhbwka.swe.utils.util.CSVReader;
import de.dhbwka.swe.utils.util.CSVWriter;

public class CSVDatenbasis implements Datenbasis {
    private CSVReader reader;
    private CSVWriter writer;

    @Override
    public void create(Class<?> c, IPersistable data) {
        // UNIMPLEMENTED:
    }

    @Override
    public IPersistable[] read(Class<?> c) {
        return new IPersistable[0]; // UNIMPLEMENTED:
    }

    @Override
    public void update(Class<?> c, IPersistable data) {
        // UNIMPLEMENTED:

    }

    @Override
    public void delete(Class<?> c, IPersistable data) {
        // UNIMPLEMENTED:

    }
}
