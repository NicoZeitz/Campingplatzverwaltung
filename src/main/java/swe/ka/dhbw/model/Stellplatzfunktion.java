package swe.ka.dhbw.model;

import java.util.ArrayList;

public class Stellplatzfunktion extends Leistungsbeschreibung{

    private Status status;
    private ArrayList<Stellplatz> stellplaetze;

    public enum Status{
        AKTIV, INAKTIV, GESTOERT
    }

}
