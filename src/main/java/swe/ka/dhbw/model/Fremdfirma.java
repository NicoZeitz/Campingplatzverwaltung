package swe.ka.dhbw.model;

import java.util.List;

public class Fremdfirma {

    private String name;
    private Adresse anschrift;
    private Person ansprechperson;
    private List<Wartung> wartungen;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Adresse getAnschrift() {
        return anschrift;
    }

    public void setAnschrift(Adresse anschrift) {
        this.anschrift = anschrift;
    }

    public Person getAnsprechperson() {
        return ansprechperson;
    }

    public void setAnsprechperson(Person ansprechperson) {
        this.ansprechperson = ansprechperson;
    }

    public List<Wartung> getWartungen() {
        return wartungen;
    }

}
