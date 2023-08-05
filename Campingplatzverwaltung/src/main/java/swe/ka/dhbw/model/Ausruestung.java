package swe.ka.dhbw.model;

import de.dhbwka.swe.utils.model.Attribute;
import de.dhbwka.swe.utils.model.ICSVPersistable;
import de.dhbwka.swe.utils.model.IDepictable;
import de.dhbwka.swe.utils.model.IPersistable;
import swe.ka.dhbw.util.Validator;

import java.util.Objects;

public sealed class Ausruestung implements ICSVPersistable, IPersistable, IDepictable, Comparable<Ausruestung> permits Fahrzeug {
    public enum Attributes {
        AUSRUESTUNG_ID,
        BEZEICHNUNG,
        ANZAHL,
        BREITE,
        HOEHE
    }

    public enum CSVPosition {
        DISCRIMINATOR,
        AUSRUESTUNG_ID,
        BEZEICHNUNG,
        ANZAHL,
        BREITE,
        HOEHE,
        KENNZEICHEN,
        FAHRZEUGTYP,
        DUMMY_DATA
    }

    protected final int ausruestungID;
    protected String bezeichnung;
    protected int anzahl;
    protected double breite;
    protected double hoehe;

    public Ausruestung(final int ausruestungID,
                       final String bezeichnung,
                       final int anzahl,
                       final double breite,
                       final double hoehe) {
        Validator.getInstance().validateGreaterThan(ausruestungID, 0);
        this.ausruestungID = ausruestungID;
        this.setBezeichnung(bezeichnung);
        this.setAnzahl(anzahl);
        this.setBreite(breite);
        this.setHoehe(hoehe);
    }

    public int getAusruestungsId() {
        return this.ausruestungID;
    }

    public double getHoehe() {
        return this.hoehe;
    }

    public void setHoehe(final double hoehe) {
        Validator.getInstance().validateGreaterThanEqual(hoehe, 0);
        this.hoehe = hoehe;
    }

    public int getAnzahl() {
        return this.anzahl;
    }

    public void setAnzahl(final int anzahl) {
        Validator.getInstance().validateGreaterThanEqual(anzahl, 0);
        this.anzahl = anzahl;
    }

    public String getBezeichnung() {
        return this.bezeichnung;
    }

    public void setBezeichnung(final String bezeichnung) {
        Validator.getInstance().validateNotEmpty(bezeichnung);
        this.bezeichnung = bezeichnung;
    }

    public double getBreite() {
        return this.breite;
    }

    public void setBreite(final double breite) {
        Validator.getInstance().validateGreaterThanEqual(breite, 0);
        this.breite = breite;
    }

    @Override
    public int compareTo(final Ausruestung that) {
        // sort Vehicle after all other Equipment
        if (this instanceof Fahrzeug && !(that instanceof Fahrzeug)) {
            return 1;
        }

        if (!(this instanceof Fahrzeug) && that instanceof Fahrzeug) {
            return 1;
        }

        return String.CASE_INSENSITIVE_ORDER.compare(this.getBezeichnung(), that.getBezeichnung());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Ausruestung that)) return false;
        return this.getAusruestungsId() == that.getAusruestungsId();
    }

    @Override
    public Attribute[] getAttributeArray() {
        return new Attribute[] {
                new Attribute(Attributes.AUSRUESTUNG_ID.name(),
                        this,
                        Integer.class,
                        this.ausruestungID,
                        0,
                        false,
                        false,
                        false,
                        true),
                new Attribute(Attributes.BEZEICHNUNG.name(), this, String.class, this.bezeichnung, "", true),
                new Attribute(Attributes.ANZAHL.name(), this, Integer.class, this.anzahl, 1, true),
                new Attribute(Attributes.BREITE.name(), this, Double.class, this.breite, 0, true),
                new Attribute(Attributes.HOEHE.name(), this, Double.class, this.hoehe, 0, true)
        };
    }

    @Override
    public String[] getCSVData() {
        final var csvData = new String[CSVPosition.values().length];
        csvData[CSVPosition.DISCRIMINATOR.ordinal()] = this.getClass().getSimpleName();
        csvData[CSVPosition.AUSRUESTUNG_ID.ordinal()] = Integer.toString(this.getAusruestungsId());
        csvData[CSVPosition.BEZEICHNUNG.ordinal()] = this.getBezeichnung();
        csvData[CSVPosition.ANZAHL.ordinal()] = Integer.toString(this.getAnzahl());
        csvData[CSVPosition.BREITE.ordinal()] = Double.toString(this.getBreite());
        csvData[CSVPosition.HOEHE.ordinal()] = Double.toString(this.getHoehe());
        csvData[CSVPosition.KENNZEICHEN.ordinal()] = "";
        csvData[CSVPosition.FAHRZEUGTYP.ordinal()] = "";
        csvData[CSVPosition.DUMMY_DATA.ordinal()] = "NULL";
        return csvData;
    }

    @Override
    public String[] getCSVHeader() {
        return new String[] {
                // Single-Table Inheritance
                CSVPosition.DISCRIMINATOR.name(),
                CSVPosition.AUSRUESTUNG_ID.name(),
                CSVPosition.BEZEICHNUNG.name(),
                CSVPosition.ANZAHL.name(),
                CSVPosition.BREITE.name(),
                CSVPosition.HOEHE.name(),
                // Attributes from Fahrzeug
                CSVPosition.KENNZEICHEN.name(),
                CSVPosition.FAHRZEUGTYP.name(),
                // Dummy Data
                CSVPosition.DUMMY_DATA.name()
        };
    }

    @Override
    public String getElementID() {
        return Integer.toString(this.ausruestungID);
    }

    @Override
    public Object getPrimaryKey() {
        return this.ausruestungID;
    }

    @Override
    public String getVisibleText() {
        return this.getAnzahl() + "x " + this.getBezeichnung() + " (" + this.getBreite() + "x" + this.getHoehe() + ")";
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getAusruestungsId());
    }

    @Override
    public Attribute[] setAttributeValues(final Attribute[] attributeArray) {
        final var oldAttributeArray = this.getAttributeArray();

        for (final var attribute : attributeArray) {
            final var name = attribute.getName();
            final var value = attribute.getValue();
            if (name.equals(Attributes.AUSRUESTUNG_ID.name()) && !value.equals(this.ausruestungID)) {
                throw new IllegalArgumentException("Die AusrüstungsID darf nicht verändert werden!");
            }

            if (name.equals(Attributes.BEZEICHNUNG.name()) && !value.equals(this.getBezeichnung())) {
                this.setBezeichnung((String) value);
            } else if (name.equals(Attributes.ANZAHL.name()) && !value.equals(this.getAnzahl())) {
                this.setAnzahl((int) value);
            } else if (name.equals(Attributes.BREITE.name()) && !value.equals(this.getBreite())) {
                this.setBreite((double) value);
            } else if (name.equals(Attributes.HOEHE.name()) && !value.equals(this.getHoehe())) {
                this.setHoehe((double) value);
            }
        }
        return oldAttributeArray;
    }

    @Override
    public String toString() {
        return "Ausruestung{" +
                "bezeichnung='" + this.getBezeichnung() + '\'' +
                ", anzahl=" + this.getAnzahl() +
                ", breite=" + this.getBreite() +
                ", hoehe=" + this.getHoehe() +
                '}';
    }
}
