package swe.ka.dhbw.model;

import java.util.ArrayList;

public class Bereich extends Anlage {

    private char kennzeichen;
    private String beschreibung;
    private ArrayList<Anlage> anlagen;

    public char getKennzeichen() {
        return kennzeichen;
    }

    public void setKennzeichen(char kennzeichen) {
        this.kennzeichen = kennzeichen;
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }

    public ArrayList<Anlage> getAnlagen() {
        return anlagen;
    }

    public void setAnlagen(ArrayList<Anlage> anlagen) {
        this.anlagen = anlagen;
    }
}
