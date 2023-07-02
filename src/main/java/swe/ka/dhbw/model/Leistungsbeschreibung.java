package swe.ka.dhbw.model;

import java.math.BigDecimal;
import java.util.ArrayList;

public class Leistungsbeschreibung {

    private BigDecimal gebuehr;
    private int maximalAnzahl;
    private String beschreibung;
    private ArrayList<GebuchteLeistung> gebuchteLeistungen;
}
