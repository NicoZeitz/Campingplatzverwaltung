package swe.ka.dhbw.model;

import de.dhbwka.swe.utils.model.Attribute;
import de.dhbwka.swe.utils.model.ICSVPersistable;
import de.dhbwka.swe.utils.model.IDepictable;
import de.dhbwka.swe.utils.model.IPersistable;
import swe.ka.dhbw.util.Validator;

import java.util.Objects;

public class Ausruestung implements ICSVPersistable, IPersistable, IDepictable {
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

    public String getBezeichnung() {
        return this.bezeichnung;
    }

    public void setBezeichnung(final String bezeichnung) {
        Validator.getInstance().validateNotEmpty(bezeichnung);
        this.bezeichnung = bezeichnung;
    }

    public int getAnzahl() {
        return this.anzahl;
    }

    public void setAnzahl(final int anzahl) {
        Validator.getInstance().validateGreaterThanEqual(anzahl, 0);
        this.anzahl = anzahl;
    }

    public double getBreite() {
        return this.breite;
    }

    public void setBreite(final double breite) {
        Validator.getInstance().validateGreaterThanEqual(breite, 0);
        this.breite = breite;
    }

    public double getHoehe() {
        return this.hoehe;
    }

    public void setHoehe(final double hoehe) {
        Validator.getInstance().validateGreaterThanEqual(hoehe, 0);
        this.hoehe = hoehe;
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
    public Attribute[] getAttributeArray() {
        return new Attribute[] {
                new Attribute(Attributes.AUSRUESTUNG_ID.name(),
                        this,
                        Integer.class,
                        this.ausruestungID,
                        this.ausruestungID,
                        false,
                        false,
                        false,
                        true),
                new Attribute(Attributes.BEZEICHNUNG.name(), this, String.class, this.bezeichnung, this.bezeichnung, true),
                new Attribute(Attributes.ANZAHL.name(), this, Integer.class, this.anzahl, this.anzahl, true),
                new Attribute(Attributes.BREITE.name(), this, Double.class, this.breite, this.breite, true),
                new Attribute(Attributes.HOEHE.name(), this, Double.class, this.hoehe, this.hoehe, true)
        };
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
                CSVPosition.FAHRZEUGTYP.name()
        };
    }

    @Override
    public String[] getCSVData() {
        final var csvData = new String[CSVPosition.values().length];
        csvData[CSVPosition.DISCRIMINATOR.ordinal()] = this.getClass().getSimpleName();
        csvData[CSVPosition.AUSRUESTUNG_ID.ordinal()] = Integer.toString(this.ausruestungID);
        csvData[CSVPosition.BEZEICHNUNG.ordinal()] = this.bezeichnung;
        csvData[CSVPosition.ANZAHL.ordinal()] = Integer.toString(this.anzahl);
        csvData[CSVPosition.BREITE.ordinal()] = Double.toString(this.breite);
        csvData[CSVPosition.HOEHE.ordinal()] = Double.toString(this.hoehe);
        csvData[CSVPosition.KENNZEICHEN.ordinal()] = "";
        csvData[CSVPosition.FAHRZEUGTYP.ordinal()] = "";
        return csvData;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Ausruestung that)) return false;
        return this.getAnzahl() == that.getAnzahl() &&
                Double.compare(that.getBreite(), this.getBreite()) == 0 &&
                Double.compare(that.getHoehe(), this.getHoehe()) == 0 &&
                Objects.equals(this.getBezeichnung(), that.getBezeichnung());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getBezeichnung(), this.getAnzahl(), this.getBreite(), this.getHoehe());
    }

    @Override
    public String toString() {
        return "Ausruestung{" +
                "bezeichnung='" + bezeichnung + '\'' +
                ", anzahl=" + anzahl +
                ", breite=" + breite +
                ", hoehe=" + hoehe +
                '}';
    }

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
        FAHRZEUGTYP
    }
}
