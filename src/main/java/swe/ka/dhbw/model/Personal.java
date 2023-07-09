package swe.ka.dhbw.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Personal extends Person {

    public enum Rolle {
        ADMINISTRATOR,
        PLATZWART,
        HAUSMEISTER,
        EMPFANG
    }

    private final List<Stoerung> stoerungen = new ArrayList<>();
    private int nummer;
    private LocalDate geburtstag;
    private Rolle benutzerrolle;

    public Personal(String vorname,
                    String nachname,
                    Geschlecht geschlecht,
                    String email,
                    String telefonnummer,
                    int nummer,
                    LocalDate geburtstag,
                    Rolle benutzerrolle) {
        super(vorname, nachname, geschlecht, email, telefonnummer);
        this.nummer = nummer;
        this.geburtstag = geburtstag;
        this.benutzerrolle = benutzerrolle;
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
