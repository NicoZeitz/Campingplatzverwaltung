package swe.ka.dhbw.model;

import java.util.ArrayList;

public class Gast extends Person{

    private int kundennummer;
    private String ausweisnummer;
    private ArrayList<Buchung> buchungenVerantwortlicherGast;
    private ArrayList<Buchung> buchungenZugehoerigerGast;

}
