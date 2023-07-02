package swe.ka.dhbw.model;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Buchung {

    private int buchungsnummer;
    private LocalDateTime anreise;
    private LocalDateTime abreise;
    private Stellplatz gebuchterStellplatz;
    private ArrayList<Chipkarte> ausgehaendigteChipkarten;
    private ArrayList<Ausruestung> mitgebrachteAusruestung;
    private Rechnung rechnung;
    private Gast verantwortlicherGast;
    private ArrayList<Gast> zugehoerigeGaeste;
    private ArrayList<GebuchteLeistung> gebuchteLeistungen;

}
