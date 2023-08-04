package swe.ka.dhbw.model;

import de.dhbwka.swe.utils.model.Attribute;
import de.dhbwka.swe.utils.model.ICSVPersistable;
import de.dhbwka.swe.utils.model.IDepictable;
import de.dhbwka.swe.utils.model.IPersistable;
import swe.ka.dhbw.util.Validator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class Stellplatz extends Anlage implements ICSVPersistable, IPersistable, IDepictable, Comparable<Stellplatz> {
    public enum Attributes {
        ANLAGE_ID,
        LAGE_LATITUDE,
        LAGE_LONGITUDE,
        STELLPLATZ,
        GEBUEHR,
        GROESSE,
        BARRIEREFREI,
        ANZAHL_WOHNWAGEN,
        ANZAHL_PKW,
        ANZAHL_ZELTE,
    }

    public enum CSVPosition {
        ANLAGE_ID,
        LAGE_LATITUDE,
        LAGE_LONGITUDE,
        STELLPLATZ,
        GEBUEHR,
        GROESSE,
        BARRIEREFREI,
        ANZAHL_WOHNWAGEN,
        ANZAHL_PKW,
        ANZAHL_ZELTE,
        VERFUEGBARE_FUNKTIONEN_IDS,
        BEREICH_ID,
        FOTO_IDS,
        DUMMY_DATA,
    }

    private final List<Stellplatzfunktion> verfuegbareFunktionen = new ArrayList<>();
    private final String stellplatz;
    private BigDecimal gebuehr;
    private double groesse;
    private boolean barrierefrei;
    private int anzahWohnwagen;
    private int anzahlPKW;
    private int anzahlZelte;

    public Stellplatz(final int anlageId,
                      final GPSPosition lage,
                      final String stellplatz,
                      final BigDecimal gebuehr,
                      final double groesse,
                      final boolean barrierefrei,
                      final int anzahWohnwagen,
                      final int anzahlPKW,
                      final int anzahlZelte) {
        super(anlageId, lage);
        Validator.getInstance().validateNotEmpty(stellplatz);
        this.stellplatz = stellplatz;
        this.setGebuehr(gebuehr);
        this.setGroesse(groesse);
        this.setBarrierefrei(barrierefrei);
        this.setAnzahWohnwagen(anzahWohnwagen);
        this.setAnzahlPKW(anzahlPKW);
        this.setAnzahlZelte(anzahlZelte);
    }

    public String getStellplatz() {
        return this.stellplatz;
    }

    public BigDecimal getGebuehr() {
        return this.gebuehr;
    }

    public void setGebuehr(final BigDecimal gebuehr) {
        Validator.getInstance().validateNotNull(gebuehr);
        Validator.getInstance().validateGreaterThanEqual(gebuehr.doubleValue(), 0d);
        this.gebuehr = gebuehr;
    }

    public double getGroesse() {
        return this.groesse;
    }

    public void setGroesse(final double groesse) {
        Validator.getInstance().validateGreaterThanEqual(groesse, 0d);
        this.groesse = groesse;
    }

    public boolean isBarrierefrei() {
        return this.barrierefrei;
    }

    public void setBarrierefrei(final boolean barrierefrei) {
        this.barrierefrei = barrierefrei;
    }

    public int getAnzahWohnwagen() {
        return this.anzahWohnwagen;
    }

    public void setAnzahWohnwagen(final int anzahWohnwagen) {
        Validator.getInstance().validateGreaterThanEqual(anzahWohnwagen, 0);
        this.anzahWohnwagen = anzahWohnwagen;
    }

    public int getAnzahlPKW() {
        return this.anzahlPKW;
    }

    public void setAnzahlPKW(final int anzahlPKW) {
        Validator.getInstance().validateGreaterThanEqual(anzahlPKW, 0);
        this.anzahlPKW = anzahlPKW;
    }

    public int getAnzahlZelte() {
        return this.anzahlZelte;
    }

    public void setAnzahlZelte(final int anzahlZelte) {
        Validator.getInstance().validateGreaterThanEqual(anzahlZelte, 0);
        this.anzahlZelte = anzahlZelte;
    }

    public List<Stellplatzfunktion> getVerfuegbareFunktionen() {
        return this.verfuegbareFunktionen;
    }

    @Override
    public int compareTo(final Stellplatz that) {
        final var thisLetter = Character.toUpperCase(this.getStellplatz().charAt(0));
        final var thatLetter = Character.toUpperCase(that.getStellplatz().charAt(0));

        if (thisLetter == thatLetter) {
            final var thisNumber = Integer.parseInt(this.getStellplatz().substring(1));
            final var thatNumber = Integer.parseInt(that.getStellplatz().substring(1));
            return Integer.compare(thisNumber, thatNumber);
        }

        if (thisLetter == 'N' || thatLetter == 'S') {
            return -1;
        }

        if (thatLetter == 'N' || thisLetter == 'S') {
            return 1;
        }

        if (thisLetter == 'W' || thatLetter == 'O') {
            return -1;
        }

        if (thatLetter == 'W' || thisLetter == 'O') {
            return 1;
        }

        return 0;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof final Stellplatz that)) return false;
        if (!super.equals(o)) return false;
        return this.getAnlageId() == that.getAnlageId();
    }

    @Override
    public Attribute[] getAttributeArray() {
        final var superAttributes = super.getAttributeArray();
        final var attributes = Arrays.copyOf(superAttributes, superAttributes.length + 7);
        attributes[Attributes.STELLPLATZ.ordinal()] = new Attribute(
                Attributes.STELLPLATZ.name(),
                this,
                String.class,
                this.getStellplatz(),
                this.getStellplatz(),
                true,
                false,
                false,
                true);
        attributes[Attributes.GEBUEHR.ordinal()] = new Attribute(
                Attributes.GEBUEHR.name(),
                this,
                BigDecimal.class,
                this.getGebuehr(),
                this.getGebuehr(),
                true);
        attributes[Attributes.GROESSE.ordinal()] = new Attribute(
                Attributes.GROESSE.name(),
                this,
                Double.class,
                this.getGroesse(),
                this.getGroesse(),
                true);
        attributes[Attributes.BARRIEREFREI.ordinal()] = new Attribute(
                Attributes.BARRIEREFREI.name(),
                this,
                Boolean.class,
                this.isBarrierefrei(),
                this.isBarrierefrei(),
                true);
        attributes[Attributes.ANZAHL_WOHNWAGEN.ordinal()] = new Attribute(
                Attributes.ANZAHL_WOHNWAGEN.name(),
                this,
                Integer.class,
                this.getAnzahWohnwagen(),
                this.getAnzahWohnwagen(),
                true);
        attributes[Attributes.ANZAHL_PKW.ordinal()] = new Attribute(
                Attributes.ANZAHL_PKW.name(),
                this,
                Integer.class,
                this.getAnzahlPKW(),
                this.getAnzahlPKW(),
                true);
        attributes[Attributes.ANZAHL_ZELTE.ordinal()] = new Attribute(
                Attributes.ANZAHL_ZELTE.name(),
                this,
                Integer.class,
                this.getAnzahlZelte(),
                this.getAnzahlZelte(),
                true);
        return attributes;
    }

    @Override
    public String[] getCSVData() {
        final var csvData = new String[CSVPosition.values().length];
        csvData[CSVPosition.ANLAGE_ID.ordinal()] = Integer.toString(this.getAnlageId());
        csvData[CSVPosition.LAGE_LATITUDE.ordinal()] = Double.toString(this.getLage().getLatitude());
        csvData[CSVPosition.LAGE_LONGITUDE.ordinal()] = Double.toString(this.getLage().getLongitude());
        csvData[CSVPosition.STELLPLATZ.ordinal()] = this.getStellplatz();
        csvData[CSVPosition.GEBUEHR.ordinal()] = this.getGebuehr().toString();
        csvData[CSVPosition.GROESSE.ordinal()] = Double.toString(this.getGroesse());
        csvData[CSVPosition.BARRIEREFREI.ordinal()] = Boolean.toString(this.isBarrierefrei());
        csvData[CSVPosition.ANZAHL_WOHNWAGEN.ordinal()] = Integer.toString(this.getAnzahWohnwagen());
        csvData[CSVPosition.ANZAHL_PKW.ordinal()] = Integer.toString(this.getAnzahlPKW());
        csvData[CSVPosition.ANZAHL_ZELTE.ordinal()] = Integer.toString(this.getAnzahlZelte());
        csvData[CSVPosition.VERFUEGBARE_FUNKTIONEN_IDS.ordinal()] = this.getVerfuegbareFunktionen().stream()
                .map(Stellplatzfunktion::getPrimaryKey)
                .map(Object::toString)
                .collect(Collectors.joining(","));
        csvData[CSVPosition.BEREICH_ID.ordinal()] = this.getBereich()
                .map(Bereich::getPrimaryKey)
                .map(Object::toString)
                .orElse("");
        csvData[CSVPosition.FOTO_IDS.ordinal()] = this.getFotos().stream()
                .map(Foto::getPrimaryKey)
                .map(Object::toString)
                .collect(Collectors.joining(","));
        csvData[CSVPosition.DUMMY_DATA.ordinal()] = "NULL";
        return csvData;
    }

    @Override
    public String[] getCSVHeader() {
        return new String[] {
                CSVPosition.ANLAGE_ID.name(),
                CSVPosition.LAGE_LATITUDE.name(),
                CSVPosition.LAGE_LONGITUDE.name(),
                CSVPosition.STELLPLATZ.name(),
                CSVPosition.GEBUEHR.name(),
                CSVPosition.GROESSE.name(),
                CSVPosition.BARRIEREFREI.name(),
                CSVPosition.ANZAHL_WOHNWAGEN.name(),
                CSVPosition.ANZAHL_PKW.name(),
                CSVPosition.ANZAHL_ZELTE.name(),
                CSVPosition.VERFUEGBARE_FUNKTIONEN_IDS.name(),
                CSVPosition.BEREICH_ID.name(),
                CSVPosition.FOTO_IDS.name(),
                CSVPosition.DUMMY_DATA.name()
        };
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

            if (name.equals(Attributes.STELLPLATZ.name()) && !value.equals(this.getStellplatz())) {
                throw new UnsupportedOperationException("Stellplatz::setAttributeValues: Stellplatz darf nicht verändert werden!");
            }

            if (name.equals(Attributes.GEBUEHR.name()) && !value.equals(this.getGebuehr())) {
                this.setGebuehr((BigDecimal) value);
            } else if (name.equals(Attributes.GROESSE.name()) && !value.equals(this.getGroesse())) {
                this.setGroesse((double) value);
            } else if (name.equals(Attributes.BARRIEREFREI.name()) && !value.equals(this.isBarrierefrei())) {
                this.setBarrierefrei((boolean) value);
            } else if (name.equals(Attributes.ANZAHL_WOHNWAGEN.name()) && !value.equals(this.getAnzahWohnwagen())) {
                this.setAnzahWohnwagen((int) value);
            } else if (name.equals(Attributes.ANZAHL_PKW.name()) && !value.equals(this.getAnzahlPKW())) {
                this.setAnzahlPKW((int) value);
            } else if (name.equals(Attributes.ANZAHL_ZELTE.name()) && !value.equals(this.getAnzahlZelte())) {
                this.setAnzahlZelte((int) value);
            }
        }
        return oldAttributeArray;
    }

    @Override
    public String toString() {
        final var foto = this.getFotos().stream().findAny();
        final var beschreibung = foto.map(value -> " (" + value.getTitel() + ")").orElse("");
        return "Stellplatz " + this.getStellplatz() + beschreibung;
    }

    public void addVerfuegbareFunktion(final Stellplatzfunktion funktion) {
        Validator.getInstance().validateNotNull(funktion);
        this.verfuegbareFunktionen.add(funktion);
        if (!funktion.getStellplaetze().contains(this)) {
            funktion.addStellplatz(this);
        }
    }

    public void removeVerfuegbareFunktion(final Stellplatzfunktion funktion) {
        Validator.getInstance().validateNotNull(funktion);
        this.verfuegbareFunktionen.remove(funktion);
        if (funktion.getStellplaetze().contains(this)) {
            funktion.removeStellplatz(this);
        }
    }
}
