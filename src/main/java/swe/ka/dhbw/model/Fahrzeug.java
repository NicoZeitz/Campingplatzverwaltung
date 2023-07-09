package swe.ka.dhbw.model;

import de.dhbwka.swe.utils.model.Attribute;
import swe.ka.dhbw.util.Validator;

import java.util.Arrays;
import java.util.Objects;

public final class Fahrzeug extends Ausruestung {
    private String kennzeichen;
    private Typ typ;

    public Fahrzeug(final int ausruestungID,
                    final String bezeichnung,
                    final int anzahl,
                    final double breite,
                    final double hoehe,
                    final String kennzeichen,
                    final Typ typ) {
        super(ausruestungID, bezeichnung, anzahl, breite, hoehe);
        this.setKennzeichen(kennzeichen);
        this.setTyp(typ);
    }

    public String getKennzeichen() {
        return this.kennzeichen;
    }

    public void setKennzeichen(final String kennzeichen) {
        Validator.getInstance().validateNotNull(kennzeichen);
        this.kennzeichen = kennzeichen;
    }

    public Typ getTyp() {
        return this.typ;
    }

    public void setTyp(final Typ typ) {
        this.typ = typ;
    }

    @Override
    public Attribute[] getAttributeArray() {
        final var superAttributes = super.getAttributeArray();
        final var attributes = new Attribute[superAttributes.length + 2];
        Arrays.copyOfRange(superAttributes, 0, superAttributes.length - 1);
        attributes[attributes.length - 2] = new Attribute(
                Attributes.KENNZEICHEN.name(),
                this,
                String.class,
                this.kennzeichen,
                this.kennzeichen,
                true);
        attributes[attributes.length - 1] = new Attribute(
                Attributes.TYP.name(),
                this,
                Typ.class,
                this.typ,
                this.typ,
                true);
        return attributes;
    }

    @Override
    public String[] getCSVData() {
        final var csvData = super.getCSVData();
        csvData[CSVPosition.KENNZEICHEN.ordinal()] = this.getKennzeichen();
        csvData[CSVPosition.FAHRZEUGTYP.ordinal()] = this.getTyp().toString();
        return csvData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Fahrzeug fahrzeug)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(getKennzeichen(), fahrzeug.getKennzeichen()) && getTyp() == fahrzeug.getTyp();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getKennzeichen(), getTyp());
    }

    @Override
    public String toString() {
        return "Fahrzeug{" +
                "bezeichnung='" + bezeichnung + '\'' +
                ", anzahl=" + anzahl +
                ", breite=" + breite +
                ", hoehe=" + hoehe +
                ", kennzeichen='" + kennzeichen + '\'' +
                ", typ=" + typ +
                '}';
    }

    public enum Attributes {
        KENNZEICHEN, TYP
    }

    public enum Typ {
        KFZ, WOHNMOBIL, WOHNWAGEN
    }
}
