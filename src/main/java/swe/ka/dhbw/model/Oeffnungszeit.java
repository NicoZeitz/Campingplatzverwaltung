package swe.ka.dhbw.model;

import java.time.LocalTime;

public class Oeffnungszeit {

    private LocalTime start;
    private LocalTime ende;
    private Oeffnungstag oeffnungstag;

    public LocalTime getStart() {
        return start;
    }

    public void setStart(LocalTime start) {
        this.start = start;
    }

    public LocalTime getEnde() {
        return ende;
    }

    public void setEnde(LocalTime ende) {
        this.ende = ende;
    }

    public Oeffnungstag getOeffnungstag() {
        return oeffnungstag;
    }

    public void setOeffnungstag(Oeffnungstag oeffnungstag) {
        this.oeffnungstag = oeffnungstag;
    }
}
