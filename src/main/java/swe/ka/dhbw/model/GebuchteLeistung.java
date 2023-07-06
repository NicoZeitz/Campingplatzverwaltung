package swe.ka.dhbw.model;

import java.time.LocalDate;

public class GebuchteLeistung {

    private LocalDate buchungStart;
    private LocalDate buchungsEnde;
    private Leistungsbeschreibung leistungsbeschreibung;
    private Buchung buchung;

    public LocalDate getBuchungStart() {
        return buchungStart;
    }

    public void setBuchungStart(LocalDate buchungStart) {
        this.buchungStart = buchungStart;
    }

    public LocalDate getBuchungsEnde() {
        return buchungsEnde;
    }

    public void setBuchungsEnde(LocalDate buchungsEnde) {
        this.buchungsEnde = buchungsEnde;
    }

    public Leistungsbeschreibung getLeistungsbeschreibung() {
        return leistungsbeschreibung;
    }

    public void setLeistungsbeschreibung(Leistungsbeschreibung leistungsbeschreibung) {
        this.leistungsbeschreibung = leistungsbeschreibung;
    }

    public Buchung getBuchung() {
        return buchung;
    }

    public void setBuchung(Buchung buchung) {
        this.buchung = buchung;
    }
}
