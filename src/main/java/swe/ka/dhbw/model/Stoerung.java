package swe.ka.dhbw.model;

import java.time.LocalDate;
import java.util.List;

public class Stoerung {

    private int stoerungsnummer;
    private String titel;
    private String beschreibung;
    private LocalDate erstellungsdatum;
    private LocalDate behebungsdatum;
    private Status status;
    private List<Foto> fotos;
    private Personal verantwortlicher;
    private Stellplatzfunktion stellplatzfunktion;

    public int getStoerungsnummer() {
        return stoerungsnummer;
    }

    public void setStoerungsnummer(int stoerungsnummer) {
        this.stoerungsnummer = stoerungsnummer;
    }

    public String getTitel() {
        return titel;
    }

    public void setTitel(String titel) {
        this.titel = titel;
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }

    public LocalDate getErstellungsdatum() {
        return erstellungsdatum;
    }

    public void setErstellungsdatum(LocalDate erstellungsdatum) {
        this.erstellungsdatum = erstellungsdatum;
    }

    public LocalDate getBehebungsdatum() {
        return behebungsdatum;
    }

    public void setBehebungsdatum(LocalDate behebungsdatum) {
        this.behebungsdatum = behebungsdatum;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<Foto> getFotos() {
        return fotos;
    }

    public void setFotos(List<Foto> fotos) {
        this.fotos = fotos;
    }

    public Personal getVerantwortlicher() {
        return verantwortlicher;
    }

    public void setVerantwortlicher(Personal verantwortlicher) {
        this.verantwortlicher = verantwortlicher;
    }

    public Stellplatzfunktion getStellplatzfunktion() {
        return stellplatzfunktion;
    }

    public void setStellplatzfunktion(Stellplatzfunktion stellplatzfunktion) {
        this.stellplatzfunktion = stellplatzfunktion;
    }

    public enum Status {
        AKTIV, BEHOBEN, IN_ARBEIT, VERSCHOBEN
    }
}
