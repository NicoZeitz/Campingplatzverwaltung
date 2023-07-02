package swe.ka.dhbw.model;

import java.util.ArrayList;

public class Oeffnungstag {

    //Einrichtung - Öffnungstag (Machen da die Kardinalitäten aus EKD Sinn?)
    private Wochentag wochentag;
    private ArrayList<Oeffnungszeit> oeffnungszeiten;
    private ArrayList<Einrichtung> einrichtungen;

    public enum Wochentag{
        MO, DI, MI, DO, FR, SA, SO
    }

}
