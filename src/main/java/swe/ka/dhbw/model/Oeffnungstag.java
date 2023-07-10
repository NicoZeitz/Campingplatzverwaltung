package swe.ka.dhbw.model;

import de.dhbwka.swe.utils.model.Attribute;
import de.dhbwka.swe.utils.model.ICSVPersistable;
import de.dhbwka.swe.utils.model.IDepictable;
import de.dhbwka.swe.utils.model.IPersistable;
import swe.ka.dhbw.util.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Oeffnungstag implements ICSVPersistable, IPersistable, IDepictable {
    public enum Wochentag {
        MO, DI, MI, DO, FR, SA, SO
    }

    public enum Attributes {
        OEFFNUNGSTAG_ID,
        WOCHENTAG
    }

    public enum CSVPosition {
        OEFFNUNGSTAG_ID,
        WOCHENTAG,
        OEFFNUNGSZEITEN_IDS,
        DUMMY_DATA
    }

    private final int oeffnungstagId;
    private Wochentag wochentag;
    private List<Oeffnungszeit> oeffnungszeiten = new ArrayList<>();

    public Oeffnungstag(final int oeffnungstagId, final Wochentag wochentag) {
        Validator.getInstance().validateGreaterThan(oeffnungstagId, 0);
        this.oeffnungstagId = oeffnungstagId;
        this.setWochentag(wochentag);
    }

    public Wochentag getWochentag() {
        return this.wochentag;
    }

    public void setWochentag(final Wochentag wochentag) {
        Validator.getInstance().validateNotNull(wochentag);
        this.wochentag = wochentag;
    }

    public List<Oeffnungszeit> getOeffnungszeiten() {
        return this.oeffnungszeiten;
    }

    public void setOeffnungszeiten(List<Oeffnungszeit> oeffnungszeiten) {
        Validator.getInstance().validateNotNull(oeffnungszeiten);
        this.oeffnungszeiten = oeffnungszeiten;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Oeffnungstag that)) return false;
        return getWochentag() == that.getWochentag() && Objects.equals(
                getOeffnungszeiten(),
                that.getOeffnungszeiten());
    }

    @Override
    public Attribute[] getAttributeArray() {
        return new Attribute[] {
                new Attribute(Attributes.OEFFNUNGSTAG_ID.name(),
                        this,
                        Integer.class,
                        this.oeffnungstagId,
                        this.oeffnungstagId,
                        false, false, false, true),
                new Attribute(Attributes.WOCHENTAG.name(),
                        this,
                        Wochentag.class,
                        this.wochentag,
                        this.wochentag,
                        true)
        };
    }

    @Override
    public String[] getCSVData() {
        final var csvData = new String[CSVPosition.values().length];
        csvData[CSVPosition.OEFFNUNGSTAG_ID.ordinal()] = Integer.toString(this.oeffnungstagId);
        csvData[CSVPosition.WOCHENTAG.ordinal()] = this.wochentag.name();
        csvData[CSVPosition.OEFFNUNGSZEITEN_IDS.ordinal()] = this.oeffnungszeiten
                .stream()
                .map(Oeffnungszeit::getPrimaryKey)
                .map(Objects::toString)
                .collect(Collectors.joining(","));
        csvData[CSVPosition.DUMMY_DATA.ordinal()] = "NULL";
        return csvData;
    }

    @Override
    public String[] getCSVHeader() {
        return new String[] {
                CSVPosition.OEFFNUNGSTAG_ID.name(),
                CSVPosition.WOCHENTAG.name(),
                CSVPosition.OEFFNUNGSZEITEN_IDS.name(),
                CSVPosition.DUMMY_DATA.name()
        };
    }

    @Override
    public String getElementID() {
        return Integer.toString(this.oeffnungstagId);
    }

    @Override
    public Object getPrimaryKey() {
        return this.oeffnungstagId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getWochentag(), getOeffnungszeiten());
    }

    @Override
    public String toString() {
        return "Oeffnungstag{" +
                "wochentag=" + wochentag +
                ", oeffnungszeiten=[" + oeffnungszeiten.stream()
                .map(Objects::toString)
                .collect(Collectors.joining(", ")) + "]" +
                '}';
    }

    public void addOeffnungszeit(Oeffnungszeit oeffnungszeit) {
        Validator.getInstance().validateNotNull(oeffnungszeit);
        this.oeffnungszeiten.add(oeffnungszeit);
    }

    public void removeOeffnungszeit(Oeffnungszeit oeffnungszeit) {
        Validator.getInstance().validateNotNull(oeffnungszeit);
        this.oeffnungszeiten.remove(oeffnungszeit);
    }
}
