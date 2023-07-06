package swe.ka.dhbw.model;

import java.time.LocalDate;
import java.util.List;

public class Personal extends Person{

    private int nummer;
    private LocalDate geburtstag;
    private Rolle benutzerrolle;
    private List<Stoerung> stoerungen;

    public enum Rolle{
        ADMINISTRATOR,
        PLATZWART,
        HAUSMEISTER,
        EMPFANG
    }

    public int getNummer() {
        return nummer;
    }

    public void setNummer(int nummer) {
        this.nummer = nummer;
    }

    public LocalDate getGeburtstag() {
        return geburtstag;
    }

    public void setGeburtstag(LocalDate geburtstag) {
        this.geburtstag = geburtstag;
    }

    public Rolle getBenutzerrolle() {
        return benutzerrolle;
    }

    public void setBenutzerrolle(Rolle benutzerrolle) {
        this.benutzerrolle = benutzerrolle;
    }

    public List<Stoerung> getStoerungen() {
        return stoerungen;
    }

    public void setStoerungen(List<Stoerung> stoerungen) {
        this.stoerungen = stoerungen;
    }
}
