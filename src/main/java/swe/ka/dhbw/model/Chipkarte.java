package swe.ka.dhbw.model;

import de.dhbwka.swe.utils.model.Attribute;
import de.dhbwka.swe.utils.model.ICSVPersistable;
import de.dhbwka.swe.utils.model.IDepictable;
import de.dhbwka.swe.utils.model.IPersistable;
import swe.ka.dhbw.util.Validator;

import java.util.Objects;

public final class Chipkarte implements ICSVPersistable, IPersistable, IDepictable, Comparable<Chipkarte> {
    public enum Status {
        VERFUEGBAR("Verfügbar"), IN_VERWENDUNG("In Verwendung");

        private final String status;

        Status(final String status) {
            this.status = status;
        }

        @Override
        public String toString() {
            return status;
        }
    }

    public enum Attributes {
        NUMMER,
        STATUS
    }

    public enum CSVPosition {
        NUMMER,
        STATUS,
        DUMMY_DATA
    }

    private final int nummer;
    private Status status;

    @SuppressWarnings("unused")
    public Chipkarte(final int nummer) {
        this(nummer, Status.VERFUEGBAR);
    }

    public Chipkarte(final int nummer, final Status status) {
        Validator.getInstance().validateGreaterThanEqual(nummer, 0);
        this.nummer = nummer;
        this.setStatus(status);
    }

    public int getNummer() {
        return nummer;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(final Status status) {
        Validator.getInstance().validateNotNull(status);
        this.status = status;
    }

    @Override
    public int compareTo(final Chipkarte o) {
        return Integer.compare(this.getNummer(), o.getNummer());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof final Chipkarte that)) return false;
        return this.getNummer() == that.getNummer() &&
                this.getStatus() == that.getStatus();
    }

    @Override
    public Attribute[] getAttributeArray() {
        return new Attribute[] {
                new Attribute(Attributes.NUMMER.name(),
                        this,
                        Integer.class,
                        this.getNummer(),
                        0,
                        true,
                        false,
                        false,
                        true),
                new Attribute(Attributes.STATUS.name(), this, Status.class, this.getStatus(), Status.VERFUEGBAR, true)
        };
    }

    @Override
    public String[] getCSVData() {
        return new String[] {
                Integer.toString(this.getNummer()),
                this.getStatus().name(),
                "NULL"
        };
    }

    @Override
    public String[] getCSVHeader() {
        return new String[] {
                CSVPosition.NUMMER.name(),
                CSVPosition.STATUS.name(),
                CSVPosition.DUMMY_DATA.name()
        };
    }

    @Override
    public String getElementID() {
        return Integer.toString(this.getNummer());
    }

    @Override
    public Object getPrimaryKey() {
        return this.getNummer();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getNummer(), this.getStatus());
    }

    @Override
    public Attribute[] setAttributeValues(Attribute[] attributeArray) {
        final var oldAttributeArray = this.getAttributeArray();
        for (final var attribute : attributeArray) {
            final var name = attribute.getName();
            final var value = attribute.getValue();

            if (name.equals(Attributes.NUMMER.name()) && !value.equals(this.nummer)) {
                throw new IllegalArgumentException("Die Nummer einer Chipkarte darf nicht verändert werden!");
            }

            if (name.equals(Attributes.STATUS.name()) && !value.equals(this.getStatus())) {
                this.setStatus((Status) value);
            }
        }
        return oldAttributeArray;
    }

    @Override
    public String toString() {
        return "Chipkarte " + this.getNummer() + " - " + this.getStatus().toString();
    }
}
