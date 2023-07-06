package swe.ka.dhbw.model;

import java.math.BigDecimal;
import java.util.List;

public class Leistungsbeschreibung {

    private BigDecimal gebuehr;
    private int maximalAnzahl;
    private String beschreibung;
    private List<GebuchteLeistung> gebuchteLeistungen;

    public BigDecimal getGebuehr() {
        return gebuehr;
    }

    public void setGebuehr(BigDecimal gebuehr) {
        this.gebuehr = gebuehr;
    }

    public int getMaximalAnzahl() {
        return maximalAnzahl;
    }

    public void setMaximalAnzahl(int maximalAnzahl) {
        this.maximalAnzahl = maximalAnzahl;
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }

    public List<GebuchteLeistung> getGebuchteLeistungen() {
        return gebuchteLeistungen;
    }

    public void setGebuchteLeistungen(List<GebuchteLeistung> gebuchteLeistungen) {
        this.gebuchteLeistungen = gebuchteLeistungen;
    }
}
