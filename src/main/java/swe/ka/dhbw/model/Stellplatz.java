package swe.ka.dhbw.model;

import java.math.BigDecimal;
import java.util.ArrayList;

public class Stellplatz extends Anlage{

    private String stellplatz;
    private BigDecimal gebuehr;
    private double groesse;
    private boolean barrierefrei;
    private int anzahWohnwagen;
    private int anzahlPKW;
    private  int anzahlZelte;
    private ArrayList<Stellplatzfunktion> verfuegbareFunktionen;
    private ArrayList<Buchung> buchungen;

}
