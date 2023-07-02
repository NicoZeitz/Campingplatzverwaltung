package swe.ka.dhbw.model;

public class Fahrzeug extends Ausruestung{

    private String kennzeichen;
    private Typ typ;

    public enum Typ{
        KFZ,
        WOHNMOBIL,
        WOHNWAGEN

    }

}
