package swe.ka.dhbw.model;

import de.dhbwka.swe.utils.model.Attribute;
import de.dhbwka.swe.utils.model.ICSVPersistable;
import de.dhbwka.swe.utils.model.IDepictable;
import de.dhbwka.swe.utils.model.IPersistable;
import swe.ka.dhbw.util.Validator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

public class Wartung implements ICSVPersistable, IPersistable, IDepictable {
    public enum Attributes {
        WARTUNGSNUMMER,
        DUERCHFUEHRUNGSDATUM,
        RECHNUNGSDATUM,
        AUFTRAGSNUMMER,
        RECHNUNGSNUMMER,
        KOSTEN,
    }

    public enum CSVPosition {
        WARTUNGSNUMMER,
        DUERCHFUEHRUNGSDATUM,
        RECHNUNGSDATUM,
        AUFTRAGSNUMMER,
        RECHNUNGSNUMMER,
        KOSTEN,
        ZUSTAENDIGE_FIRMA_ID,
        ANLAGE_ID,
        DUMMY_DATA
    }

    private final int wartungsnummer;
    private LocalDate duerchfuehrungsdatum;
    private LocalDate rechnungsdatum;
    private String auftragsnummer;
    private String rechnungsnummer;
    private BigDecimal kosten;
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private Optional<Fremdfirma> zustaendigeFirma = Optional.empty();

    private Anlage anlage;

    public Wartung(final int wartungsnummer,
                   final LocalDate duerchfuehrungsdatum,
                   final LocalDate rechnungsdatum,
                   final String auftragsnummer,
                   final String rechnungsnummer,
                   final BigDecimal kosten) {
        Validator.getInstance().validateGreaterThanEqual(wartungsnummer, 0);
        this.wartungsnummer = wartungsnummer;
        this.setDuerchfuehrungsdatum(duerchfuehrungsdatum);
        this.setRechnungsdatum(rechnungsdatum);
        this.setAuftragsnummer(auftragsnummer);
        this.setRechnungsnummer(rechnungsnummer);
        this.setKosten(kosten);
    }

    public int getWartungsnummer() {
        return this.wartungsnummer;
    }

    public LocalDate getDuerchfuehrungsdatum() {
        return this.duerchfuehrungsdatum;
    }

    public void setDuerchfuehrungsdatum(final LocalDate duerchfuehrungsdatum) {
        Validator.getInstance().validateNotNull(duerchfuehrungsdatum);
        this.duerchfuehrungsdatum = duerchfuehrungsdatum;
    }

    public LocalDate getRechnungsdatum() {
        return this.rechnungsdatum;
    }

    public void setRechnungsdatum(final LocalDate rechnungsdatum) {
        Validator.getInstance().validateNotNull(rechnungsdatum);
        this.rechnungsdatum = rechnungsdatum;
    }

    public String getAuftragsnummer() {
        return this.auftragsnummer;
    }

    public void setAuftragsnummer(final String auftragsnummer) {
        Validator.getInstance().validateNotEmpty(auftragsnummer);
        this.auftragsnummer = auftragsnummer;
    }

    public String getRechnungsnummer() {
        return this.rechnungsnummer;
    }

    public void setRechnungsnummer(final String rechnungsnummer) {
        Validator.getInstance().validateNotEmpty(rechnungsnummer);
        this.rechnungsnummer = rechnungsnummer;
    }

    public BigDecimal getKosten() {
        return this.kosten;
    }

    public void setKosten(final BigDecimal kosten) {
        Validator.getInstance().validateNotNull(kosten);
        this.kosten = kosten;
    }

    public Optional<Fremdfirma> getZustaendigeFirma() {
        return this.zustaendigeFirma;
    }

    public void setZustaendigeFirma(final Fremdfirma zustaendigeFirma) {
        this.setZustaendigeFirma(Optional.ofNullable(zustaendigeFirma));
    }

    public void setZustaendigeFirma(@SuppressWarnings("OptionalUsedAsFieldOrParameterType") final Optional<Fremdfirma> zustaendigeFirma) {
        Validator.getInstance().validateNotNull(zustaendigeFirma);
        this.zustaendigeFirma = zustaendigeFirma;
    }

    public Anlage getAnlage() {
        return this.anlage;
    }

