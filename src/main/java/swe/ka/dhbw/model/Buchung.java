package swe.ka.dhbw.model;

import de.dhbwka.swe.utils.model.Attribute;
import de.dhbwka.swe.utils.model.ICSVPersistable;
import de.dhbwka.swe.utils.model.IDepictable;
import de.dhbwka.swe.utils.model.IPersistable;
import swe.ka.dhbw.util.Validator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class Buchung implements IPersistable, ICSVPersistable, IDepictable {
    private final int buchungsnummer;
    private LocalDateTime anreise;
    private LocalDateTime abreise;
    private Stellplatz gebuchterStellplatz;
    private List<Chipkarte> ausgehaendigteChipkarten;
    private List<Ausruestung> mitgebrachteAusruestung;
    private Rechnung rechnung;
    private Gast verantwortlicherGast;
    private List<Gast> zugehoerigeGaeste;
    private List<GebuchteLeistung> gebuchteLeistungen;

    public Buchung(final int buchungsnummer,
                   final LocalDateTime anreise,
                   final LocalDateTime abreise) {
        this.buchungsnummer = buchungsnummer;
        this.setAnreise(anreise);
        this.setAbreise(abreise);
    }

    public int getBuchungsnummer() {
        return this.buchungsnummer;
    }

    public LocalDateTime getAnreise() {
        return this.anreise;
    }

    public void setAnreise(final LocalDateTime anreise) {
        Validator.getInstance().validateNotNull(anreise);
        this.anreise = anreise;
    }

    public LocalDateTime getAbreise() {
        return this.abreise;
    }

    public void setAbreise(final LocalDateTime abreise) {
        Validator.getInstance().validateNotNull(abreise);
        this.abreise = abreise;
    }

    public Stellplatz getGebuchterStellplatz() {
        return this.gebuchterStellplatz;
    }

    public void setGebuchterStellplatz(final Stellplatz gebuchterStellplatz) {
        Validator.getInstance().validateNotNull(gebuchterStellplatz);
        this.gebuchterStellplatz = gebuchterStellplatz;
    }

    public List<Chipkarte> getAusgehaendigteChipkarten() {
        return this.ausgehaendigteChipkarten;
    }

    public void addAusgehaendigteChipkarte(final Chipkarte chipkarte) {
        Validator.getInstance().validateNotNull(chipkarte);
        this.ausgehaendigteChipkarten.add(chipkarte);
    }

    public void removeAusgehaendigteChipkarte(final Chipkarte chipkarte) {
        this.ausgehaendigteChipkarten.remove(chipkarte);
    }

    public List<Ausruestung> getMitgebrachteAusruestung() {
        return this.mitgebrachteAusruestung;
    }

    public void addMitgebrachteAusruestung(final Ausruestung ausruestung) {
        Validator.getInstance().validateNotNull(ausruestung);
        this.mitgebrachteAusruestung.add(ausruestung);
    }

    public void removeMitgebrachteAusruestung(final Ausruestung ausruestung) {
        this.mitgebrachteAusruestung.remove(ausruestung);
    }

    public Rechnung getRechnung() {
        return this.rechnung;
    }

    public void setRechnung(final Rechnung rechnung) {
        Validator.getInstance().validateNotNull(rechnung);
        this.rechnung = rechnung;
    }

    public Gast getVerantwortlicherGast() {
        return this.verantwortlicherGast;
    }

    public void setVerantwortlicherGast(final Gast verantwortlicherGast) {
        Validator.getInstance().validateNotNull(verantwortlicherGast);
        this.verantwortlicherGast = verantwortlicherGast;
    }

    public List<Gast> getZugehoerigeGaeste() {
        return this.zugehoerigeGaeste;
    }

    public void addZugehoerigerGast(final Gast gast) {
        Validator.getInstance().validateNotNull(gast);
        this.zugehoerigeGaeste.add(gast);
    }

    public void removeZugehoerigerGast(final Gast gast) {
        this.zugehoerigeGaeste.remove(gast);
    }

    public List<GebuchteLeistung> getGebuchteLeistungen() {
        return this.gebuchteLeistungen;
    }

    public void addGebuchteLeistung(final GebuchteLeistung gebuchteLeistung) {
        Validator.getInstance().validateNotNull(gebuchteLeistung);
        this.gebuchteLeistungen.add(gebuchteLeistung);
    }

    public void removeGebuchteLeistung(final GebuchteLeistung gebuchteLeistung) {
        this.gebuchteLeistungen.remove(gebuchteLeistung);
    }

    @Override
    public String getElementID() {
        return Integer.toString(this.getBuchungsnummer());
    }

    @Override
    public Object getPrimaryKey() {
        return this.getBuchungsnummer();
    }

    @Override
    public Attribute[] getAttributeArray() {
        return new Attribute[] {
                new Attribute(Attributes.BUCHUNGSNUMMER.name(),
                        this,
                        Integer.class,
                        this.getBuchungsnummer(),
                        this.getBuchungsnummer(),
                        true,
                        false,
                        false,
                        true),
                new Attribute(Attributes.ANREISE.name(), this, LocalDateTime.class, this.getAnreise(), this.getAnreise(), true),
                new Attribute(Attributes.ABREISE.name(), this, LocalDateTime.class, this.getAbreise(), this.getAbreise(), true),
        };
    }

    @Override
    public Attribute[] setAttributeValues(Attribute[] attributeArray) {
        final var oldAttributeArray = this.getAttributeArray().clone();

        for (final var attribute : attributeArray) {
            final var name = attribute.getName();
            final var value = attribute.getValue();
            if (name.equals(Attributes.BUCHUNGSNUMMER.name()) && !value.equals(this.getBuchungsnummer())) {
                throw new IllegalArgumentException("Buchung::setAttributeValues: Die Buchungsnummer darf nicht verändert werden!");
            }

            if (name.equals(Attributes.ANREISE.name()) && !value.equals(this.getAnreise())) {
                this.setAnreise((LocalDateTime) value);
            } else if (name.equals(Attributes.ABREISE.name()) && !value.equals(this.getAbreise())) {
                this.setAbreise((LocalDateTime) value);
            }
        }
        return oldAttributeArray;
    }

    @Override
    public String[] getCSVHeader() {
        return new String[] {
                CSVPosition.BUCHUNGSNUMMER.name(),
                CSVPosition.ANREISE.name(),
                CSVPosition.ABREISE.name(),
                CSVPosition.GEBUCHTER_STELLPLATZ_ID.name(),
                CSVPosition.AUSGEHAENDIGTE_CHIPKARTEN_IDS.name(),
                CSVPosition.RECHNUNG_ID.name(),
                CSVPosition.VERANTWORTLICHER_GAST_ID.name(),
                CSVPosition.ZUGEHOERIGE_GAESTE_IDS.name(),
                CSVPosition.GEBUCHTE_LEISTUNGEN_IDS.name()
        };
    }

    @Override
    public String[] getCSVData() {
        final var csvData = new String[CSVPosition.values().length];
        csvData[CSVPosition.BUCHUNGSNUMMER.ordinal()] = Integer.toString(this.getBuchungsnummer());
        csvData[CSVPosition.ANREISE.ordinal()] = this.getAnreise().toString();
        csvData[CSVPosition.ABREISE.ordinal()] = this.getAbreise().toString();
        csvData[CSVPosition.GEBUCHTER_STELLPLATZ_ID.ordinal()] = this.getGebuchterStellplatz().getPrimaryKey().toString();
        csvData[CSVPosition.AUSGEHAENDIGTE_CHIPKARTEN_IDS.ordinal()] = this.getAusgehaendigteChipkarten()
                .stream()
                .map(Chipkarte::getPrimaryKey)
                .map(Object::toString)
                .collect(Collectors.joining(","));
        csvData[CSVPosition.RECHNUNG_ID.ordinal()] = this.getRechnung().getPrimaryKey().toString();
        csvData[CSVPosition.VERANTWORTLICHER_GAST_ID.ordinal()] = this.getVerantwortlicherGast().getPrimaryKey().toString();
        csvData[CSVPosition.ZUGEHOERIGE_GAESTE_IDS.ordinal()] = this.getZugehoerigeGaeste()
                .stream()
                .map(Gast::getPrimaryKey)
                .map(Object::toString)
                .collect(Collectors.joining(","));
        csvData[CSVPosition.GEBUCHTE_LEISTUNGEN_IDS.ordinal()] = this.getGebuchteLeistungen()
                .stream()
                .map(GebuchteLeistung::getPrimaryKey)
                .map(Object::toString)
                .collect(Collectors.joining(","));
        return csvData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Buchung buchung)) return false;
        return this.getBuchungsnummer() == buchung.getBuchungsnummer() &&
                Objects.equals(this.getAnreise(), buchung.getAnreise()) &&
                Objects.equals(this.getAbreise(), buchung.getAbreise()) &&
                Objects.equals(this.getGebuchterStellplatz(), buchung.getGebuchterStellplatz()) &&
                Objects.equals(this.getAusgehaendigteChipkarten(), buchung.getAusgehaendigteChipkarten()) &&
                Objects.equals(this.getMitgebrachteAusruestung(), buchung.getMitgebrachteAusruestung()) &&
                Objects.equals(this.getRechnung(), buchung.getRechnung()) &&
                Objects.equals(this.getVerantwortlicherGast(), buchung.getVerantwortlicherGast()) &&
                Objects.equals(this.getZugehoerigeGaeste(), buchung.getZugehoerigeGaeste()) &&
                Objects.equals(this.getGebuchteLeistungen(), buchung.getGebuchteLeistungen());
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                this.getBuchungsnummer(),
                this.getAnreise(),
                this.getAbreise(),
                this.getGebuchterStellplatz(),
                this.getAusgehaendigteChipkarten(),
                this.getMitgebrachteAusruestung(),
                this.getRechnung(),
                this.getVerantwortlicherGast(),
                this.getZugehoerigeGaeste(),
                this.getGebuchteLeistungen()
        );
    }

    @Override
    public String toString() {
        return "Buchung{" +
                "buchungsnummer=" + buchungsnummer +
                ", anreise=" + anreise +
                ", abreise=" + abreise +
                ", gebuchterStellplatz=" + gebuchterStellplatz +
                ", ausgehaendigteChipkarten=[" + ausgehaendigteChipkarten.stream()
                .map(Objects::toString)
                .collect(Collectors.joining(", ")) + "]" +
                ", mitgebrachteAusruestung=[" + mitgebrachteAusruestung.stream()
                .map(Objects::toString)
                .collect(Collectors.joining(", ")) + "]" +
                ", rechnung=" + rechnung +
                ", verantwortlicherGast=" + verantwortlicherGast +
                ", zugehoerigeGaeste=[" + zugehoerigeGaeste.stream()
                .map(Objects::toString)
                .collect(Collectors.joining(", ")) + "]" +
                ", gebuchteLeistungen=[" + gebuchteLeistungen.stream()
                .map(Objects::toString)
                .collect(Collectors.joining(", ")) + "]" +
                '}';
    }

    public enum Attributes {
        BUCHUNGSNUMMER,
        ANREISE,
        ABREISE
    }

    public enum CSVPosition {
        BUCHUNGSNUMMER,
        ANREISE,
        ABREISE,
        GEBUCHTER_STELLPLATZ_ID,
        AUSGEHAENDIGTE_CHIPKARTEN_IDS,
        RECHNUNG_ID,
        VERANTWORTLICHER_GAST_ID,
        ZUGEHOERIGE_GAESTE_IDS,
        GEBUCHTE_LEISTUNGEN_IDS
    }
}
