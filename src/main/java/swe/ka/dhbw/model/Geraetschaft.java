package swe.ka.dhbw.model;

import de.dhbwka.swe.utils.model.Attribute;
import de.dhbwka.swe.utils.model.ICSVPersistable;
import de.dhbwka.swe.utils.model.IDepictable;
import de.dhbwka.swe.utils.model.IPersistable;
import swe.ka.dhbw.util.Validator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

public final class Geraetschaft extends Leistungsbeschreibung implements ICSVPersistable, IPersistable, IDepictable {
    public enum Attributes {
        LEISTUNGSBESCHREIBUNG_ID,
        GEBUEHR,
        MAXIMAL_ANZAHL,
        BESCHREIBUNG,
        ANSCHAFFUNGSDATUM,
        ZUSTAND
    }

    public enum CSVPosition {
        LEISTUNGSBESCHREIBUNG_ID,
        GEBUEHR,
        MAXIMAL_ANZAHL,
        BESCHREIBUNG,
        ANSCHAFFUNGSDATUM,
        ZUSTAND,
        DUMMY_DATA
    }

    private LocalDate anschaffungsdatum;
    private String zustand;

    public Geraetschaft(final int leistungsbeschreibungId,
                        final BigDecimal gebuehr,
                        final int maximalAnzahl,
                        final String beschreibung,
                        final LocalDate anschaffungsdatum,
                        final String zustand) {
        super(leistungsbeschreibungId, gebuehr, maximalAnzahl, beschreibung);
        this.setAnschaffungsdatum(anschaffungsdatum);
        this.setZustand(zustand);
    }

    public LocalDate getAnschaffungsdatum() {
        return this.anschaffungsdatum;
    }

    public void setAnschaffungsdatum(final LocalDate anschaffungsdatum) {
        Validator.getInstance().validateNotNull(anschaffungsdatum);
        this.anschaffungsdatum = anschaffungsdatum;
    }

    public String getZustand() {
        return this.zustand;
    }

    public void setZustand(final String zustand) {
        Validator.getInstance().validateNotNull(zustand);
        this.zustand = zustand;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof final Geraetschaft that)) return false;
        if (!super.equals(o)) return false;
        return this.getLeistungsbeschreibungId() == that.getLeistungsbeschreibungId();
    }

    @Override
    public Attribute[] getAttributeArray() {
        final var superAttributes = super.getAttributeArray();
        final var attributes = Arrays.copyOf(superAttributes, superAttributes.length + 2);
        attributes[Attributes.ANSCHAFFUNGSDATUM.ordinal()] = new Attribute(
                Attributes.ANSCHAFFUNGSDATUM.name(),
                this,
                LocalDate.class,
                this.getAnschaffungsdatum(),
                this.getAnschaffungsdatum(),
                true);
        attributes[Attributes.ZUSTAND.ordinal()] = new Attribute(
                Attributes.ZUSTAND.name(),
                this,
                String.class,
                this.getZustand(),
                this.getZustand(),
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
        csvData[CSVPosition.ANSCHAFFUNGSDATUM.ordinal()] = this.getAnschaffungsdatum().toString();
        csvData[CSVPosition.ZUSTAND.ordinal()] = this.getZustand();
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
                CSVPosition.ANSCHAFFUNGSDATUM.name(),
                CSVPosition.ZUSTAND.name(),
                CSVPosition.DUMMY_DATA.name()
        };
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.getLeistungsbeschreibungId());
    }

    @Override
    public Attribute[] setAttributeValues(final Attribute[] attributeArray) {
        final var oldAttributeArray = this.getAttributeArray();

        super.setAttributeValues(attributeArray);

        for (final var attribute : attributeArray) {
            final var name = attribute.getName();
            final var value = attribute.getValue();

            if (name.equals(Attributes.ANSCHAFFUNGSDATUM.name()) && !value.equals(this.getAnschaffungsdatum())) {
                this.setAnschaffungsdatum((LocalDate) value);
            } else if (name.equals(Attributes.ZUSTAND.name()) && !value.equals(this.getZustand())) {
                this.setZustand((String) value);
            }
        }
        return oldAttributeArray;
    }

    @Override
    public String toString() {
        final var df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        final var price = df.format(this.getGebuehr().setScale(2, RoundingMode.HALF_EVEN)) + "€";
        final var purchaseDate = this.getAnschaffungsdatum().format(DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.GERMANY));
        return this.getBeschreibung() + " - " + price + " (" + this.getZustand() + ", angeschafft am " + purchaseDate + ")";
    }
}
