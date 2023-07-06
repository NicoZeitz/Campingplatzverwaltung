package swe.ka.dhbw.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Wartung {

    private int wartungsnummer;
    private LocalDate duerchfuehrungsdatum;
    private LocalDate rechnungsdatum;
    private String auftragsnummer;
    private String rechnungsnummer;
    private BigDecimal kosten;
    private Fremdfirma zustaendigeFirma;
    private Anlage anlage;

    public int getWartungsnummer() {
        return wartungsnummer;
    }

    public void setWartungsnummer(int wartungsnummer) {
        this.wartungsnummer = wartungsnummer;
    }

    public LocalDate getDuerchfuehrungsdatum() {
        return duerchfuehrungsdatum;
    }

    public void setDuerchfuehrungsdatum(LocalDate duerchfuehrungsdatum) {
        this.duerchfuehrungsdatum = duerchfuehrungsdatum;
    }

    public LocalDate getRechnungsdatum() {
        return rechnungsdatum;
    }

    public void setRechnungsdatum(LocalDate rechnungsdatum) {
        this.rechnungsdatum = rechnungsdatum;
    }

    public String getAuftragsnummer() {
        return auftragsnummer;
    }

    public void setAuftragsnummer(String auftragsnummer) {
        this.auftragsnummer = auftragsnummer;
    }

    public String getRechnungsnummer() {
        return rechnungsnummer;
    }

    public void setRechnungsnummer(String rechnungsnummer) {
        this.rechnungsnummer = rechnungsnummer;
    }

    public BigDecimal getKosten() {
        return kosten;
    }

    public void setKosten(BigDecimal kosten) {
        this.kosten = kosten;
    }

    public Fremdfirma getZustaendigeFirma() {
        return zustaendigeFirma;
    }

    public void setZustaendigeFirma(Fremdfirma zustaendigeFirma) {
        this.zustaendigeFirma = zustaendigeFirma;
    }

    public Anlage getAnlage() {
        return anlage;
    }

    public void setAnlage(Anlage anlage) {
        this.anlage = anlage;
    }
}
