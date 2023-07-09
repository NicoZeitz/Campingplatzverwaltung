package swe.ka.dhbw.model;

import de.dhbwka.swe.utils.model.Attribute;
import de.dhbwka.swe.utils.model.ICSVPersistable;
import de.dhbwka.swe.utils.model.IDepictable;
import de.dhbwka.swe.utils.model.IPersistable;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Rechnung implements IPersistable, ICSVPersistable, IDepictable {

    private LocalDate rechnungsdatum;
    private int rechnungsnummer;
    private BigDecimal betragNetto;
    private String zahlungsanweisung;
    private String bankverbindung;
    private String zahlungszweck;
    private LocalDate zahlungsziel;
    private Gast adressat;

    public LocalDate getRechnungsdatum() {
        return rechnungsdatum;
    }

    public void setRechnungsdatum(LocalDate rechnungsdatum) {
        this.rechnungsdatum = rechnungsdatum;
    }

    public int getRechnungsnummer() {
        return rechnungsnummer;
    }

    public void setRechnungsnummer(int rechnungsnummer) {
        this.rechnungsnummer = rechnungsnummer;
    }

    public BigDecimal getBetragNetto() {
        return betragNetto;
    }

    public void setBetragNetto(BigDecimal betragNetto) {
        this.betragNetto = betragNetto;
    }

    public String getZahlungsanweisung() {
        return zahlungsanweisung;
    }

    public void setZahlungsanweisung(String zahlungsanweisung) {
        this.zahlungsanweisung = zahlungsanweisung;
    }

    public String getBankverbindung() {
        return bankverbindung;
    }

    public void setBankverbindung(String bankverbindung) {
        this.bankverbindung = bankverbindung;
    }

    public String getZahlungszweck() {
        return zahlungszweck;
    }

    public void setZahlungszweck(String zahlungszweck) {
        this.zahlungszweck = zahlungszweck;
    }

    public LocalDate getZahlungsziel() {
        return zahlungsziel;
    }

    public void setZahlungsziel(LocalDate zahlungsziel) {
        this.zahlungsziel = zahlungsziel;
    }

    public Gast getAdressat() {
        return adressat;
    }

    public void setAdressat(Gast adressat) {
        this.adressat = adressat;
    }

    @Override
    public Attribute[] getAttributeArray() {
        return new Attribute[0];
    }

    @Override
    public Attribute[] setAttributeValues(Attribute[] attributeArray) {
        return IDepictable.super.setAttributeValues(attributeArray);
    }

    @Override
    public String[] getCSVHeader() {
        return new String[0];
    }

    @Override
    public String[] getCSVData() {
        return new String[0];
    }

    @Override
    public String getElementID() {
        return null;
    }

    @Override
    public Object getPrimaryKey() {
        return null;
    }
}
