package swe.ka.dhbw.model;

import de.dhbwka.swe.utils.model.Attribute;
import de.dhbwka.swe.utils.model.ICSVPersistable;
import de.dhbwka.swe.utils.model.IDepictable;
import de.dhbwka.swe.utils.model.IPersistable;
import swe.ka.dhbw.util.Validator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public final class Rechnung implements IPersistable, ICSVPersistable, IDepictable {
    public enum Attributes {
        RECHNUNGSNUMMER,
        RECHNUNGSDATUM,
        BETRAG_NETTO,
        ZAHLUNGSANWEISUNG,
        BANKVERBINDUNG,
        ZAHLUNGSZWECK,
        ZAHLUNGSZIEL
    }

    public enum CSVPosition {
        RECHNUNGSNUMMER,
        RECHNUNGSDATUM,
        BETRAG_NETTO,
        ZAHLUNGSANWEISUNG,
        BANKVERBINDUNG,
        ZAHLUNGSZWECK,
        ZAHLUNGSZIEL,
        ADRESSAT_ID,
        DUMMY_DATA
    }

    private final int rechnungsnummer;
    private LocalDate rechnungsdatum;
    private BigDecimal betragNetto;
    private String zahlungsanweisung;
    private String bankverbindung;
    private String zahlungszweck;
    private LocalDate zahlungsziel;
    private Gast adressat;

    public Rechnung(final int rechnungsnummer,
                    final LocalDate rechnungsdatum,
                    final BigDecimal betragNetto,
                    final String zahlungsanweisung,
                    final String bankverbindung,
                    final String zahlungszweck,
                    final LocalDate zahlungsziel) {
        Validator.getInstance().validateGreaterThanEqual(rechnungsnummer, 0);
        this.rechnungsnummer = rechnungsnummer;
        this.setRechnungsdatum(rechnungsdatum);
        this.setBetragNetto(betragNetto);
        this.setZahlungsanweisung(zahlungsanweisung);
        this.setBankverbindung(bankverbindung);
        this.setZahlungszweck(zahlungszweck);
        this.setZahlungsziel(zahlungsziel);
    }

    public int getRechnungsnummer() {
        return this.rechnungsnummer;
    }

    public LocalDate getRechnungsdatum() {
        return this.rechnungsdatum;
    }

    public void setRechnungsdatum(final LocalDate rechnungsdatum) {
        Validator.getInstance().validateNotNull(rechnungsdatum);
        this.rechnungsdatum = rechnungsdatum;
    }

    public BigDecimal getBetragNetto() {
        return this.betragNetto;
    }

    public void setBetragNetto(final BigDecimal betragNetto) {
        Validator.getInstance().validateNotNull(betragNetto);
        this.betragNetto = betragNetto;
    }

    public String getZahlungsanweisung() {
        return this.zahlungsanweisung;
    }

    public void setZahlungsanweisung(final String zahlungsanweisung) {
        Validator.getInstance().validateNotNull(zahlungsanweisung);
        this.zahlungsanweisung = zahlungsanweisung;
    }

    public String getBankverbindung() {
        return this.bankverbindung;
    }

    public void setBankverbindung(final String bankverbindung) {
        Validator.getInstance().validateNotEmpty(bankverbindung);
        this.bankverbindung = bankverbindung;
    }

    public String getZahlungszweck() {
        return this.zahlungszweck;
    }

    public void setZahlungszweck(final String zahlungszweck) {
        Validator.getInstance().validateNotNull(zahlungszweck);
        this.zahlungszweck = zahlungszweck;
    }

    public LocalDate getZahlungsziel() {
        return this.zahlungsziel;
    }

    public void setZahlungsziel(final LocalDate zahlungsziel) {
        Validator.getInstance().validateNotNull(zahlungsziel);
        this.zahlungsziel = zahlungsziel;
    }

    public Gast getAdressat() {
        return this.adressat;
    }

    public void setAdressat(final Gast adressat) {
        Validator.getInstance().validateNotNull(adressat);
        this.adressat = adressat;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof final Rechnung that)) return false;
        return this.getRechnungsnummer() == that.getRechnungsnummer();
    }

    @Override
    public Attribute[] getAttributeArray() {
        return new Attribute[] {
                new Attribute(Attributes.RECHNUNGSNUMMER.name(),
                        this,
                        Integer.class,
                        this.getRechnungsnummer(),
                        this.getRechnungsnummer(),
                        true,
                        false,
                        false,
                        true),
                new Attribute(Attributes.RECHNUNGSDATUM.name(),
                        this,
                        LocalDate.class,
                        this.getRechnungsdatum(),
                        this.getRechnungsdatum(),
                        true),
                new Attribute(Attributes.BETRAG_NETTO.name(),
                        this,
                        BigDecimal.class,
                        this.getBetragNetto(),
                        this.getBetragNetto(),
                        true),
                new Attribute(Attributes.ZAHLUNGSANWEISUNG.name(),
                        this,
                        String.class,
                        this.getZahlungsanweisung(),
                        this.getZahlungsanweisung(),
                        true),
                new Attribute(Attributes.BANKVERBINDUNG.name(),
                        this,
                        String.class,
                        this.getBankverbindung(),
                        this.getBankverbindung(),
                        true),
                new Attribute(Attributes.ZAHLUNGSZWECK.name(),
                        this,
                        String.class,
                        this.getZahlungszweck(),
                        this.getZahlungszweck(),
                        true),
                new Attribute(Attributes.ZAHLUNGSZIEL.name(),
                        this,
                        LocalDate.class,
                        this.getZahlungsziel(),
                        this.getZahlungsziel(),
                        true),
        };
    }

    @Override
    public String[] getCSVData() {
        final var csvData = new String[CSVPosition.values().length];
        csvData[CSVPosition.RECHNUNGSNUMMER.ordinal()] = String.valueOf(this.getRechnungsnummer());
        csvData[CSVPosition.RECHNUNGSDATUM.ordinal()] = this.getRechnungsdatum().toString();
        csvData[CSVPosition.BETRAG_NETTO.ordinal()] = this.getBetragNetto().toString();
        csvData[CSVPosition.ZAHLUNGSANWEISUNG.ordinal()] = this.getZahlungsanweisung();
        csvData[CSVPosition.BANKVERBINDUNG.ordinal()] = this.getBankverbindung();
        csvData[CSVPosition.ZAHLUNGSZWECK.ordinal()] = this.getZahlungszweck();
        csvData[CSVPosition.ZAHLUNGSZIEL.ordinal()] = this.getZahlungsziel().toString();
        csvData[CSVPosition.ADRESSAT_ID.ordinal()] = this.getAdressat().getPrimaryKey().toString();
        csvData[CSVPosition.DUMMY_DATA.ordinal()] = "NULL";
        return csvData;
    }

    @Override
    public String[] getCSVHeader() {
        return new String[] {
                CSVPosition.RECHNUNGSNUMMER.name(),
                CSVPosition.RECHNUNGSDATUM.name(),
                CSVPosition.BETRAG_NETTO.name(),
                CSVPosition.ZAHLUNGSANWEISUNG.name(),
                CSVPosition.BANKVERBINDUNG.name(),
                CSVPosition.ZAHLUNGSZWECK.name(),
                CSVPosition.ZAHLUNGSZIEL.name(),
                CSVPosition.ADRESSAT_ID.name(),
                CSVPosition.DUMMY_DATA.name()
        };
    }

    @Override
    public String getElementID() {
        return Integer.toString(this.getRechnungsnummer());
    }

    @Override
    public Object getPrimaryKey() {
        return this.getRechnungsnummer();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getRechnungsnummer());
    }

    @Override
    public Attribute[] setAttributeValues(Attribute[] attributeArray) {
        final var oldAttributeArray = this.getAttributeArray();

        for (final var attribute : attributeArray) {
            final var name = attribute.getName();
            final var value = attribute.getValue();
            if (name.equals(Attributes.RECHNUNGSNUMMER.name()) && !value.equals(this.getRechnungsnummer())) {
                throw new IllegalArgumentException(
                        "Rechnung::setAttributeValues: Die Rechnungsnummer darf nicht verändert werden!");
            }

            if (name.equals(Attributes.RECHNUNGSDATUM.name()) && !value.equals(this.getRechnungsdatum())) {
                this.setRechnungsdatum((LocalDate) value);
            } else if (name.equals(Attributes.BETRAG_NETTO.name()) && !value.equals(this.getBetragNetto())) {
                this.setBetragNetto((BigDecimal) value);
            } else if (name.equals(Attributes.ZAHLUNGSANWEISUNG.name()) && !value.equals(this.getZahlungsanweisung())) {
                this.setZahlungsanweisung((String) value);
            } else if (name.equals(Attributes.BANKVERBINDUNG.name()) && !value.equals(this.getBankverbindung())) {
                this.setBankverbindung((String) value);
            } else if (name.equals(Attributes.ZAHLUNGSZWECK.name()) && !value.equals(this.getZahlungszweck())) {
                this.setZahlungszweck((String) value);
            } else if (name.equals(Attributes.ZAHLUNGSZIEL.name()) && !value.equals(this.getZahlungsziel())) {
                this.setZahlungsziel((LocalDate) value);
            }
        }
        return oldAttributeArray;
    }

    @Override
    public String toString() {
        return "Rechnung{" +
                "rechnungsnummer=" + this.getRechnungsnummer() +
                ", rechnungsdatum=" + this.getRechnungsdatum() +
                ", betragNetto=" + this.getBetragNetto() +
                ", zahlungsanweisung='" + this.getZahlungsanweisung() + '\'' +
                ", bankverbindung='" + this.getBankverbindung() + '\'' +
                ", zahlungszweck='" + this.getZahlungszweck() + '\'' +
                ", zahlungsziel=" + this.getZahlungsziel() +
                ", adressat=" + this.getAdressat() +
                '}';
    }
}
