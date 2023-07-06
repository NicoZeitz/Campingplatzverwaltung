package swe.ka.dhbw.model;

import java.time.LocalDate;

public class Geraetschaft extends Leistungsbeschreibung {

    private LocalDate anschaffungsdatum;
    private String zustand;

    public LocalDate getAnschaffungsdatum() {
        return anschaffungsdatum;
    }

    public void setAnschaffungsdatum(LocalDate anschaffungsdatum) {
        this.anschaffungsdatum = anschaffungsdatum;
    }

    public String getZustand() {
        return zustand;
    }

    public void setZustand(String zustand) {
        this.zustand = zustand;
    }
}
