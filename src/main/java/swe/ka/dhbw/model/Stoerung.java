package swe.ka.dhbw.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class Stoerung {

    private int stoerungsnummer;
    private String titel;
    private String beschreibung;
    private LocalDate erstellungsdatum;
    private LocalDate behebungsdatum;
    private Status status;
    private ArrayList<Foto> fotos;
    private Personal verantwortlicher;
    private Stellplatzfunktion stellplatzfunktion;

    public enum Status{
        AKTIV, BEHOBEN, IN_ARBEIT, VERSCHOBEN
    }

}
