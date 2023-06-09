package swe.ka.dhbw.model;

import swe.ka.dhbw.util.Validator;

public class Person {
    public enum Geschlecht {
        MAENNLICH,
        WEIBLICH,
        DIVERS,
    }

    private String vorname;
    private String nachname;
    private Geschlecht geschlecht;
    private String email;
    private String telefonnummer;

    public Person(final String vorname, final String nachname, final Geschlecht geschlecht, final String email, final String telefonnummer) {
        this.setVorname(vorname);
        this.setNachname(nachname);
        this.setGeschlecht(geschlecht);
        this.setEmail(email);
        this.setTelefonnummer(telefonnummer);
    }

    public String getVorname() {
        return vorname;
    }

    public void setVorname(String vorname) {
        Validator.getInstance().validateNotNull(vorname, "Person.vorname");
        this.vorname = vorname;
    }

    public String getNachname() {
        return nachname;
    }

    public void setNachname(String nachname) {
        Validator.getInstance().validateNotNull(vorname, "Person.nachname");
        this.nachname = nachname;
    }

    public Geschlecht getGeschlecht() {
        return geschlecht;
    }

    public void setGeschlecht(Geschlecht geschlecht) {
        Validator.getInstance().validateNotNull(vorname, "Person.geschlecht");
        this.geschlecht = geschlecht;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        Validator.getInstance().validateEmail(email);
        this.email = email;
    }

    public String getTelefonnummer() {
        return telefonnummer;
    }

    public void setTelefonnummer(String telefonnummer) {
        Validator.getInstance().validatePhoneNumber(telefonnummer);
        this.telefonnummer = telefonnummer;
    }
}
