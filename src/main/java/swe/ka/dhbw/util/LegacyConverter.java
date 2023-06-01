package swe.ka.dhbw.util;

import java.util.Date;

public final class LegacyConverter {
    public class Name {
        public final String vorname;
        public final String nachname;

        public Name(final String vorname, final String nachname) {
            this.vorname = vorname;
            this.nachname = nachname;
        }
    }

    private static LegacyConverter instance;

    private LegacyConverter() {}

    public synchronized static LegacyConverter getInstance() {
        if(instance == null) {
            instance = new LegacyConverter();
        }
        return instance;
    }

    public Date convertDate(final String data) {
        throw new RuntimeException("TODO: UNIMPLEMENTED!");
    }
    public Name convertName(final String data) {
        throw new RuntimeException("TODO: UNIMPLEMENTED!");
    }
}
