package swe.ka.dhbw.model;

import de.dhbwka.swe.utils.model.Attribute;
import de.dhbwka.swe.utils.model.ICSVPersistable;
import de.dhbwka.swe.utils.model.IDepictable;
import de.dhbwka.swe.utils.model.IPersistable;
import swe.ka.dhbw.util.Validator;

import java.util.Objects;

public class Gast extends Person implements IPersistable, IDepictable, ICSVPersistable {
    private final int kundennummer;
    private String ausweisnummer;
    private Adresse anschrift;

    public Gast(
            final String vorname,
            final String nachname,
            final Geschlecht geschlecht,
            final String email,
            final String telefonnummer,
            final int kundennummer,
            final String ausweisnummer
    ) {
        super(vorname, nachname, geschlecht, email, telefonnummer);
        Validator.getInstance().validateGreaterThan(kundennummer, 0);
        this.kundennummer = kundennummer;
        this.setAusweisnummer(ausweisnummer);
    }

    public int getKundennummer() {
        return this.kundennummer;
    }

    public String getAusweisnummer() {
        return this.ausweisnummer;
    }

    public void setAusweisnummer(final String ausweisnummer) {
        Validator.getInstance().validateNotEmpty(ausweisnummer);
        this.ausweisnummer = ausweisnummer;
    }

    public Adresse getAnschrift() {
        return this.anschrift;
    }

    public void setAnschrift(final Adresse anschrift) {
        Validator.getInstance().validateNotNull(anschrift);
        this.anschrift = anschrift;
    }

    @Override
    public Object getPrimaryKey() {
        return this.getKundennummer();
    }

    @Override
    public String getElementID() {
        return Integer.toString(this.getKundennummer());
    }

    @Override
    public Attribute[] getAttributeArray() {
        return new Attribute[] {
                new Attribute(Attributes.VORNAME.name(), this, String.class, this.vorname, this.vorname, true),
                new Attribute(Attributes.NACHNAME.name(), this, String.class, nachname, nachname, true),
                new Attribute(Attributes.GESCHLECHT.name(), this, Geschlecht.class, geschlecht, geschlecht, true),
                new Attribute(Attributes.EMAIL.name(), this, String.class, email, email, true),
                new Attribute(Attributes.TELEFONNUMMER.name(), this, String.class, telefonnummer, telefonnummer, true),
                new Attribute(Attributes.KUNDENNUMMER.name(),
                        this,
                        int.class,
                        kundennummer,
                        kundennummer,
                        true,
                        false,
                        false,
                        true),
                new Attribute(Attributes.AUSWEISNUMMER.name(), this, String.class, ausweisnummer, ausweisnummer, true),
        };
    }

    @Override
    public Attribute[] setAttributeValues(Attribute[] attributeArray) {
        final var oldAttributeArray = this.getAttributeArray().clone();

        for (final var attribute : attributeArray) {
            final var name = attribute.getName();
            final var value = attribute.getValue();

            if (name.equals(Attributes.KUNDENNUMMER.name()) && !value.equals(this.getKundennummer())) {
                throw new IllegalArgumentException("Kundennummer darf nicht geändert werden!");
            }

            if (name.equals(Attributes.VORNAME.name()) && !value.equals(this.getVorname())) {
                this.setVorname((String) value);
            } else if (name.equals(Attributes.NACHNAME.name()) && !value.equals(this.getNachname())) {
                this.setNachname((String) value);
            } else if (name.equals(Attributes.GESCHLECHT.name()) && !value.equals(this.getGeschlecht())) {
                this.setGeschlecht((Geschlecht) value);
            } else if (name.equals(Attributes.EMAIL.name()) && !value.equals(this.getEmail())) {
                this.setEmail((String) value);
            } else if (name.equals(Attributes.TELEFONNUMMER.name()) && !value.equals(this.getTelefonnummer())) {
                this.setTelefonnummer((String) value);
            } else if (name.equals(Attributes.AUSWEISNUMMER.name()) && !value.equals(this.getAusweisnummer())) {
                this.setAusweisnummer((String) value);
            }
        }

        return oldAttributeArray;
    }

    @Override
    public String[] getCSVHeader() {
        return new String[] {
                CSVPosition.VORNAME.name(),
                CSVPosition.NACHNAME.name(),
                CSVPosition.GESCHLECHT.name(),
                CSVPosition.EMAIL.name(),
                CSVPosition.TELEFONNUMMER.name(),
                CSVPosition.KUNDENNUMMER.name(),
                CSVPosition.AUSWEISNUMMER.name(),
                CSVPosition.ANSCHRIFT.name()
        };
    }

    @Override
    public String[] getCSVData() {
        final var csvData = new String[CSVPosition.values().length];
        csvData[CSVPosition.VORNAME.ordinal()] = this.getVorname();
        csvData[CSVPosition.NACHNAME.ordinal()] = this.getNachname();
        csvData[CSVPosition.GESCHLECHT.ordinal()] = this.getGeschlecht().toString();
        csvData[CSVPosition.EMAIL.ordinal()] = this.getEmail();
        csvData[CSVPosition.TELEFONNUMMER.ordinal()] = this.getTelefonnummer();
        csvData[CSVPosition.KUNDENNUMMER.ordinal()] = Integer.toString(this.getKundennummer());
        csvData[CSVPosition.AUSWEISNUMMER.ordinal()] = this.getAusweisnummer();
        csvData[CSVPosition.ANSCHRIFT.ordinal()] = this.getAnschrift().getPrimaryKey().toString();
        return csvData;
    }


    @Override
    public String toString() {
        return "Gast{" +
                "kundennummer=" + this.getKundennummer() +
                ", ausweisnummer='" + this.getAusweisnummer() + '\'' +
                ", anschrift=" + this.getAnschrift() +
                ", vorname='" + this.getVorname() + '\'' +
                ", nachname='" + this.getNachname() + '\'' +
                ", geschlecht=" + this.getGeschlecht() +
                ", email='" + this.getEmail() + '\'' +
                ", telefonnummer='" + this.getTelefonnummer() + '\'' +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Gast that)) return false;
        return this.getKundennummer() == that.getKundennummer() &&
                Objects.equals(this.getAusweisnummer(), that.getAusweisnummer()) &&
                Objects.equals(this.getAnschrift(), that.getAnschrift());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getKundennummer(), this.getAusweisnummer(), this.getAnschrift());
    }

    public enum CSVPosition {
        VORNAME,
        NACHNAME,
        GESCHLECHT,
        EMAIL,
        TELEFONNUMMER,
        KUNDENNUMMER,
        AUSWEISNUMMER,
        ANSCHRIFT
    }

    public enum Attributes {
        VORNAME,
        NACHNAME,
        GESCHLECHT,
        EMAIL,
        TELEFONNUMMER,
        KUNDENNUMMER,
        AUSWEISNUMMER
    }
}
