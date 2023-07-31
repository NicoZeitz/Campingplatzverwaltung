package swe.ka.dhbw.model;

import de.dhbwka.swe.utils.model.Attribute;
import de.dhbwka.swe.utils.model.ICSVPersistable;
import de.dhbwka.swe.utils.model.IDepictable;
import de.dhbwka.swe.utils.model.IPersistable;
import swe.ka.dhbw.util.Validator;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public final class Buchung implements IPersistable, ICSVPersistable, IDepictable, Comparable<Buchung> {
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
        GEBUCHTE_LEISTUNGEN_IDS,
        // TODO: ADD AUSRUESTUNG
        DUMMY_DATA
    }

    private final int buchungsnummer;
    private final Set<Chipkarte> ausgehaendigteChipkarten = new LinkedHashSet<>();
    private final List<Ausruestung> mitgebrachteAusruestung = new ArrayList<>();
    private final Set<Gast> zugehoerigeGaeste = new LinkedHashSet<>();
    private final List<GebuchteLeistung> gebuchteLeistungen = new ArrayList<>();
    private LocalDateTime anreise;
    private LocalDateTime abreise;
    private Stellplatz gebuchterStellplatz;
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private Optional<Rechnung> rechnung = Optional.empty();
    private Gast verantwortlicherGast;

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

    public Collection<Chipkarte> getAusgehaendigteChipkarten() {
        return this.ausgehaendigteChipkarten;
    }

    public List<Ausruestung> getMitgebrachteAusruestung() {
        return this.mitgebrachteAusruestung;
    }

    public Optional<Rechnung> getRechnung() {
        return this.rechnung;
    }

    public void setRechnung(final Rechnung rechnung) {
        this.setRechnung(Optional.of(rechnung));
    }

    public void setRechnung(@SuppressWarnings("OptionalUsedAsFieldOrParameterType") final Optional<Rechnung> rechnung) {
        Validator.getInstance().validateNotNull(rechnung);
        this.rechnung = rechnung;
    }

    public Gast getVerantwortlicherGast() {
        return this.verantwortlicherGast;
    }

    public void setVerantwortlicherGast(final Gast verantwortlicherGast) {
        Validator.getInstance().validateNotNull(verantwortlicherGast);

        if (this.verantwortlicherGast.equals(verantwortlicherGast)) {
            return;
        }
        
        this.verantwortlicherGast.removeBuchung(this);

        this.verantwortlicherGast = verantwortlicherGast;
        if (!verantwortlicherGast.getBuchungen().contains(this)) {
            verantwortlicherGast.addBuchung(this);
        }
    }

    public Collection<Gast> getZugehoerigeGaeste() {
        return this.zugehoerigeGaeste;
    }

    public List<GebuchteLeistung> getGebuchteLeistungen() {
        return this.gebuchteLeistungen;
    }

    @Override
    public int compareTo(final Buchung that) {
        final var res = this.getAnreise().compareTo(that.getAnreise());
        if (res == 0) {
            return this.getAbreise().compareTo(that.getAbreise());
        }
        return res;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Buchung that)) return false;
        return this.getBuchungsnummer() == that.getBuchungsnummer() &&
                Objects.equals(this.getAnreise(), that.getAnreise()) &&
                Objects.equals(this.getAbreise(), that.getAbreise()) &&
                Objects.equals(this.getGebuchterStellplatz(), that.getGebuchterStellplatz()) &&
                Objects.equals(this.getAusgehaendigteChipkarten(), that.getAusgehaendigteChipkarten()) &&
                Objects.equals(this.getMitgebrachteAusruestung(), that.getMitgebrachteAusruestung()) &&
                Objects.equals(this.getRechnung(), that.getRechnung()) &&
                Objects.equals(this.getVerantwortlicherGast(), that.getVerantwortlicherGast()) &&
                Objects.equals(this.getZugehoerigeGaeste(), that.getZugehoerigeGaeste()) &&
                Objects.equals(this.getGebuchteLeistungen(), that.getGebuchteLeistungen());
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
        csvData[CSVPosition.RECHNUNG_ID.ordinal()] = this.getRechnung()
                .map(Rechnung::getPrimaryKey)
                .map(Objects::toString)
                .orElse("");
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
        csvData[CSVPosition.DUMMY_DATA.ordinal()] = "NULL";
        return csvData;
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
                CSVPosition.GEBUCHTE_LEISTUNGEN_IDS.name(),
                CSVPosition.DUMMY_DATA.name()
        };
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
    public Attribute[] setAttributeValues(Attribute[] attributeArray) {
        final var oldAttributeArray = this.getAttributeArray();

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

    public void addAusgehaendigteChipkarte(final Chipkarte chipkarte) {
        Validator.getInstance().validateNotNull(chipkarte);
        this.ausgehaendigteChipkarten.add(chipkarte);
    }

    public void addGebuchteLeistung(final GebuchteLeistung gebuchteLeistung) {
        Validator.getInstance().validateNotNull(gebuchteLeistung);
        this.gebuchteLeistungen.add(gebuchteLeistung);
    }

    public void addMitgebrachteAusruestung(final Ausruestung ausruestung) {
        Validator.getInstance().validateNotNull(ausruestung);
        this.mitgebrachteAusruestung.add(ausruestung);
    }

    public void addZugehoerigerGast(final Gast gast) {
        Validator.getInstance().validateNotNull(gast);
        this.zugehoerigeGaeste.add(gast);
        if (!gast.getBuchungen().contains(this)) {
            gast.addBuchung(this);
        }
    }

    @SuppressWarnings("unused")
    public void removeAusgehaendigteChipkarte(final Chipkarte chipkarte) {
        this.ausgehaendigteChipkarten.remove(chipkarte);
    }

    @SuppressWarnings("unused")
    public void removeGebuchteLeistung(final GebuchteLeistung gebuchteLeistung) {
        this.gebuchteLeistungen.remove(gebuchteLeistung);
    }

    @SuppressWarnings("unused")
    public void removeMitgebrachteAusruestung(final Ausruestung ausruestung) {
        this.mitgebrachteAusruestung.remove(ausruestung);
    }

    @SuppressWarnings("unused")
    public void removeZugehoerigerGast(final Gast gast) {
        this.zugehoerigeGaeste.remove(gast);
        if (gast.getBuchungen().contains(this)) {
            gast.removeBuchung(this);
        }
    }
}
