package swe.ka.dhbw.model;

import java.util.List;

public class Anlage {

    private GPSPosition lage;
    private Bereich bereich;
    private List<Foto> fotos;

    public GPSPosition getLage() {
        return lage;
    }

    public void setLage(GPSPosition lage) {
        this.lage = lage;
    }

    public Bereich getBereich() {
        return bereich;
    }

    public void setBereich(Bereich bereich) {
        this.bereich = bereich;
    }

    public List<Foto> getFotos() {
        return fotos;
    }

    public void setFotos(List<Foto> fotos) {
        this.fotos = fotos;
    }

}
