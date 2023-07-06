package swe.ka.dhbw.model;

public class Fahrzeug extends Ausruestung {

    private String kennzeichen;
    private Typ typ;

    public String getKennzeichen() {
        return kennzeichen;
    }

    public void setKennzeichen(String kennzeichen) {
        this.kennzeichen = kennzeichen;
    }

    public Typ getTyp() {
        return typ;
    }

    public void setTyp(Typ typ) {
        this.typ = typ;
    }

    public enum Typ {
        KFZ, WOHNMOBIL, WOHNWAGEN

    }
}
