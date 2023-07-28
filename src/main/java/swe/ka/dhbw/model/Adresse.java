package swe.ka.dhbw.model;

import de.dhbwka.swe.utils.model.Attribute;
import de.dhbwka.swe.utils.model.ICSVPersistable;
import de.dhbwka.swe.utils.model.IDepictable;
import de.dhbwka.swe.utils.model.IPersistable;
import swe.ka.dhbw.util.Validator;

import java.util.Objects;
import java.util.Optional;

public final class Adresse implements IPersistable, IDepictable, ICSVPersistable {
    // Not a complete list
    public enum Land {
        DE("Deutschland"),
        US("Vereinigte Staaten von Amerika"),
        NL("Niederlande"),
        GB("Großbritannien"),
        PL("Polen"),
        CA("Kanada"),
        BE("Belgien"),
        AT("Österreich"),
        SE("Schweden"),
        NO("Norwegen"),
        FI("Finnland"),
        FR("Frankreich"),
        CH("Schweiz");

        public final String name;

        Land(final String name) {
            this.name = name;
        }
    }

    public enum Attributes {
        ADRESSE_ID,
        STRASSE,
        HAUSNUMMER,
        ZUSATZ,
        ORT,
        PLZ,
        LAND
    }

    public enum CSVPosition {
        ADRESSE_ID,
        STRASSE,
        HAUSNUMMER,
        ZUSATZ,
        ORT,
        PLZ,
        LAND,
        DUMMY_DATA
    }

    private final int adresseID;
    private String strasse;
    private int hausnummer;
    private Optional<String> zusatz;
    private String ort;
    private String plz;
    private Land land;

    public Adresse(
            final int adresseID,
            final String strasse,
            final int hausnummer,
            final Optional<String> zusatz,
            final String ort,
            final String plz,
            final Land land
    ) {
        this.adresseID = adresseID;
        this.setStrasse(strasse);
        this.setHausnummer(hausnummer);
        this.setZusatz(zusatz);
        this.setOrt(ort);
        this.setPLZ(plz);
        this.setLand(land);
    }

    public int getAdresseID() {
        return this.adresseID;
    }

    public String getStrasse() {
        return this.strasse;
    }

    public void setStrasse(final String strasse) {
        Validator.getInstance().validateNotEmpty(strasse);
        this.strasse = strasse;
    }

    public int getHausnummer() {
        return this.hausnummer;
    }

    public void setHausnummer(final int hausnummer) {
        Validator.getInstance().validateGreaterThan(hausnummer, 0);
        this.hausnummer = hausnummer;
    }

    public Optional<String> getZusatz() {
        return this.zusatz;
    }

    public void setZusatz(final Optional<String> zusatz) {
        Validator.getInstance().validateNotNull(zusatz);
        this.zusatz = zusatz;
    }

    public String getOrt() {
        return this.ort;
    }

    public void setOrt(final String ort) {
        Validator.getInstance().validateNotEmpty(ort);
        this.ort = ort;
    }

    public String getPLZ() {
        return this.plz;
    }

    public void setPLZ(final String plz) {
        Validator.getInstance().validateNotEmpty(plz);
        this.plz = plz;
    }

    public Land getLand() {
        return this.land;
    }

