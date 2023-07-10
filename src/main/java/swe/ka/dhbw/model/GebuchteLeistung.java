package swe.ka.dhbw.model;

import de.dhbwka.swe.utils.model.Attribute;
import de.dhbwka.swe.utils.model.ICSVPersistable;
import de.dhbwka.swe.utils.model.IDepictable;
import de.dhbwka.swe.utils.model.IPersistable;
import swe.ka.dhbw.util.Validator;

import java.time.LocalDate;
import java.util.Objects;

public final class GebuchteLeistung implements IDepictable, IPersistable, ICSVPersistable {
    public enum Attributes {
        GEBUCHTE_LEISTUNG_ID,
        BUCHUNG_START,
        BUCHUNGS_ENDE
    }

    public enum CSVPosition {
        GEBUCHTE_LEISTUNG_ID,
        BUCHUNG_START,
        BUCHUNGS_ENDE,
        LEISTUNGSBESCHREIBUNG_ID,
        DUMMY_DATA
    }

    private final int gebuchteLeistungId;
    private LocalDate buchungStart;
    private LocalDate buchungsEnde;
    private Leistungsbeschreibung leistungsbeschreibung;

    public GebuchteLeistung(final int gebuchteLeistungId, final LocalDate buchungStart, final LocalDate buchungsEnde) {
        Validator.getInstance().validateGreaterThanEqual(gebuchteLeistungId, 0);
        this.gebuchteLeistungId = gebuchteLeistungId;
        this.setBuchungStart(buchungStart);
        this.setBuchungsEnde(buchungsEnde);
    }

    public int getGebuchteLeistungId() {
        return this.gebuchteLeistungId;
    }

    public LocalDate getBuchungStart() {
        return this.buchungStart;
    }

    public void setBuchungStart(final LocalDate buchungStart) {
        Validator.getInstance().validateNotNull(buchungStart);
        this.buchungStart = buchungStart;
    }

    public LocalDate getBuchungsEnde() {
        return this.buchungsEnde;
    }

    public void setBuchungsEnde(final LocalDate buchungsEnde) {
        Validator.getInstance().validateNotNull(buchungsEnde);
        this.buchungsEnde = buchungsEnde;
    }

    public Leistungsbeschreibung getLeistungsbeschreibung() {
        return this.leistungsbeschreibung;
    }

    public void setLeistungsbeschreibung(final Leistungsbeschreibung leistungsbeschreibung) {
        Validator.getInstance().validateNotNull(leistungsbeschreibung);
        this.leistungsbeschreibung = leistungsbeschreibung;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof final GebuchteLeistung that)) return false;
        return Objects.equals(this.getBuchungStart(), that.getBuchungStart()) &&
                Objects.equals(this.getBuchungsEnde(), that.getBuchungsEnde()) &&
                Objects.equals(this.getLeistungsbeschreibung(), that.getLeistungsbeschreibung());
    }

    @Override
    public Attribute[] getAttributeArray() {
        return new Attribute[] {
                new Attribute(Attributes.GEBUCHTE_LEISTUNG_ID.name(),
                        this,
                        Integer.class,
                        this.getGebuchteLeistungId(),
                        this.getGebuchteLeistungId(),
                        false,
                        false,
                        false,
                        true),
                new Attribute(Attributes.BUCHUNG_START.name(),
                        this,
                        LocalDate.class,
                        this.getBuchungStart(),
                        this.getBuchungStart(),
                        true),
                new Attribute(Attributes.BUCHUNGS_ENDE.name(),
                        this,
                        LocalDate.class,
                        this.getBuchungsEnde(),
                        this.getBuchungsEnde(),
                        true),
        };
    }

    @Override
    public String[] getCSVData() {
        final var csvData = new String[CSVPosition.values().length];
        csvData[CSVPosition.GEBUCHTE_LEISTUNG_ID.ordinal()] = Integer.toString(this.getGebuchteLeistungId());
        csvData[CSVPosition.BUCHUNG_START.ordinal()] = this.getBuchungStart().toString();
        csvData[CSVPosition.BUCHUNGS_ENDE.ordinal()] = this.getBuchungsEnde().toString();
        csvData[CSVPosition.LEISTUNGSBESCHREIBUNG_ID.ordinal()] = this.getLeistungsbeschreibung().getPrimaryKey().toString();
        csvData[CSVPosition.DUMMY_DATA.ordinal()] = "NULL";
        return csvData;
    }

    @Override
    public String[] getCSVHeader() {
        return new String[] {
                CSVPosition.GEBUCHTE_LEISTUNG_ID.name(),
                CSVPosition.BUCHUNG_START.name(),
                CSVPosition.BUCHUNGS_ENDE.name(),
                CSVPosition.LEISTUNGSBESCHREIBUNG_ID.name(),
                CSVPosition.DUMMY_DATA.name()
        };
    }

    @Override
    public String getElementID() {
        return Integer.toString(this.getGebuchteLeistungId());
    }

    @Override
    public Object getPrimaryKey() {
        return this.getGebuchteLeistungId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getBuchungStart(), this.getBuchungsEnde(), this.getLeistungsbeschreibung());
    }

    @Override
    public Attribute[] setAttributeValues(Attribute[] attributeArray) {
        final var oldAttributeArray = this.getAttributeArray();

        for (final var attribute : attributeArray) {
            final var name = attribute.getName();
            final var value = attribute.getValue();

            if (name.equals(Attributes.GEBUCHTE_LEISTUNG_ID.name()) && !value.equals(this.getGebuchteLeistungId())) {
                throw new UnsupportedOperationException(
                        "GebuchteLeistung::setAttributeValues: GebuchteLeistungId darf nicht verändert werden!");
            }

            if (name.equals(Attributes.BUCHUNG_START.name()) && !value.equals(this.getBuchungStart())) {
                this.setBuchungStart((LocalDate) value);
            } else if (name.equals(Attributes.BUCHUNGS_ENDE.name()) && !value.equals(this.getBuchungsEnde())) {
                this.setBuchungsEnde((LocalDate) value);
            }
        }
        return oldAttributeArray;
    }

    @Override
    public String toString() {
        return "GebuchteLeistung{" +
                "buchungStart=" + this.getBuchungStart() +
                ", buchungsEnde=" + this.getBuchungsEnde() +
                ", leistungsbeschreibung=" + this.getLeistungsbeschreibung() +
                '}';
    }
}
