package swe.ka.dhbw.model;

import java.util.List;

public class Oeffnungstag {

    //Einrichtung - Öffnungstag (Machen da die Kardinalitäten aus EKD Sinn?)
    private Wochentag wochentag;
    private List<Oeffnungszeit> oeffnungszeiten;
    private List<Einrichtung> einrichtungen;

    public Wochentag getWochentag() {
        return wochentag;
    }

    public void setWochentag(Wochentag wochentag) {
        this.wochentag = wochentag;
    }

    public List<Oeffnungszeit> getOeffnungszeiten() {
        return oeffnungszeiten;
    }

    public void setOeffnungszeiten(List<Oeffnungszeit> oeffnungszeiten) {
        this.oeffnungszeiten = oeffnungszeiten;
    }

    public List<Einrichtung> getEinrichtungen() {
        return einrichtungen;
    }

    public void setEinrichtungen(List<Einrichtung> einrichtungen) {
        this.einrichtungen = einrichtungen;
    }

    public enum Wochentag {
        MO, DI, MI, DO, FR, SA, SO
    }
}
