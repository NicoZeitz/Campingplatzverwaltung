package swe.ka.dhbw.model;

import de.dhbwka.swe.utils.model.Attribute;
import de.dhbwka.swe.utils.model.ICSVPersistable;
import de.dhbwka.swe.utils.model.IDepictable;
import de.dhbwka.swe.utils.model.IPersistable;
import swe.ka.dhbw.util.Validator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public final class Stoerung implements IDepictable, ICSVPersistable, IPersistable {
    public enum Status {
        AKTIV, BEHOBEN, IN_ARBEIT, VERSCHOBEN
    }

    public enum Attributes {
        STOERUNGSNUMMER,
        TITEL,
        BESCHREIBUNG,
        ERSTELLUNGSDATUM,
        BEHEBUNGSDATUM,
        STATUS,
    }

    public enum CSVPosition {
        STOERUNGSNUMMER,
        TITEL,
        BESCHREIBUNG,
        ERSTELLUNGSDATUM,
        BEHEBUNGSDATUM,
        STATUS,
        FOTO_IDS,
        VERANTWORTLICHER_ID,
        STELLPLATZFUNKTION_ID,
        DUMMY_DATA
    }

    private final int stoerungsnummer;
    private final Set<Foto> fotos = new LinkedHashSet<>();
    private String titel;
    private String beschreibung;
    private LocalDateTime erstellungsdatum;
    private LocalDateTime behebungsdatum;
    private Status status;
    private Personal verantwortlicher;
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private Optional<Stellplatzfunktion> stellplatzfunktion = Optional.empty();

    public Stoerung(final int stoerungsnummer,
                    final String titel,
                    final String beschreibung,
                    final LocalDateTime erstellungsdatum,
                    final LocalDateTime behebungsdatum,
                    final Status status) {
        Validator.getInstance().validateGreaterThanEqual(stoerungsnummer, 0);
        this.stoerungsnummer = stoerungsnummer;
        this.setTitel(titel);
        this.setBeschreibung(beschreibung);
        this.setErstellungsdatum(erstellungsdatum);
        this.setBehebungsdatum(behebungsdatum);
        this.setStatus(status);
    }

    public int getStoerungsnummer() {
        return this.stoerungsnummer;
    }

    public String getTitel() {
        return this.titel;
    }

    public void setTitel(final String titel) {
        Validator.getInstance().validateNotEmpty(titel);
        this.titel = titel;
    }

    public String getBeschreibung() {
        return this.beschreibung;
    }

    public void setBeschreibung(final String beschreibung) {
        Validator.getInstance().validateNotNull(beschreibung);
        this.beschreibung = beschreibung;
    }

    public LocalDateTime getErstellungsdatum() {
        return this.erstellungsdatum;
    }

    public void setErstellungsdatum(final LocalDateTime erstellungsdatum) {
        Validator.getInstance().validateNotNull(erstellungsdatum);
        this.erstellungsdatum = erstellungsdatum;
    }

    public LocalDateTime getBehebungsdatum() {
        return this.behebungsdatum;
    }

    public void setBehebungsdatum(final LocalDateTime behebungsdatum) {
        Validator.getInstance().validateNotNull(behebungsdatum);
        this.behebungsdatum = behebungsdatum;
    }

    public Status getStatus() {
        return this.status;
    }

    public void setStatus(final Status status) {
        Validator.getInstance().validateNotNull(status);
        this.status = status;
    }

    public Collection<Foto> getFotos() {
        return this.fotos;
    }

    public Personal getVerantwortlicher() {
        return this.verantwortlicher;
    }

    public void setVerantwortlicher(final Personal verantwortlicher) {
        Validator.getInstance().validateNotNull(verantwortlicher);
        this.verantwortlicher = verantwortlicher;
    }

    public Optional<Stellplatzfunktion> getStellplatzfunktion() {
        return this.stellplatzfunktion;
    }

    public void setStellplatzfunktion(final Stellplatzfunktion stellplatzfunktion) {
        this.setStellplatzfunktion(Optional.ofNullable(stellplatzfunktion));
    }

    public void setStellplatzfunktion(@SuppressWarnings("OptionalUsedAsFieldOrParameterType") final Optional<Stellplatzfunktion> stellplatzfunktion) {
        Validator.getInstance().validateNotNull(stellplatzfunktion);
        this.stellplatzfunktion = stellplatzfunktion;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof final Stoerung that)) return false;
        return this.getStoerungsnummer() == that.getStoerungsnummer();
    }

    @Override
    public Attribute[] getAttributeArray() {
        return new Attribute[] {
                new Attribute(Attributes.STOERUNGSNUMMER.name(),
                        this,
                        Integer.class,
                        this.getStoerungsnummer(),
                        this.getStoerungsnummer(),
                        false,
                        false,
                        false,
                        true),
                new Attribute(Attributes.TITEL.name(), this, String.class, this.getTitel(), this.getTitel(), true),
                new Attribute(Attributes.BESCHREIBUNG.name(),
                        this,
                        String.class,
                        this.getBeschreibung(),
                        this.getBeschreibung(),
                        true),
                new Attribute(Attributes.ERSTELLUNGSDATUM.name(),
                        this,
                        LocalDate.class,
                        this.getErstellungsdatum(),
                        this.getErstellungsdatum(),
                        true),
                new Attribute(Attributes.BEHEBUNGSDATUM.name(),
                        this,
                        LocalDate.class,
                        this.getBehebungsdatum(),
                        this.getBehebungsdatum(),
                        true),
                new Attribute(Attributes.STATUS.name(), this, Status.class, this.getStatus(), this.getStatus(), true),
        };
    }

    @Override
    public String[] getCSVData() {
        final var csvData = new String[CSVPosition.values().length];
        csvData[CSVPosition.STOERUNGSNUMMER.ordinal()] = Integer.toString(this.getStoerungsnummer());
        csvData[CSVPosition.TITEL.ordinal()] = this.getTitel();
        csvData[CSVPosition.BESCHREIBUNG.ordinal()] = this.getBeschreibung();
        csvData[CSVPosition.ERSTELLUNGSDATUM.ordinal()] = this.getErstellungsdatum().toString();
        csvData[CSVPosition.BEHEBUNGSDATUM.ordinal()] = this.getBehebungsdatum().toString();
        csvData[CSVPosition.STATUS.ordinal()] = this.getStatus().name();
        csvData[CSVPosition.FOTO_IDS.ordinal()] = this.getFotos().stream()
                .map(Foto::getPrimaryKey)
                .map(Object::toString)
                .collect(Collectors.joining(","));
        csvData[CSVPosition.VERANTWORTLICHER_ID.ordinal()] = this.getVerantwortlicher().getPrimaryKey().toString();
        csvData[CSVPosition.STELLPLATZFUNKTION_ID.ordinal()] = this.getStellplatzfunktion()
                .map(Stellplatzfunktion::getPrimaryKey)
                .map(Objects::toString)
                .orElse("");
        csvData[CSVPosition.DUMMY_DATA.ordinal()] = "NULL";
        return csvData;
    }

    @Override
    public String[] getCSVHeader() {
        return new String[] {
                CSVPosition.STOERUNGSNUMMER.name(),
                CSVPosition.TITEL.name(),
                CSVPosition.BESCHREIBUNG.name(),
                CSVPosition.ERSTELLUNGSDATUM.name(),
                CSVPosition.BEHEBUNGSDATUM.name(),
                CSVPosition.STATUS.name(),
                CSVPosition.FOTO_IDS.name(),
                CSVPosition.VERANTWORTLICHER_ID.name(),
                CSVPosition.STELLPLATZFUNKTION_ID.name(),
                CSVPosition.DUMMY_DATA.name(),
        };
    }

    @Override
    public String getElementID() {
        return Integer.toString(this.getStoerungsnummer());
    }

    @Override
    public Object getPrimaryKey() {
        return this.getStoerungsnummer();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getStoerungsnummer());
    }

    @Override
    public Attribute[] setAttributeValues(final Attribute[] attributeArray) {
        final var oldAttributeArray = this.getAttributeArray();

        for (final var attribute : attributeArray) {
            final var name = attribute.getName();
            final var value = attribute.getValue();
            if (name.equals(Attributes.STOERUNGSNUMMER.name()) && !value.equals(this.getStoerungsnummer())) {
                throw new IllegalArgumentException(
                        "Stoerung::setAttributeValues: Die Stoerungsnummer darf nicht verändert werden!");
            }

            if (name.equals(Attributes.TITEL.name()) && !value.equals(this.getTitel())) {
                this.setTitel((String) value);
            } else if (name.equals(Attributes.BESCHREIBUNG.name()) && !value.equals(this.getBeschreibung())) {
                this.setBeschreibung((String) value);
            } else if (name.equals(Attributes.ERSTELLUNGSDATUM.name()) && !value.equals(this.getErstellungsdatum())) {
                this.setErstellungsdatum((LocalDateTime) value);
            } else if (name.equals(Attributes.BEHEBUNGSDATUM.name()) && !value.equals(this.getBehebungsdatum())) {
                this.setBehebungsdatum((LocalDateTime) value);
            } else if (name.equals(Attributes.STATUS.name()) && !value.equals(this.getStatus())) {
                this.setStatus((Status) value);
            }
        }
        return oldAttributeArray;
    }

    @Override
    public String toString() {
        return "Stoerung{" +
                "stoerungsnummer=" + this.getStoerungsnummer() +
                ", titel='" + this.getTitel() + '\'' +
                ", beschreibung='" + this.getBeschreibung() + '\'' +
                ", erstellungsdatum=" + this.getErstellungsdatum() +
                ", behebungsdatum=" + this.getBehebungsdatum() +
                ", status=" + this.getStatus() +
                ", verantwortlicher=" + this.getVerantwortlicher() +
                ", stellplatzfunktion=" + this.getStellplatzfunktion() +
                ", fotos=[" + this.getFotos().stream().map(Objects::toString).collect(Collectors.joining(", ")) + "]" +
                '}';
    }

    @SuppressWarnings("unused")
    public void addFoto(final Foto foto) {
        Validator.getInstance().validateNotNull(foto);
        this.fotos.add(foto);
    }

    @SuppressWarnings("unused")
    public void removeFoto(final Foto foto) {
        this.fotos.remove(foto);
    }
}
