package swe.ka.dhbw.model;

import de.dhbwka.swe.utils.model.Attribute;
import de.dhbwka.swe.utils.model.ICSVPersistable;
import de.dhbwka.swe.utils.model.IDepictable;
import de.dhbwka.swe.utils.model.IPersistable;
import swe.ka.dhbw.util.Validator;

import java.time.LocalTime;
import java.util.Objects;

public final class Oeffnungszeit implements IDepictable, ICSVPersistable, IPersistable {
    public enum CSVPosition {
        OEFFNUNGSZEIT_ID,
        START,
        ENDE,
        DUMMY_DATA
    }

    public enum Attributes {
        OEFFNUNGSZEIT_ID,
        START,
        ENDE
    }

    private final int oeffnungszeitID;
    private LocalTime start;
    private LocalTime ende;

    public Oeffnungszeit(
            final int oeffnungszeitID,
            final LocalTime start,
            final LocalTime ende
    ) {
        this.oeffnungszeitID = oeffnungszeitID;
        this.setStart(start);
        this.setEnde(ende);
    }

    public LocalTime getStart() {
        return this.start;
    }

    public void setStart(final LocalTime start) {
        Validator.getInstance().validateNotNull(start);
        this.start = start;
    }

    public LocalTime getEnde() {
        return this.ende;
    }

    public void setEnde(final LocalTime ende) {
        Validator.getInstance().validateNotNull(ende);
        this.ende = ende;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Oeffnungszeit that)) return false;
        return Objects.equals(this.getStart(), that.getStart()) && Objects.equals(this.getEnde(), that.getEnde());
    }

    @Override
    public Attribute[] getAttributeArray() {
        return new Attribute[] {
                new Attribute(Attributes.OEFFNUNGSZEIT_ID.name(),
                        this,
                        Integer.class,
                        this.oeffnungszeitID,
                        this.oeffnungszeitID,
                        false,
                        false,
                        false,
                        true),
                new Attribute(Attributes.START.name(), this, LocalTime.class, this.getStart(), this.getStart(), true),
                new Attribute(Attributes.ENDE.name(), this, LocalTime.class, this.getEnde(), this.getEnde(), true)
        };
    }

    @Override
    public String[] getCSVData() {
        final var csvData = new String[CSVPosition.values().length];
        csvData[CSVPosition.OEFFNUNGSZEIT_ID.ordinal()] = Integer.toString(this.oeffnungszeitID);
        csvData[CSVPosition.START.ordinal()] = this.getStart().toString();
        csvData[CSVPosition.ENDE.ordinal()] = this.getEnde().toString();
        csvData[CSVPosition.DUMMY_DATA.ordinal()] = "NULL";
        return csvData;
    }

    @Override
    public String[] getCSVHeader() {
        return new String[] {
                CSVPosition.OEFFNUNGSZEIT_ID.name(),
                CSVPosition.START.name(),
                CSVPosition.ENDE.name(),
                CSVPosition.DUMMY_DATA.name()
        };
    }

    @Override
    public String getElementID() {
        return Integer.toString(this.oeffnungszeitID);
    }

    @Override
    public Object getPrimaryKey() {
        return this.oeffnungszeitID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getStart(), this.getEnde());
    }

    @Override
    public String toString() {
        return "Oeffnungszeit{" +
                "start=" + start +
                ", ende=" + ende +
                '}';
    }
}
