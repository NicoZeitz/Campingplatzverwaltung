package swe.ka.dhbw.model;

import de.dhbwka.swe.utils.model.Attribute;
import de.dhbwka.swe.utils.model.ICSVPersistable;
import de.dhbwka.swe.utils.model.IDepictable;
import de.dhbwka.swe.utils.model.IPersistable;
import swe.ka.dhbw.util.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class Fremdfirma implements ICSVPersistable, IPersistable, IDepictable {
    public enum Attributes {
        FREMDFIRMA_ID,
        NAME,
    }

    public enum CSVPosition {
        FREMDFIRMA_ID,
        NAME,
        ANSCHRIFT_ID,
        ANSPRECHPERSON_ID,
        WARTUNG_IDS,
        DUMMY_DATA
    }

    private final int fremdfirmaID;
    private final List<Wartung> wartungen = new ArrayList<>();
    private String name;
    private Adresse anschrift;
    private Person ansprechperson;

    public Fremdfirma(final int fremdfirmaID, final String name) {
        Validator.getInstance().validateGreaterThanEqual(fremdfirmaID, 0);
        this.fremdfirmaID = fremdfirmaID;
        this.setName(name);
    }

    public int getFremdfirmaID() {
        return this.fremdfirmaID;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        Validator.getInstance().validateNotEmpty(name);
        this.name = name;
    }

    public Adresse getAnschrift() {
        return this.anschrift;
    }

    public void setAnschrift(final Adresse anschrift) {
        Validator.getInstance().validateNotNull(anschrift);
        this.anschrift = anschrift;
    }

    public Person getAnsprechperson() {
        return this.ansprechperson;
    }

    public void setAnsprechperson(final Person ansprechperson) {
        Validator.getInstance().validateNotNull(ansprechperson);
        this.ansprechperson = ansprechperson;
    }

    public List<Wartung> getWartungen() {
        return this.wartungen;
    }

    public void addWartung(final Wartung wartung) {
        Validator.getInstance().validateNotNull(wartung);
        this.wartungen.add(wartung);
    }

    public void removeWartung(final Wartung wartung) {
        Validator.getInstance().validateNotNull(wartung);
        this.wartungen.remove(wartung);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof final Fremdfirma that)) return false;
        return Objects.equals(this.getName(), that.getName()) &&
                Objects.equals(this.getAnschrift(), that.getAnschrift()) &&
                Objects.equals(this.getAnsprechperson(), that.getAnsprechperson()) &&
                Objects.equals(this.getWartungen(), that.getWartungen());
    }

    @Override
    public Attribute[] getAttributeArray() {
        return new Attribute[]{
                new Attribute(Attributes.FREMDFIRMA_ID.name(),
                        this,
                        Integer.class,
                        this.getFremdfirmaID(),
                        this.getFremdfirmaID(),
                        false,
                        false,
                        false,
                        true),
                new Attribute(Attributes.NAME.name(), this, String.class, this.getName(), this.getName(), true)
        };
    }

    @Override
    public String[] getCSVData() {
        final var csvData = new String[CSVPosition.values().length];
        csvData[CSVPosition.FREMDFIRMA_ID.ordinal()] = Integer.toString(this.getFremdfirmaID());
        csvData[CSVPosition.NAME.ordinal()] = this.getName();
        csvData[CSVPosition.ANSCHRIFT_ID.ordinal()] = Integer.toString(this.getAnschrift().getAdresseID());
        csvData[CSVPosition.ANSPRECHPERSON_ID.ordinal()] = this.getAnsprechperson().getPrimaryKey().toString();
        csvData[CSVPosition.WARTUNG_IDS.ordinal()] = this.getWartungen().stream()
                .map(wartung -> wartung.getPrimaryKey().toString())
                .collect(Collectors.joining(","));
        csvData[CSVPosition.DUMMY_DATA.ordinal()] = "NULL";
        return csvData;
    }

    @Override
    public String[] getCSVHeader() {
        return new String[]{
                CSVPosition.FREMDFIRMA_ID.name(),
                CSVPosition.NAME.name(),
                CSVPosition.ANSCHRIFT_ID.name(),
                CSVPosition.ANSPRECHPERSON_ID.name(),
                CSVPosition.WARTUNG_IDS.name(),
                CSVPosition.DUMMY_DATA.name()
        };
    }

    @Override
    public String getElementID() {
        return Integer.toString(this.getFremdfirmaID());
    }

    @Override
    public Object getPrimaryKey() {
        return this.getFremdfirmaID();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getName(), this.getAnschrift(), this.getAnsprechperson(), this.getWartungen());
    }

    @Override
    public Attribute[] setAttributeValues(final Attribute[] attributeArray) {
        final var oldAttributeArray = this.getAttributeArray();

        for (final var attribute : attributeArray) {
            final var name = attribute.getName();
            final var value = attribute.getValue();

            if (name.equals(Attributes.FREMDFIRMA_ID.name()) && !value.equals(this.getFremdfirmaID())) {
                throw new UnsupportedOperationException(
                        "Fremdfirma::setAttributeValues: FremdfirmaId darf nicht verändert werden!");
            }

            if (name.equals(Attributes.NAME.name()) && !value.equals(this.getName())) {
                this.setName((String) value);
            }
        }
        return oldAttributeArray;
    }

    @Override
    public String toString() {
        return "Fremdfirma{" +
                "name='" + this.getName() + '\'' +
                ", anschrift=" + this.getAnschrift() +
                ", ansprechperson=" + this.getAnsprechperson() +
                ", wartungen=[" + this.getWartungen().stream().map(Objects::toString).collect(Collectors.joining(", ")) + "]" +
                '}';
    }
}
