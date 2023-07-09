package swe.ka.dhbw.model;

import de.dhbwka.swe.utils.model.Attribute;
import de.dhbwka.swe.utils.model.ICSVPersistable;
import de.dhbwka.swe.utils.model.IDepictable;
import de.dhbwka.swe.utils.model.IPersistable;
import swe.ka.dhbw.util.Validator;

public class Person implements IDepictable, ICSVPersistable, IPersistable {
    protected String vorname;
    protected String nachname;
    protected Geschlecht geschlecht;
    protected String email;
    protected String telefonnummer;

    public Person(
            final String vorname,
            final String nachname,
            final Geschlecht geschlecht,
            final String email,
            final String telefonnummer
    ) {
        this.setVorname(vorname);
        this.setNachname(nachname);
        this.setGeschlecht(geschlecht);
        this.setEmail(email);
        this.setTelefonnummer(telefonnummer);
    }

    public String getVorname() {
        return vorname;
    }

    public void setVorname(final String vorname) {
        Validator.getInstance().validateNotEmpty(vorname);
        this.vorname = vorname;
    }

    public String getNachname() {
        return nachname;
    }

    public void setNachname(final String nachname) {
        Validator.getInstance().validateNotEmpty(vorname);
        this.nachname = nachname;
    }

    public Geschlecht getGeschlecht() {
        return geschlecht;
    }

    public void setGeschlecht(final Geschlecht geschlecht) {
        Validator.getInstance().validateNotEmpty(vorname);
        this.geschlecht = geschlecht;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        Validator.getInstance().validateEmail(email);
        this.email = email;
    }

    public String getTelefonnummer() {
        return telefonnummer;
    }

    public void setTelefonnummer(final String telefonnummer) {
        Validator.getInstance().validatePhoneNumber(telefonnummer);
        this.telefonnummer = telefonnummer;
    }

    @Override
    public String getElementID() {
        return null;
    }

    @Override
    public Attribute[] getAttributeArray() {
        return new Attribute[0];
    }

    @Override
    public Attribute[] setAttributeValues(Attribute[] attributeArray) {
        return IDepictable.super.setAttributeValues(attributeArray);
    }

    @Override
    public String[] getCSVHeader() {
        return new String[0];
    }

    @Override
    public String[] getCSVData() {
        return new String[0];
    }

    @Override
    public Object getPrimaryKey() {
        return null;
    }

    public enum Geschlecht {
        MAENNLICH,
        WEIBLICH,
        DIVERS,
    }
}