    public void setLand(final Land land) {
        Validator.getInstance().validateNotNull(land);
        this.land = land;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Adresse that)) return false;
        return this.getHausnummer() == that.getHausnummer() &&
                Objects.equals(this.getStrasse(), that.getStrasse()) &&
                Objects.equals(this.getZusatz(), that.getZusatz()) &&
                Objects.equals(this.getOrt(), that.getOrt()) &&
                Objects.equals(this.getPLZ(), that.getPLZ()) &&
                Objects.equals(this.getLand(), that.getLand());
    }

    @Override
    public Attribute[] getAttributeArray() {
        return new Attribute[] {
                new Attribute(Attributes.ADRESSE_ID.name(),
                        this,
                        Integer.class,
                        this.getAdresseID(),
                        this.getAdresseID(),
                        false,
                        false,
                        false,
                        true),
                new Attribute(Attributes.STRASSE.name(),
                        this,
                        String.class,
                        this.getStrasse(),
                        this.getStrasse(),
                        true),
                new Attribute(Attributes.HAUSNUMMER.name(),
                        this,
                        Integer.class,
                        this.getHausnummer(),
                        this.getHausnummer(),
                        true),
                new Attribute(Attributes.ZUSATZ.name(), this, Optional.class, this.getZusatz(), this.getZusatz(), true),
                new Attribute(Attributes.ORT.name(), this, String.class, this.getOrt(), this.getOrt(), true),
                new Attribute(Attributes.PLZ.name(), this, String.class, this.getPLZ(), this.getPLZ(), true),
                new Attribute(Attributes.LAND.name(), this, Land.class, this.getLand(), this.getLand(), true)
        };
    }

    @Override
    public String[] getCSVData() {
        final var csvData = new String[CSVPosition.values().length];
        csvData[CSVPosition.ADRESSE_ID.ordinal()] = Integer.toString(this.getAdresseID());
        csvData[CSVPosition.STRASSE.ordinal()] = this.getStrasse();
        csvData[CSVPosition.HAUSNUMMER.ordinal()] = Integer.toString(this.getHausnummer());
        csvData[CSVPosition.ZUSATZ.ordinal()] = this.getZusatz().orElse("");
        csvData[CSVPosition.ORT.ordinal()] = this.getOrt();
        csvData[CSVPosition.PLZ.ordinal()] = this.getPLZ();
        csvData[CSVPosition.LAND.ordinal()] = this.getLand().toString();
        csvData[CSVPosition.DUMMY_DATA.ordinal()] = "NULL";
        return csvData;
    }

    @Override
    public String[] getCSVHeader() {
        return new String[] {
                CSVPosition.ADRESSE_ID.name(),
                CSVPosition.STRASSE.name(),
                CSVPosition.HAUSNUMMER.name(),
                CSVPosition.ZUSATZ.name(),
                CSVPosition.ORT.name(),
                CSVPosition.PLZ.name(),
                CSVPosition.LAND.name(),
                CSVPosition.DUMMY_DATA.name()
        };
    }

    @Override
    public String getElementID() {
        return Integer.toString(this.getAdresseID());
    }

    @Override
    public Object getPrimaryKey() {
        return this.getAdresseID();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getAdresseID(),
                this.getStrasse(),
                this.getHausnummer(),
                this.getZusatz(),
                this.getOrt(),
                this.getPLZ(),
                this.getLand());
    }

    @Override
    @SuppressWarnings("unchecked")
    public Attribute[] setAttributeValues(final Attribute[] attributeArray) {
        final var oldAttributeArray = this.getAttributeArray();

        for (final var attribute : attributeArray) {
            final var name = attribute.getName();
            final var value = attribute.getValue();
            if (name.equals(Attributes.ADRESSE_ID.name()) && !value.equals(this.getAdresseID())) {
                throw new IllegalArgumentException("Adresse::setAttributeValues: Die AdresseID darf nicht verändert werden!");
            }

            if (name.equals(Attributes.STRASSE.name()) && !value.equals(this.getStrasse())) {
                this.setStrasse((String) value);
            } else if (name.equals(Attributes.HAUSNUMMER.name()) && !value.equals(this.getHausnummer())) {
                this.setHausnummer((int) value);
            } else if (name.equals(Attributes.ZUSATZ.name()) && !value.equals(this.getZusatz())) {
                this.setZusatz((Optional<String>) value);
            } else if (name.equals(Attributes.ORT.name()) && !value.equals(this.getOrt())) {
                this.setOrt((String) value);
            } else if (name.equals(Attributes.PLZ.name()) && !value.equals(this.getPLZ())) {
                this.setPLZ((String) value);
            } else if (name.equals(Attributes.LAND.name()) && !value.equals(this.getLand())) {
                this.setLand((Land) value);
            }
        }
        return oldAttributeArray;
    }

    @Override
    public String toString() {
        return "Adresse{" +
                "strasse='" + this.getStrasse() + '\'' +
                ", hausnummer=" + this.getHausnummer() +
                ", zusatz='" + this.getZusatz() + '\'' +
                ", ort='" + this.getOrt() + '\'' +
                ", plz='" + this.getPLZ() + '\'' +
                ", land='" + this.getLand() + '\'' +
                '}';
    }
}
