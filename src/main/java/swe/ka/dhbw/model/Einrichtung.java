package swe.ka.dhbw.model;

import java.time.LocalDateTime;

public class Einrichtung extends Anlage {

    private String name;
    private String beschreibung;
    private LocalDateTime letzteWartung;
    private Oeffnungstag oeffnungstag;
    private Fremdfirma zustaendigeFirma;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }

    public LocalDateTime getLetzteWartung() {
        return letzteWartung;
    }

    public void setLetzteWartung(LocalDateTime letzteWartung) {
        this.letzteWartung = letzteWartung;
    }

    public Oeffnungstag getOeffnungstag() {
        return oeffnungstag;
    }

    public void setOeffnungstag(Oeffnungstag oeffnungstag) {
        this.oeffnungstag = oeffnungstag;
    }

    public Fremdfirma getZustaendigeFirma() {
        return zustaendigeFirma;
    }

    public void setZustaendigeFirma(Fremdfirma zustaendigeFirma) {
        this.zustaendigeFirma = zustaendigeFirma;
    }

}
