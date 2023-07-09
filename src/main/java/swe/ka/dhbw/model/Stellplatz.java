package swe.ka.dhbw.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Stellplatz extends Anlage {

    private final List<Stellplatzfunktion> verfuegbareFunktionen = new ArrayList<>();
    private String stellplatz;
    private BigDecimal gebuehr;
    private double groesse;
    private boolean barrierefrei;
    private int anzahWohnwagen;
    private int anzahlPKW;
    private int anzahlZelte;

    public Stellplatz(int anlageId, GPSPosition lage) {
        super(anlageId, lage);
    }

    public String getStellplatz() {
        return stellplatz;
    }

    public void setStellplatz(String stellplatz) {
        this.stellplatz = stellplatz;
    }

    public BigDecimal getGebuehr() {
        return gebuehr;
    }

    public void setGebuehr(BigDecimal gebuehr) {
        this.gebuehr = gebuehr;
    }

    public double getGroesse() {
        return groesse;
    }

    public void setGroesse(double groesse) {
        this.groesse = groesse;
    }

    public boolean isBarrierefrei() {
        return barrierefrei;
    }

    public void setBarrierefrei(boolean barrierefrei) {
        this.barrierefrei = barrierefrei;
    }

    public int getAnzahWohnwagen() {
        return anzahWohnwagen;
    }

    public void setAnzahWohnwagen(int anzahWohnwagen) {
        this.anzahWohnwagen = anzahWohnwagen;
    }

    public int getAnzahlPKW() {
        return anzahlPKW;
    }

    public void setAnzahlPKW(int anzahlPKW) {
        this.anzahlPKW = anzahlPKW;
    }

    public int getAnzahlZelte() {
        return anzahlZelte;
    }

    public void setAnzahlZelte(int anzahlZelte) {
        this.anzahlZelte = anzahlZelte;
    }

    public List<Stellplatzfunktion> getVerfuegbareFunktionen() {
        return verfuegbareFunktionen;
    }
    
    @Override
    public String[] getCSVData() {
        return new String[0];
    }

    @Override
    public String[] getCSVHeader() {
        return new String[0];
    }

    @Override
    public String getElementID() {
        return null;
    }

    @Override
    public Object getPrimaryKey() {
        return null;
    }
}
