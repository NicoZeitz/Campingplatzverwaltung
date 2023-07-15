package swe.ka.dhbw.model;

import de.dhbwka.swe.utils.model.Attribute;
import de.dhbwka.swe.utils.model.IDepictable;
import de.dhbwka.swe.utils.model.IPersistable;
import swe.ka.dhbw.util.Validator;

import java.math.BigDecimal;
import java.util.Objects;

public abstract class Leistungsbeschreibung implements IPersistable, IDepictable {
    public enum Attributes {
        LEISTUNGSBESCHREIBUNG_ID,
        GEBUEHR,
        MAXIMAL_ANZAHL,
        BESCHREIBUNG
    }

    protected final int leistungsbeschreibungId;
    protected BigDecimal gebuehr;
    protected int maximalAnzahl;
    protected String beschreibung;

    public Leistungsbeschreibung(final int leistungsbeschreibungId,
                                 final BigDecimal gebuehr,
                                 final int maximalAnzahl,
                                 final String beschreibung) {
        Validator.getInstance().validateGreaterThanEqual(leistungsbeschreibungId, 0);
        this.leistungsbeschreibungId = leistungsbeschreibungId;
        this.setGebuehr(gebuehr);
        this.setMaximalAnzahl(maximalAnzahl);
        this.setBeschreibung(beschreibung);
    }

    public int getLeistungsbeschreibungId() {
        return this.leistungsbeschreibungId;
    }

    public BigDecimal getGebuehr() {
        return this.gebuehr;
    }

    public void setGebuehr(final BigDecimal gebuehr) {
        Validator.getInstance().validateNotNull(gebuehr);
        Validator.getInstance().validateGreaterThanEqual(gebuehr.doubleValue(), 0d);
        this.gebuehr = gebuehr;
    }

    public int getMaximalAnzahl() {
        return this.maximalAnzahl;
    }

    public void setMaximalAnzahl(final int maximalAnzahl) {
        Validator.getInstance().validateGreaterThanEqual(maximalAnzahl, 0);
        this.maximalAnzahl = maximalAnzahl;
    }

    public String getBeschreibung() {
        return this.beschreibung;
    }

    public void setBeschreibung(final String beschreibung) {
        Validator.getInstance().validateNotNull(beschreibung);
        this.beschreibung = beschreibung;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof final Leistungsbeschreibung that)) return false;
        return this.getMaximalAnzahl() == that.getMaximalAnzahl() &&
                Objects.equals(this.getGebuehr(), that.getGebuehr()) &&
                Objects.equals(this.getBeschreibung(), that.getBeschreibung());
    }

    @Override
    public Attribute[] getAttributeArray() {
        return new Attribute[] {
                new Attribute(Attributes.LEISTUNGSBESCHREIBUNG_ID.name(),
                        this,
                        Integer.class,
                        this.getLeistungsbeschreibungId(),
                        this.getLeistungsbeschreibungId(),
                        false,
                        false,
                        false,
                        true),
                new Attribute(Attributes.GEBUEHR.name(), this, BigDecimal.class, this.getGebuehr(), this.getGebuehr(), true),
                new Attribute(Attributes.MAXIMAL_ANZAHL.name(),
                        this,
                        Integer.class,
                        this.getMaximalAnzahl(),
                        this.getMaximalAnzahl(),
                        true),
                new Attribute(Attributes.BESCHREIBUNG.name(),
                        this,
                        String.class,
                        this.getBeschreibung(),
                        this.getBeschreibung(),
                        true),
        };
    }

    @Override
    public String getElementID() {
        return Integer.toString(this.getLeistungsbeschreibungId());
    }

    @Override
    public Object getPrimaryKey() {
        return this.getLeistungsbeschreibungId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getGebuehr(), this.getMaximalAnzahl(), this.getBeschreibung());
    }

    @Override
    public Attribute[] setAttributeValues(final Attribute[] attributeArray) {
        final var oldAttributeArray = this.getAttributeArray();

        for (final var attribute : attributeArray) {
            final var name = attribute.getName();
            final var value = attribute.getValue();
            if (name.equals(Attributes.LEISTUNGSBESCHREIBUNG_ID.name()) && !value.equals(this.getLeistungsbeschreibungId())) {
                throw new IllegalArgumentException(
                        "Leistungsbeschreibung::setAttributeValues: Die LeistungsbeschreibungId darf nicht verändert werden!");
            }

            if (name.equals(Attributes.GEBUEHR.name()) && !value.equals(this.getGebuehr())) {
                this.setGebuehr((BigDecimal) value);
            } else if (name.equals(Attributes.MAXIMAL_ANZAHL.name()) && !value.equals(this.getMaximalAnzahl())) {
                this.setMaximalAnzahl((int) value);
            } else if (name.equals(Attributes.BESCHREIBUNG.name()) && !value.equals(this.getBeschreibung())) {
                this.setBeschreibung((String) value);
            }
        }
        return oldAttributeArray;
    }

    @Override
    public String toString() {
        return "Leistungsbeschreibung{" +
                "gebuehr=" + gebuehr +
                ", maximalAnzahl=" + maximalAnzahl +
                ", beschreibung='" + beschreibung +
                '}';
    }
}
