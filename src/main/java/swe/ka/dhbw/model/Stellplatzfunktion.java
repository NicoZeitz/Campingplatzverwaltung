package swe.ka.dhbw.model;

import java.util.List;

public class Stellplatzfunktion extends Leistungsbeschreibung {

    private Status status;
    private List<Stellplatz> stellplaetze;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<Stellplatz> getStellplaetze() {
        return stellplaetze;
    }

    public void setStellplaetze(List<Stellplatz> stellplaetze) {
        this.stellplaetze = stellplaetze;
    }

    public enum Status {
        AKTIV, INAKTIV, GESTOERT
    }
}