    public void setAnlage(final Anlage anlage) {
        Validator.getInstance().validateNotNull(anlage);
        this.anlage = anlage;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof final Wartung that)) return false;
        return this.getWartungsnummer() == that.getWartungsnummer() &&
                Objects.equals(this.getDuerchfuehrungsdatum(), that.getDuerchfuehrungsdatum()) &&
                Objects.equals(this.getRechnungsdatum(), that.getRechnungsdatum()) &&
                Objects.equals(this.getAuftragsnummer(), that.getAuftragsnummer()) &&
                Objects.equals(this.getRechnungsnummer(), that.getRechnungsnummer()) &&
                Objects.equals(this.getKosten(), that.getKosten()) &&
                Objects.equals(this.getZustaendigeFirma(), that.getZustaendigeFirma()) &&
                Objects.equals(this.getAnlage(), that.getAnlage());
    }

    @Override
    public Attribute[] getAttributeArray() {
        return new Attribute[] {
                new Attribute(Attributes.WARTUNGSNUMMER.name(),
                        this,
                        Integer.class,
                        this.getWartungsnummer(),
                        this.getWartungsnummer(),
                        false,
                        false,
                        false,
                        true),
                new Attribute(Attributes.DUERCHFUEHRUNGSDATUM.name(),
                        this,
                        LocalDate.class,
                        this.getDuerchfuehrungsdatum(),
                        this.getDuerchfuehrungsdatum(),
                        true),
                new Attribute(Attributes.RECHNUNGSDATUM.name(),
                        this,
                        LocalDate.class,
                        this.getRechnungsdatum(),
                        this.getRechnungsdatum(),
                        true),
                new Attribute(Attributes.AUFTRAGSNUMMER.name(),
                        this,
                        String.class,
                        this.getAuftragsnummer(),
                        this.getAuftragsnummer(),
                        true),
                new Attribute(Attributes.RECHNUNGSNUMMER.name(),
                        this,
                        String.class,
                        this.getRechnungsnummer(),
                        this.getRechnungsnummer(),
                        true),
                new Attribute(Attributes.KOSTEN.name(), this, BigDecimal.class, this.getKosten(), this.getKosten(), true)
        };
    }

    @Override
    public String[] getCSVData() {
        final var csvData = new String[CSVPosition.values().length];
        csvData[CSVPosition.WARTUNGSNUMMER.ordinal()] = Integer.toString(this.getWartungsnummer());
        csvData[CSVPosition.DUERCHFUEHRUNGSDATUM.ordinal()] = this.getDuerchfuehrungsdatum().toString();
        csvData[CSVPosition.RECHNUNGSDATUM.ordinal()] = this.getRechnungsdatum().toString();
        csvData[CSVPosition.AUFTRAGSNUMMER.ordinal()] = this.getAuftragsnummer();
        csvData[CSVPosition.RECHNUNGSNUMMER.ordinal()] = this.getRechnungsnummer();
        csvData[CSVPosition.KOSTEN.ordinal()] = this.getKosten().toString();
        csvData[CSVPosition.ZUSTAENDIGE_FIRMA_ID.ordinal()] = this.getZustaendigeFirma()
                .map(Fremdfirma::getPrimaryKey)
                .map(Objects::toString)
                .orElse("");
        csvData[CSVPosition.ANLAGE_ID.ordinal()] = this.getAnlage().getPrimaryKey().toString();
        csvData[CSVPosition.DUMMY_DATA.ordinal()] = "NULL";
        return csvData;
    }

    @Override
    public String[] getCSVHeader() {
        return new String[] {
                CSVPosition.WARTUNGSNUMMER.name(),
                CSVPosition.DUERCHFUEHRUNGSDATUM.name(),
                CSVPosition.RECHNUNGSDATUM.name(),
                CSVPosition.AUFTRAGSNUMMER.name(),
                CSVPosition.RECHNUNGSNUMMER.name(),
                CSVPosition.KOSTEN.name(),
                CSVPosition.ZUSTAENDIGE_FIRMA_ID.name(),
                CSVPosition.ANLAGE_ID.name(),
                CSVPosition.DUMMY_DATA.name()
        };
    }

    @Override
    public String getElementID() {
        return Integer.toString(this.getWartungsnummer());
    }

    @Override
    public Object getPrimaryKey() {
        return this.getWartungsnummer();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getWartungsnummer(),
                this.getDuerchfuehrungsdatum(),
                this.getRechnungsdatum(),
                this.getAuftragsnummer(),
                this.getRechnungsnummer(),
                this.getKosten(),
                this.getZustaendigeFirma(),
                this.getAnlage());
    }

    @Override
    public Attribute[] setAttributeValues(final Attribute[] attributeArray) {
        final var oldAttributeArray = this.getAttributeArray();

        for (final var attribute : attributeArray) {
            final var name = attribute.getName();
            final var value = attribute.getValue();

            if (name.equals(Attributes.WARTUNGSNUMMER.name()) && !value.equals(this.getWartungsnummer())) {
                throw new IllegalArgumentException("Wartung::setAttributeValues: Die Wartungsnummer darf nicht verändert werden!");
            }

            if (name.equals(Attributes.DUERCHFUEHRUNGSDATUM.name()) && !value.equals(this.getDuerchfuehrungsdatum())) {
                this.setDuerchfuehrungsdatum((LocalDate) value);
            } else if (name.equals(Attributes.RECHNUNGSDATUM.name()) && !value.equals(this.getRechnungsdatum())) {
                this.setRechnungsdatum((LocalDate) value);
            } else if (name.equals(Attributes.AUFTRAGSNUMMER.name()) && !value.equals(this.getAuftragsnummer())) {
                this.setAuftragsnummer((String) value);
            } else if (name.equals(Attributes.RECHNUNGSNUMMER.name()) && !value.equals(this.getRechnungsnummer())) {
                this.setRechnungsnummer((String) value);
            } else if (name.equals(Attributes.KOSTEN.name()) && !value.equals(this.getKosten())) {
                this.setKosten((BigDecimal) value);
            }
        }
        return oldAttributeArray;
    }

    @Override
    public String toString() {
        return "Wartung{" +
                "wartungsnummer=" + this.getWartungsnummer() +
                ", duerchfuehrungsdatum=" + this.getDuerchfuehrungsdatum() +
                ", rechnungsdatum=" + this.getRechnungsdatum() +
                ", auftragsnummer='" + this.getAuftragsnummer() + '\'' +
                ", rechnungsnummer='" + this.getRechnungsnummer() + '\'' +
                ", kosten=" + this.getKosten() +
                ", zustaendigeFirma=" + this.getZustaendigeFirma() +
                ", anlage=" + this.getAnlage() +
                '}';
    }
}
