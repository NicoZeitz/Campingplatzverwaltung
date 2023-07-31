package swe.ka.dhbw.model;

import de.dhbwka.swe.utils.model.Attribute;
import de.dhbwka.swe.utils.model.ICSVPersistable;
import de.dhbwka.swe.utils.model.IDepictable;
import de.dhbwka.swe.utils.model.IPersistable;
import swe.ka.dhbw.util.Validator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

public final class Stellplatzfunktion extends Leistungsbeschreibung implements ICSVPersistable, IPersistable, IDepictable {
    public enum Status {
        AKTIV, INAKTIV, GESTOERT
    }

    public enum Attributes {
        LEISTUNGSBESCHREIBUNG_ID,
        GEBUEHR,
        MAXIMAL_ANZAHL,
        BESCHREIBUNG,
        STATUS
    }

    public enum CSVPosition {
        LEISTUNGSBESCHREIBUNG_ID,
        GEBUEHR,
        MAXIMAL_ANZAHL,
        BESCHREIBUNG,
        STATUS,
        STELLPLATZ_IDS,
        DUMMY_DATA
    }

    private final Set<Stellplatz> stellplaetze = new LinkedHashSet<>();
    private Status status;

    public Stellplatzfunktion(final int leistungsbeschreibungId,
                              final BigDecimal gebuehr,
                              final int maximalAnzahl,
                              final String beschreibung,
                              final Status status) {
        super(leistungsbeschreibungId, gebuehr, maximalAnzahl, beschreibung);
        this.setStatus(status);
    }

    public Status getStatus() {
        return this.status;
    }

    public void setStatus(final Status status) {
        Validator.getInstance().validateNotNull(status);
        this.status = status;
    }

    public Collection<Stellplatz> getStellplaetze() {
        return this.stellplaetze;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof final Stellplatzfunktion that)) return false;
        if (!super.equals(o)) return false;
        return this.getStatus() == that.getStatus();
    }

    @Override
    public Attribute[] getAttributeArray() {
        final var superAttributes = super.getAttributeArray();
        final var attributes = Arrays.copyOf(superAttributes, superAttributes.length + 1);
        attributes[Attributes.STATUS.ordinal()] = new Attribute(
                Attributes.STATUS.name(),
                this,
                Status.class,
                this.getStatus(),
                this.getStatus(),
                true);
        return attributes;
    }

    @Override
    public String[] getCSVData() {
        final var csvData = new String[CSVPosition.values().length];
        csvData[CSVPosition.LEISTUNGSBESCHREIBUNG_ID.ordinal()] = Integer.toString(this.getLeistungsbeschreibungId());
        csvData[CSVPosition.GEBUEHR.ordinal()] = this.getGebuehr().toString();
        csvData[CSVPosition.MAXIMAL_ANZAHL.ordinal()] = Integer.toString(this.getMaximalAnzahl());
        csvData[CSVPosition.BESCHREIBUNG.ordinal()] = this.getBeschreibung();
        csvData[CSVPosition.STATUS.ordinal()] = this.getStatus().name();
        csvData[CSVPosition.STELLPLATZ_IDS.ordinal()] = this.getStellplaetze().stream()
                .map(Stellplatz::getPrimaryKey)
                .map(Object::toString)
                .collect(Collectors.joining(","));
        csvData[CSVPosition.DUMMY_DATA.ordinal()] = "NULL";
        return csvData;
    }

    @Override
    public String[] getCSVHeader() {
        return new String[] {
                CSVPosition.LEISTUNGSBESCHREIBUNG_ID.name(),
                CSVPosition.GEBUEHR.name(),
                CSVPosition.MAXIMAL_ANZAHL.name(),
                CSVPosition.BESCHREIBUNG.name(),
                CSVPosition.STATUS.name(),
                CSVPosition.STELLPLATZ_IDS.name(),
                CSVPosition.DUMMY_DATA.name()
        };
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.getStatus());
    }

    @Override
    public Attribute[] setAttributeValues(final Attribute[] attributeArray) {
        final var oldAttributeArray = this.getAttributeArray();

        super.setAttributeValues(attributeArray);

        for (final var attribute : attributeArray) {
            final var name = attribute.getName();
            final var value = attribute.getValue();

            if (name.equals(Attributes.STATUS.name()) && !value.equals(this.getStatus())) {
                this.setStatus((Status) value);
            }
        }
        return oldAttributeArray;
    }

    @Override
    public String toString() {
        final var df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        final var price = df.format(this.getGebuehr().setScale(2, RoundingMode.HALF_EVEN)) + "€";
        return this.getBeschreibung() + "für den Stellplatz - " + price;
    }

    public void addStellplatz(final Stellplatz stellplatz) {
        Validator.getInstance().validateNotNull(stellplatz);
        this.stellplaetze.add(stellplatz);
        if (!stellplatz.getVerfuegbareFunktionen().contains(this)) {
            stellplatz.addVerfuegbareFunktion(this);
        }
    }

    public void removeStellplatz(final Stellplatz stellplatz) {
        Validator.getInstance().validateNotNull(stellplatz);
        this.stellplaetze.remove(stellplatz);
        if (stellplatz.getVerfuegbareFunktionen().contains(this)) {
            stellplatz.removeVerfuegbareFunktion(this);
        }
    }
}
