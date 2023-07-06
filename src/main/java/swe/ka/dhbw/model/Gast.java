package swe.ka.dhbw.model;

import java.util.List;

public class Gast extends Person {

    private int kundennummer;
    private String ausweisnummer;
    private List<Buchung> buchungenVerantwortlicherGast;
    private List<Buchung> buchungenZugehoerigerGast;

    public int getKundennummer() {
        return kundennummer;
    }

    public void setKundennummer(int kundennummer) {
        this.kundennummer = kundennummer;
    }

    public String getAusweisnummer() {
        return ausweisnummer;
    }

    public void setAusweisnummer(String ausweisnummer) {
        this.ausweisnummer = ausweisnummer;
    }

    public List<Buchung> getBuchungenVerantwortlicherGast() {
        return buchungenVerantwortlicherGast;
    }

    public void setBuchungenVerantwortlicherGast(List<Buchung> buchungenVerantwortlicherGast) {
        this.buchungenVerantwortlicherGast = buchungenVerantwortlicherGast;
    }

    public List<Buchung> getBuchungenZugehoerigerGast() {
        return buchungenZugehoerigerGast;
    }

    public void setBuchungenZugehoerigerGast(List<Buchung> buchungenZugehoerigerGast) {
        this.buchungenZugehoerigerGast = buchungenZugehoerigerGast;
    }
}
