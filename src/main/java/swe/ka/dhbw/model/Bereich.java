package swe.ka.dhbw.model;

import de.dhbwka.swe.utils.model.Attribute;
import swe.ka.dhbw.util.Validator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Bereich extends Anlage {
    private char kennzeichen;
    private String beschreibung;
    private List<Anlage> anlagen;

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

    public List<Anlage> getAnlagen() {
        return this.anlagen;
    }

    public void addAnlage(final Anlage anlage) {
        Validator.getInstance().validateNotNull(anlage);
        this.anlagen.add(anlage);
    }

    public void removeAnlage(final Anlage anlage) {
        this.anlagen.remove(anlage);
    }

    @Override
    public Object getPrimaryKey() {
        return this.getAnlageId();
    }

    @Override
    public String getElementID() {
        return Integer.toString(this.getAnlageId());
    }

    @Override
    public String[] getCSVHeader() {
        return new String[] {
                CSVPosition.ANLAGEID.name(),
                CSVPosition.LAGE_LATITUDE.name(),
                CSVPosition.LAGE_LONGITUDE.name(),
                CSVPosition.KENNZEICHEN.name(),
                CSVPosition.BESCHREIBUNG.name(),
                CSVPosition.ANLAGEN_IDS.name()
        };
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
        return csvData;
    }

    @Override
    public Attribute[] getAttributeArray() {
        final var superAttributes = super.getAttributeArray();
        final var attributes = new Attribute[superAttributes.length + 2];
        Arrays.copyOfRange(superAttributes, 0, superAttributes.length - 1);
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
    public Attribute[] setAttributeValues(final Attribute[] attributeArray) {
        final var oldAttributeArray = this.getAttributeArray().clone();

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
        ANLAGEN_IDS
    }
}
