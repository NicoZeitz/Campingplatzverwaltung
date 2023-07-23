package swe.ka.dhbw.model;

import de.dhbwka.swe.utils.model.Attribute;
import swe.ka.dhbw.util.Validator;

import java.util.Arrays;
import java.util.Objects;

public final class Fahrzeug extends Ausruestung {
    public enum Attributes {
        KENNZEICHEN, TYP
    }

    public enum Typ {
        KFZ("Auto / KFZ"),
        WOHNMOBIL("Wohnmobil"),
        WOHNWAGEN("Wohnwagen");

        private final String name;

        Typ(final String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

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
        Validator.getInstance().validateNotNull(typ);
        this.typ = typ;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Fahrzeug that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(this.getKennzeichen(), that.getKennzeichen()) &&
                this.getTyp() == that.getTyp();
    }

    @Override
    public Attribute[] getAttributeArray() {
        final var superAttributes = super.getAttributeArray();
        final var attributes = Arrays.copyOf(superAttributes, superAttributes.length + 2);
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
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.getKennzeichen(), this.getTyp());
    }

    @Override
    public Attribute[] setAttributeValues(final Attribute[] attributeArray) {
        final var oldAttributeArray = this.getAttributeArray();

        super.setAttributeValues(attributeArray);

        for (final var attribute : attributeArray) {
            final var name = attribute.getName();
            final var value = attribute.getValue();

            if (name.equals(Attributes.KENNZEICHEN.name()) && !value.equals(this.getKennzeichen())) {
                this.setKennzeichen((String) value);
            } else if (name.equals(Attributes.TYP.name()) && !value.equals(this.getTyp())) {
                this.setTyp((Typ) value);
            }
        }
        return oldAttributeArray;
    }

    @Override
    public String toString() {
        return "Fahrzeug{" +
                "bezeichnung='" + this.getBezeichnung() + '\'' +
                ", anzahl=" + this.getAnzahl() +
                ", breite=" + this.getBreite() +
                ", hoehe=" + this.getHoehe() +
                ", kennzeichen='" + this.getKennzeichen() + '\'' +
                ", typ=" + this.getTyp() +
                '}';
    }
}
