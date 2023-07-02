package swe.ka.dhbw.model;

import java.time.LocalDate;
import java.util.ArrayList;

public class Personal extends Person{

    private int nummer;
    private LocalDate geburtstag;
    private Rolle benutzerrolle;
    private ArrayList<Stoerung> stoerungen;

    public enum Rolle{
        ADMINISTRATOR,
        PLATZWART,
        HAUSMEISTER,
        EMPFANG
    }

}
