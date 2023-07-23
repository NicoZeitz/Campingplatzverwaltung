package swe.ka.dhbw.model;

import de.dhbwka.swe.utils.model.Attribute;
import de.dhbwka.swe.utils.model.ICSVPersistable;
import de.dhbwka.swe.utils.model.IDepictable;
import de.dhbwka.swe.utils.model.IPersistable;
import swe.ka.dhbw.util.Validator;

import java.util.Objects;

public class Person implements IDepictable, IPersistable, ICSVPersistable {
    public enum Geschlecht {
        MAENNLICH("männlich"),
        WEIBLICH("weiblich"),
        DIVERS("divers");

        private final String name;

        Geschlecht(final String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    public enum Attributes {
        PERSON_ID,
        VORNAME,
        NACHNAME,
        GESCHLECHT,
        EMAIL,
        TELEFONNUMMER
    }

    public enum CSVPosition {
        PERSON_ID,
        VORNAME,
        NACHNAME,
        GESCHLECHT,
        EMAIL,
        TELEFONNUMMER,
        DUMMY_DATA
    }

    protected final int personId;
    protected String vorname;
    protected String nachname;
    protected Geschlecht geschlecht;
    protected String email;
    protected String telefonnummer;


    public Person(
            final int personId,
            final String vorname,
            final String nachname,
            final Geschlecht geschlecht,
            final String email,
            final String telefonnummer
    ) {
        Validator.getInstance().validateGreaterThanEqual(personId, 0);
        this.personId = personId;
        this.setVorname(vorname);
        this.setNachname(nachname);
        this.setGeschlecht(geschlecht);
        this.setEmail(email);
        this.setTelefonnummer(telefonnummer);
    }

    public int getPersonId() {
        return personId;
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

    public String getName() {
        return this.getVorname() + " " + this.getNachname();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof final Person that)) return false;
        return Objects.equals(this.getVorname(), that.getVorname()) &&
                Objects.equals(this.getNachname(), that.getNachname()) &&
                this.getGeschlecht() == that.getGeschlecht() &&
                Objects.equals(this.getEmail(), that.getEmail()) &&
                Objects.equals(this.getTelefonnummer(), that.getTelefonnummer());
    }

    @Override
    public Attribute[] getAttributeArray() {
        return new Attribute[] {
                new Attribute(Attributes.PERSON_ID.name(),
                        this,
                        Integer.class,
                        this.getPersonId(),
                        this.getPersonId(),
                        false,
                        false,
                        false,
                        true),
                new Attribute(Attributes.VORNAME.name(), this, String.class, this.getVorname(), this.getVorname(), true),
                new Attribute(Attributes.NACHNAME.name(), this, String.class, this.getNachname(), this.getNachname(), true),
                new Attribute(Attributes.GESCHLECHT.name(),
                        this,
                        Geschlecht.class,
                        this.getGeschlecht(),
                        this.getGeschlecht(),
                        true),
                new Attribute(Attributes.EMAIL.name(), this, String.class, this.getEmail(), this.getEmail(), true),
                new Attribute(Attributes.TELEFONNUMMER.name(),
                        this,
                        String.class,
                        this.getTelefonnummer(),
                        this.getTelefonnummer(),
                        true)
        };
    }

    @Override
    public String[] getCSVData() {
        final var csvData = new String[CSVPosition.values().length];
        csvData[CSVPosition.PERSON_ID.ordinal()] = Integer.toString(this.getPersonId());
        csvData[CSVPosition.VORNAME.ordinal()] = this.getVorname();
        csvData[CSVPosition.NACHNAME.ordinal()] = this.getNachname();
        csvData[CSVPosition.GESCHLECHT.ordinal()] = this.getGeschlecht().name();
        csvData[CSVPosition.EMAIL.ordinal()] = this.getEmail();
        csvData[CSVPosition.TELEFONNUMMER.ordinal()] = this.getTelefonnummer();
        csvData[CSVPosition.DUMMY_DATA.ordinal()] = "NULL";
        return csvData;
    }

    @Override
    public String[] getCSVHeader() {
        return new String[] {
                CSVPosition.PERSON_ID.name(),
                CSVPosition.VORNAME.name(),
                CSVPosition.NACHNAME.name(),
                CSVPosition.GESCHLECHT.name(),
                CSVPosition.EMAIL.name(),
                CSVPosition.TELEFONNUMMER.name(),
                CSVPosition.DUMMY_DATA.name()
        };
    }

    @Override
    public String getElementID() {
        return Integer.toString(this.getPersonId());
    }

    @Override
    public Object getPrimaryKey() {
        return this.getPersonId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getVorname(),
                this.getNachname(),
                this.getGeschlecht(),
                this.getEmail(),
                this.getTelefonnummer());
    }

    @Override
    public Attribute[] setAttributeValues(Attribute[] attributeArray) {
        final var oldAttributeArray = this.getAttributeArray();

        for (final var attribute : attributeArray) {
            final var name = attribute.getName();
            final var value = attribute.getValue();
            if (name.equals(Attributes.PERSON_ID.name()) && !value.equals(this.getPersonId())) {
                throw new IllegalArgumentException("Person::setAttributeValues: Die PersonId darf nicht verändert werden!");
            }

            if (name.equals(Attributes.VORNAME.name()) && !value.equals(this.getVorname())) {
                this.setVorname((String) value);
            } else if (name.equals(Attributes.NACHNAME.name()) && !value.equals(this.getNachname())) {
                this.setNachname((String) value);
            } else if (name.equals(Attributes.GESCHLECHT.name()) && !value.equals(this.getGeschlecht())) {
                this.setGeschlecht((Geschlecht) value);
            } else if (name.equals(Attributes.EMAIL.name()) && !value.equals(this.getEmail())) {
                this.setEmail((String) value);
            } else if (name.equals(Attributes.TELEFONNUMMER.name()) && !value.equals(this.getTelefonnummer())) {
                this.setTelefonnummer((String) value);
            }
        }
        return oldAttributeArray;
    }

    @Override
    public String toString() {
        return "Person{" +
                "vorname='" + this.getVorname() + '\'' +
                ", nachname='" + this.getNachname() + '\'' +
                ", geschlecht=" + this.getGeschlecht() +
                ", email='" + this.getEmail() + '\'' +
                ", telefonnummer='" + this.getTelefonnummer() + '\'' +
                '}';
    }
}
