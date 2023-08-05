package swe.ka.dhbw.model;

import de.dhbwka.swe.utils.model.Attribute;
import de.dhbwka.swe.utils.model.ICSVPersistable;
import de.dhbwka.swe.utils.model.IDepictable;
import de.dhbwka.swe.utils.model.IPersistable;
import swe.ka.dhbw.util.Validator;

import java.util.*;
import java.util.stream.Collectors;

public final class Bereich extends Anlage implements ICSVPersistable, IDepictable, IPersistable {
    public enum Attributes {
        ANLAGE_ID,
        LAGE_LATITUDE,
        LAGE_LONGITUDE,
        KENNZEICHEN,
        BESCHREIBUNG
    }

    public enum CSVPosition {
        ANLAGEID,
        LAGE_LATITUDE,
        LAGE_LONGITUDE,
        KENNZEICHEN,
        BESCHREIBUNG,
        ANLAGEN_IDS,
        BEREICH_ID,
        FOTO_IDS,
        DUMMY_DATA
    }

    private final Set<Anlage> anlagen = new LinkedHashSet<>();
    private char kennzeichen;
    private String beschreibung;

    public Bereich(final int anlageId, final GPSPosition lage, final char kennzeichen, final String beschreibung) {
        super(anlageId, lage);
        this.setKennzeichen(kennzeichen);
        this.setBeschreibung(beschreibung);
    }

    public char getKennzeichen() {
        return kennzeichen;
    }

    public void setKennzeichen(char kennzeichen) {
        this.kennzeichen = kennzeichen;
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public void setBeschreibung(final String beschreibung) {
        Validator.getInstance().validateNotEmpty(beschreibung);
        this.beschreibung = beschreibung;
    }

    public Collection<Anlage> getAnlagen() {
        return this.anlagen;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof final Bereich that)) return false;
        if (!super.equals(o)) return false;
        return this.getAnlageId() == that.getAnlageId();
    }

    @Override
    public Attribute[] getAttributeArray() {
        final var superAttributes = super.getAttributeArray();
        final var attributes = Arrays.copyOf(superAttributes, superAttributes.length + 2);
        attributes[Attributes.KENNZEICHEN.ordinal()] = new Attribute(
                Attributes.KENNZEICHEN.name(),
                this,
                String.class,
                this.getKennzeichen(),
                this.getKennzeichen(),
                true);
        attributes[Attributes.BESCHREIBUNG.ordinal()] = new Attribute(
                Attributes.BESCHREIBUNG.name(),
                this,
                String.class,
                this.getBeschreibung(),
                this.getBeschreibung(),
                true);
        return attributes;
    }

    @Override
    public String[] getCSVData() {
        final var csvData = new String[CSVPosition.values().length];
        csvData[CSVPosition.ANLAGEID.ordinal()] = Integer.toString(this.getAnlageId());
        csvData[CSVPosition.LAGE_LATITUDE.ordinal()] = Double.toString(this.getLage().getLatitude());
        csvData[CSVPosition.LAGE_LONGITUDE.ordinal()] = Double.toString(this.getLage().getLongitude());
        csvData[CSVPosition.KENNZEICHEN.ordinal()] = Character.toString(this.getKennzeichen());
        csvData[CSVPosition.BESCHREIBUNG.ordinal()] = this.getBeschreibung();
        csvData[CSVPosition.ANLAGEN_IDS.ordinal()] = this.getAnlagen()
                .stream()
                .map(Anlage::getPrimaryKey)
                .map(Object::toString)
                .collect(Collectors.joining(","));
        csvData[CSVPosition.BEREICH_ID.ordinal()] = this.getBereich()
                .map(Bereich::getPrimaryKey)
                .map(Object::toString)
                .orElse("");
        csvData[CSVPosition.FOTO_IDS.ordinal()] = this.getFotos()
                .stream()
                .map(Foto::getPrimaryKey)
                .map(Object::toString)
                .collect(Collectors.joining(","));
        csvData[CSVPosition.DUMMY_DATA.ordinal()] = "NULL";
        return csvData;
    }

    @Override
    public String[] getCSVHeader() {
        return new String[] {
                CSVPosition.ANLAGEID.name(),
                CSVPosition.LAGE_LATITUDE.name(),
                CSVPosition.LAGE_LONGITUDE.name(),
                CSVPosition.KENNZEICHEN.name(),
                CSVPosition.BESCHREIBUNG.name(),
                CSVPosition.ANLAGEN_IDS.name(),
                CSVPosition.BEREICH_ID.name(),
                CSVPosition.FOTO_IDS.name(),
                CSVPosition.DUMMY_DATA.name()
        };
    }

    @Override
    public String getVisibleText() {
        return Character.toString(this.getKennzeichen());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.getAnlageId());
    }

    @Override
    public Attribute[] setAttributeValues(final Attribute[] attributeArray) {
        final var oldAttributeArray = this.getAttributeArray();

        super.setAttributeValues(attributeArray);

        for (final var attribute : attributeArray) {
            final var name = attribute.getName();
            final var value = attribute.getValue();

            if (name.equals(Attributes.KENNZEICHEN.name()) && !value.equals(this.getKennzeichen())) {
                this.setKennzeichen((char) value);
            } else if (name.equals(Attributes.BESCHREIBUNG.name()) && !value.equals(this.getBeschreibung())) {
                this.setBeschreibung((String) value);
            }
        }
        return oldAttributeArray;
    }

    @Override
    public String toString() {
        return "Bereich{" +
                "kennzeichen=" + this.getKennzeichen() +
                ", beschreibung='" + this.getBeschreibung() + '\'' +
                ", lage=" + this.getLage() +
                ", bereich=" + this.getBereich() +
                ", fotos=[" + this.getFotos().stream().map(Objects::toString).collect(Collectors.joining(", ")) + "]" +
                '}';
    }

    public void addAnlage(final Anlage anlage) {
        Validator.getInstance().validateNotNull(anlage);
        this.anlagen.add(anlage);
        if (!anlage.getBereich().equals(Optional.of(this))) {
            anlage.setBereich(Optional.of(this));
        }
    }

    @SuppressWarnings("unused")
    public void removeAnlage(final Anlage anlage) {
        this.anlagen.remove(anlage);
        anlage.setBereich(Optional.empty());
    }
}
