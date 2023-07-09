package swe.ka.dhbw.model;

import de.dhbwka.swe.utils.model.Attribute;
import de.dhbwka.swe.utils.model.ICSVPersistable;
import de.dhbwka.swe.utils.model.IDepictable;
import de.dhbwka.swe.utils.model.IPersistable;
import swe.ka.dhbw.util.Validator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Personal extends Person implements IDepictable, ICSVPersistable, IPersistable {
    public enum Rolle {
        ADMINISTRATOR,
        PLATZWART,
        HAUSMEISTER,
        EMPFANG
    }

    public enum Attributes {
        PERSONAL_ID,
        VORNAME,
        NACHNAME,
        GESCHLECHT,
        EMAIL,
        TELEFONNUMMER,
        PERSONALNUMMER,
        GEBURTSTAG,
        ROLLE
    }

    public enum CSVPosition {
        PERSONAL_ID,
        VORNAME,
        NACHNAME,
        GESCHLECHT,
        EMAIL,
        TELEFONNUMMER,
        PERSONALNUMMER,
        GEBURTSTAG,
        ROLLE,
        STOERUNGEN_IDS
    }

    private final List<Stoerung> stoerungen = new ArrayList<>();
    private final int nummer;
    private LocalDate geburtstag;
    private Rolle benutzerrolle;

    public Personal(
            final int nummer,
            final String vorname,
            final String nachname,
            final Geschlecht geschlecht,
            final String email,
            final String telefonnummer,
            final LocalDate geburtstag,
            final Rolle benutzerrolle
    ) {
        super(nummer, vorname, nachname, geschlecht, email, telefonnummer);
        Validator.getInstance().validateGreaterThanEqual(nummer, 0);
        this.nummer = nummer;
        this.setGeburtstag(geburtstag);
        this.setBenutzerrolle(benutzerrolle);
    }

    public int getNummer() {
        return this.nummer;
    }


    public LocalDate getGeburtstag() {
        return this.geburtstag;
    }

    public void setGeburtstag(final LocalDate geburtstag) {
        Validator.getInstance().validateNotNull(geburtstag);
        this.geburtstag = geburtstag;
    }

    public Rolle getBenutzerrolle() {
        return this.benutzerrolle;
    }

    public void setBenutzerrolle(final Rolle benutzerrolle) {
        Validator.getInstance().validateNotNull(benutzerrolle);
        this.benutzerrolle = benutzerrolle;
    }

    public List<Stoerung> getStoerungen() {
        return this.stoerungen;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof final Personal that)) return false;
        return this.getNummer() == that.getNummer() &&
                Objects.equals(this.getGeburtstag(), that.getGeburtstag()) &&
                this.getBenutzerrolle() == that.getBenutzerrolle();
    }

    @Override
    public Attribute[] getAttributeArray() {
        final var superAttributes = super.getAttributeArray();
        final var attributes = Arrays.copyOf(superAttributes, superAttributes.length + 3);
        attributes[Attributes.PERSONALNUMMER.ordinal()] = new Attribute(
                Attributes.PERSONALNUMMER.name(),
                this,
                Integer.class,
                this.getNummer(),
                this.getNummer(),
                true,
                false,
                false,
                true);
        attributes[Attributes.GEBURTSTAG.ordinal()] = new Attribute(
                Attributes.GEBURTSTAG.name(),
                this,
                LocalDate.class,
                this.getGeburtstag(),
                this.getGeburtstag(),
                true);
        attributes[Attributes.ROLLE.ordinal()] = new Attribute(
                Attributes.ROLLE.name(),
                this,
                Rolle.class,
                this.getBenutzerrolle(),
                this.getBenutzerrolle(),
                true);
        return attributes;
    }

    @Override
    public String[] getCSVData() {
        final var csvData = new String[CSVPosition.values().length];
        csvData[CSVPosition.PERSONAL_ID.ordinal()] = String.valueOf(this.getNummer());
        csvData[CSVPosition.VORNAME.ordinal()] = this.getVorname();
        csvData[CSVPosition.NACHNAME.ordinal()] = this.getNachname();
        csvData[CSVPosition.GESCHLECHT.ordinal()] = this.getGeschlecht().toString();
        csvData[CSVPosition.EMAIL.ordinal()] = this.getEmail();
        csvData[CSVPosition.TELEFONNUMMER.ordinal()] = this.getTelefonnummer();
        csvData[CSVPosition.PERSONALNUMMER.ordinal()] = String.valueOf(this.getNummer());
        csvData[CSVPosition.GEBURTSTAG.ordinal()] = this.getGeburtstag().toString();
        csvData[CSVPosition.ROLLE.ordinal()] = this.getBenutzerrolle().toString();
        csvData[CSVPosition.STOERUNGEN_IDS.ordinal()] = this.getStoerungen()
                .stream()
                .map(Stoerung::getPrimaryKey)
                .map(Objects::toString)
                .collect(Collectors.joining(","));
        return csvData;
    }

    @Override
    public String[] getCSVHeader() {
        return new String[] {
                CSVPosition.PERSONAL_ID.toString(),
                CSVPosition.VORNAME.toString(),
                CSVPosition.NACHNAME.toString(),
                CSVPosition.GESCHLECHT.toString(),
                CSVPosition.EMAIL.toString(),
                CSVPosition.TELEFONNUMMER.toString(),
                CSVPosition.PERSONALNUMMER.toString(),
                CSVPosition.GEBURTSTAG.toString(),
                CSVPosition.ROLLE.toString(),
                CSVPosition.STOERUNGEN_IDS.toString()
        };
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getNummer(), this.getGeburtstag(), this.getBenutzerrolle());
    }

    @Override
    public Attribute[] setAttributeValues(Attribute[] attributeArray) {
        final var oldAttributeArray = this.getAttributeArray();

        super.setAttributeValues(attributeArray);

        for (final var attribute : attributeArray) {
            final var name = attribute.getName();
            final var value = attribute.getValue();

            if (name.equals(Attributes.PERSONALNUMMER.name()) && !value.equals(this.getNummer())) {
                throw new IllegalArgumentException("Personalnummer darf nicht geändert werden!");
            }

            if (name.equals(Attributes.GEBURTSTAG.name()) && !value.equals(this.getGeburtstag())) {
                this.setGeburtstag((LocalDate) value);
            } else if (name.equals(Attributes.ROLLE.name()) && !value.equals(this.getBenutzerrolle())) {
                this.setBenutzerrolle((Rolle) value);
            }
        }

        return oldAttributeArray;
    }

    @Override
    public String toString() {
        return "Personal{" +
                "nummer=" + this.getNummer() +
                ", geburtstag=" + this.getGeschlecht() +
                ", benutzerrolle=" + this.getBenutzerrolle() +
                ", vorname='" + this.getVorname() +
                ", nachname='" + this.getNachname() +
                ", geschlecht=" + this.getGeschlecht() +
                ", email='" + this.getEmail() +
                ", telefonnummer='" + this.getTelefonnummer() +
                '}';
    }

    public void addStoerung(final Stoerung stoerung) {
        Validator.getInstance().validateNotNull(stoerung);
        this.stoerungen.add(stoerung);
        if (!stoerung.getVerantwortlicher().equals(this)) {
            stoerung.setVerantwortlicher(this);
        }
    }

    public void removeStoerung(final Stoerung stoerung) {
        Validator.getInstance().validateNotNull(stoerung);
        this.stoerungen.remove(stoerung);
    }
}
