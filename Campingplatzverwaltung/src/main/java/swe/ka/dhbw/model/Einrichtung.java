package swe.ka.dhbw.model;

import de.dhbwka.swe.utils.model.Attribute;
import de.dhbwka.swe.utils.model.ICSVPersistable;
import de.dhbwka.swe.utils.model.IDepictable;
import de.dhbwka.swe.utils.model.IPersistable;
import swe.ka.dhbw.util.Validator;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public final class Einrichtung extends Anlage implements ICSVPersistable, IPersistable, IDepictable {
    public enum Attributes {
        ANLAGE_ID,
        LAGE_LATITUDE,
        LAGE_LONGITUDE,
        NAME,
        BESCHREIBUNG,
        LETZTE_WARTUNG
    }

    public enum CSVPosition {
        ANLAGE_ID,
        LAGE_LATITUDE,
        LAGE_LONGITUDE,
        NAME,
        BESCHREIBUNG,
        LETZTE_WARTUNG,
        OEFFNUNGSTAGE_IDS,
        ZUSTAENDIGE_FIRMA_ID,
        BEREICH_ID,
        FOTO_IDS,
        DUMMY_DATA
    }

    private String name;
    private String beschreibung;
    private LocalDateTime letzteWartung;
    private List<Oeffnungstag> oeffnungstage = new ArrayList<>();
    private Optional<Fremdfirma> zustaendigeFirma = Optional.empty();

    public Einrichtung(final int anlageId,
                       final GPSPosition lage,
                       final String name,
                       final String beschreibung,
                       final LocalDateTime letzteWartung) {
        super(anlageId, lage);
        this.setName(name);
        this.setBeschreibung(beschreibung);
        this.setLetzteWartung(letzteWartung);
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        Validator.getInstance().validateNotEmpty(name);
        this.name = name;
    }

    public String getBeschreibung() {
        return this.beschreibung;
    }

    public void setBeschreibung(final String beschreibung) {
        Validator.getInstance().validateNotNull(beschreibung);
        this.beschreibung = beschreibung;
    }

    public LocalDateTime getLetzteWartung() {
        return this.letzteWartung;
    }

    public void setLetzteWartung(final LocalDateTime letzteWartung) {
        Validator.getInstance().validateNotNull(letzteWartung);
        this.letzteWartung = letzteWartung;
    }

    public List<Oeffnungstag> getOeffnungstage() {
        return this.oeffnungstage;
    }

    @SuppressWarnings("unused")
    public void setOeffnungstage(final List<Oeffnungstag> oeffnungstage) {
        Validator.getInstance().validateNotNull(oeffnungstage);
        if (oeffnungstage.size() > 7) {
            throw new IllegalArgumentException("Es können maximal 7 Öffnungstage angegeben werden.");
        }

        this.oeffnungstage = oeffnungstage;
    }

    public Optional<Fremdfirma> getZustaendigeFirma() {
        return this.zustaendigeFirma;
    }

    public void setZustaendigeFirma(final Fremdfirma zustaendigeFirma) {
        this.setZustaendigeFirma(Optional.ofNullable(zustaendigeFirma));
    }

    public void setZustaendigeFirma(final Optional<Fremdfirma> zustaendigeFirma) {
        Validator.getInstance().validateNotNull(zustaendigeFirma);

        if (this.zustaendigeFirma.equals(zustaendigeFirma)) {
            return;
        }

        this.zustaendigeFirma.ifPresent(firma -> firma.removeEinrichtung(this));

        this.zustaendigeFirma = zustaendigeFirma;
        if (zustaendigeFirma.isPresent() && !zustaendigeFirma.get().getEinrichtungen().contains(this)) {
            zustaendigeFirma.get().addEinrichtung(this);
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof final Einrichtung that)) return false;
        if (!super.equals(o)) return false;
        return this.getAnlageId() == that.getAnlageId();
    }

    @Override
    public Attribute[] getAttributeArray() {
        final var superAttributes = super.getAttributeArray();
        final var attributes = Arrays.copyOf(superAttributes, superAttributes.length + 3);
        attributes[Einrichtung.Attributes.NAME.ordinal()] = new Attribute(
                Attributes.NAME.name(),
                this,
                String.class,
                this.getName(),
                this.getName(),
                true);
        attributes[Einrichtung.Attributes.BESCHREIBUNG.ordinal()] = new Attribute(
                Einrichtung.Attributes.BESCHREIBUNG.name(),
                this,
                String.class,
                this.getBeschreibung(),
                this.getBeschreibung(),
                true);
        attributes[Einrichtung.Attributes.LETZTE_WARTUNG.ordinal()] = new Attribute(
                Einrichtung.Attributes.LETZTE_WARTUNG.name(),
                this,
                String.class,
                this.getLetzteWartung(),
                this.getLetzteWartung(),
                true);
        return attributes;
    }

    @Override
    public String[] getCSVData() {
        final var csvData = new String[CSVPosition.values().length];
        csvData[CSVPosition.ANLAGE_ID.ordinal()] = Integer.toString(this.getAnlageId());
        csvData[CSVPosition.LAGE_LATITUDE.ordinal()] = Double.toString(this.getLage().getLatitude());
        csvData[CSVPosition.LAGE_LONGITUDE.ordinal()] = Double.toString(this.getLage().getLongitude());
        csvData[CSVPosition.NAME.ordinal()] = this.getName();
        csvData[CSVPosition.BESCHREIBUNG.ordinal()] = this.getBeschreibung();
        csvData[CSVPosition.LETZTE_WARTUNG.ordinal()] = this.getLetzteWartung().toString();
        csvData[CSVPosition.OEFFNUNGSTAGE_IDS.ordinal()] = this.getOeffnungstage().stream()
                .map(Oeffnungstag::getPrimaryKey)
                .map(Object::toString)
                .collect(Collectors.joining(","));
        csvData[CSVPosition.ZUSTAENDIGE_FIRMA_ID.ordinal()] = this.getZustaendigeFirma()
                .map(Fremdfirma::getPrimaryKey)
                .map(Object::toString)
                .orElse("");
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
                CSVPosition.NAME.name(),
                CSVPosition.BESCHREIBUNG.name(),
                CSVPosition.LETZTE_WARTUNG.name(),
                CSVPosition.OEFFNUNGSTAGE_IDS.name(),
                CSVPosition.ZUSTAENDIGE_FIRMA_ID.name(),
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

            if (name.equals(Einrichtung.Attributes.NAME.name()) && !value.equals(this.getName())) {
                this.setName((String) value);
            } else if (name.equals(Einrichtung.Attributes.BESCHREIBUNG.name()) && !value.equals(this.getBeschreibung())) {
                this.setBeschreibung((String) value);
            } else if (name.equals(Attributes.LETZTE_WARTUNG.name()) && !value.equals(this.getLetzteWartung())) {
                this.setLetzteWartung((LocalDateTime) value);
            }
        }
        return oldAttributeArray;
    }

    @Override
    public String toString() {
        return "Einrichtung{" +
                "name='" + this.getName() + '\'' +
                ", beschreibung='" + this.getBeschreibung() + '\'' +
                ", letzteWartung=" + this.getLetzteWartung() +
                ", oeffnungstage=[" + this.getOeffnungstage()
                .stream()
                .map(Objects::toString)
                .collect(Collectors.joining(", ")) + "]" +
                ", zustaendigeFirma=" + this.getZustaendigeFirma() +
                ", lage=" + this.getLage() +
                ", bereich=" + this.getBereich() +
                ", fotos=[" + this.getFotos().stream().map(Objects::toString).collect(Collectors.joining(", ")) + "]" +
                '}';
    }

    public void addOeffnungstag(final Oeffnungstag oeffnungstag) {
        Validator.getInstance().validateNotNull(oeffnungstag);
        if (this.oeffnungstage.size() > 7) {
            throw new IllegalArgumentException("Es können maximal 7 Öffnungstage angegeben werden.");
        }

        this.oeffnungstage.add(oeffnungstag);
    }

    @SuppressWarnings("unused")
    public void removeOeffnungstag(final Oeffnungstag oeffnungstag) {
        this.oeffnungstage.remove(oeffnungstag);
    }
}
