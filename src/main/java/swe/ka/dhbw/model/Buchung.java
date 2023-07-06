package swe.ka.dhbw.model;

import java.time.LocalDateTime;
import java.util.List;

public class Buchung {

    private int buchungsnummer;
    private LocalDateTime anreise;
    private LocalDateTime abreise;
    private Stellplatz gebuchterStellplatz;
    private List<Chipkarte> ausgehaendigteChipkarten;
    private List<Ausruestung> mitgebrachteAusruestung;
    private Rechnung rechnung;
    private Gast verantwortlicherGast;
    private List<Gast> zugehoerigeGaeste;
    private List<GebuchteLeistung> gebuchteLeistungen;

    public int getBuchungsnummer() {
        return buchungsnummer;
    }

    public void setBuchungsnummer(int buchungsnummer) {
        this.buchungsnummer = buchungsnummer;
    }

    public LocalDateTime getAnreise() {
        return anreise;
    }

    public void setAnreise(LocalDateTime anreise) {
        this.anreise = anreise;
    }

    public LocalDateTime getAbreise() {
        return abreise;
    }

    public void setAbreise(LocalDateTime abreise) {
        this.abreise = abreise;
    }

    public Stellplatz getGebuchterStellplatz() {
        return gebuchterStellplatz;
    }

    public void setGebuchterStellplatz(Stellplatz gebuchterStellplatz) {
        this.gebuchterStellplatz = gebuchterStellplatz;
    }

    public List<Chipkarte> getAusgehaendigteChipkarten() {
        return ausgehaendigteChipkarten;
    }

    public void setAusgehaendigteChipkarten(List<Chipkarte> ausgehaendigteChipkarten) {
        this.ausgehaendigteChipkarten = ausgehaendigteChipkarten;
    }

    public List<Ausruestung> getMitgebrachteAusruestung() {
        return mitgebrachteAusruestung;
    }

    public void setMitgebrachteAusruestung(List<Ausruestung> mitgebrachteAusruestung) {
        this.mitgebrachteAusruestung = mitgebrachteAusruestung;
    }

    public Rechnung getRechnung() {
        return rechnung;
    }

    public void setRechnung(Rechnung rechnung) {
        this.rechnung = rechnung;
    }

    public Gast getVerantwortlicherGast() {
        return verantwortlicherGast;
    }

    public void setVerantwortlicherGast(Gast verantwortlicherGast) {
        this.verantwortlicherGast = verantwortlicherGast;
    }

    public List<Gast> getZugehoerigeGaeste() {
        return zugehoerigeGaeste;
    }

    public void setZugehoerigeGaeste(List<Gast> zugehoerigeGaeste) {
        this.zugehoerigeGaeste = zugehoerigeGaeste;
    }

    public List<GebuchteLeistung> getGebuchteLeistungen() {
        return gebuchteLeistungen;
    }

    public void setGebuchteLeistungen(List<GebuchteLeistung> gebuchteLeistungen) {
        this.gebuchteLeistungen = gebuchteLeistungen;
    }

}
