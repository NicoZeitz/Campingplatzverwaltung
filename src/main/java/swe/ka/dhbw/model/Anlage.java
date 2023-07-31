package swe.ka.dhbw.model;

import de.dhbwka.swe.utils.model.Attribute;
import de.dhbwka.swe.utils.model.IDepictable;
import de.dhbwka.swe.utils.model.IPersistable;
import swe.ka.dhbw.util.Validator;

import java.util.*;
import java.util.stream.Collectors;

public sealed abstract class Anlage implements IDepictable, IPersistable permits Bereich, Einrichtung, Stellplatz {
    public enum Attributes {
        ANLAGE_ID,
        LAGE_LATITUDE,
        LAGE_LONGITUDE
    }

    protected final int anlageId;
    protected final Set<Foto> fotos = new LinkedHashSet<>();
    protected GPSPosition lage;
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    protected Optional<Bereich> bereich = Optional.empty();

    public Anlage(final int anlageId, final GPSPosition lage) {
        Validator.getInstance().validateGreaterThanEqual(anlageId, 0);
        this.anlageId = anlageId;
        this.setLage(lage);
    }

    public int getAnlageId() {
        return this.anlageId;
    }

    public GPSPosition getLage() {
        return this.lage;
    }

    public void setLage(final GPSPosition lage) {
        Validator.getInstance().validateNotNull(lage);
        this.lage = lage;
    }

    public Optional<Bereich> getBereich() {
        return this.bereich;
    }

    public void setBereich(final Bereich bereich) {
        this.setBereich(Optional.ofNullable(bereich));
    }

    public void setBereich(@SuppressWarnings("OptionalUsedAsFieldOrParameterType") final Optional<Bereich> bereich) {
        Validator.getInstance().validateNotNull(bereich);

        if (this.bereich.equals(bereich)) {
            return;
        }
        
        this.bereich.ifPresent(b -> b.removeAnlage(this));

        this.bereich = bereich;
        if (bereich.isPresent() && !bereich.get().getAnlagen().contains(this)) {
            bereich.get().addAnlage(this);
        }
    }

    public Collection<Foto> getFotos() {
        return this.fotos;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof final Anlage that)) return false;
        return Objects.equals(this.getLage(), that.getLage()) &&
                Objects.equals(this.getBereich(), that.getBereich()) &&
                Objects.equals(this.getFotos(), that.getFotos());
    }

    @Override
    public Attribute[] getAttributeArray() {
        return new Attribute[] {
                new Attribute(Attributes.ANLAGE_ID.name(),
                        this,
                        Integer.class,
                        this.getAnlageId(),
                        this.getAnlageId(),
                        false,
                        false,
                        false,
                        true),
                new Attribute(Attributes.LAGE_LATITUDE.name(),
                        this,
                        Double.class,
                        this.getLage().getLatitude(),
                        this.getLage().getLatitude(),
                        true),
                new Attribute(Attributes.LAGE_LONGITUDE.name(),
                        this,
                        Double.class,
                        this.getLage().getLongitude(),
                        this.getLage().getLongitude(),
                        true),
        };
    }

    @Override
    public String getElementID() {
        return Integer.toString(this.getAnlageId());
    }

    @Override
    public Object getPrimaryKey() {
        return this.getAnlageId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getLage(), this.getBereich(), this.getFotos());
    }

    @Override
    public Attribute[] setAttributeValues(final Attribute[] attributeArray) {
        final var oldAttributeArray = this.getAttributeArray();

        for (final var attribute : attributeArray) {
            final var name = attribute.getName();
            final var value = attribute.getValue();

            if (name.equals(Attributes.ANLAGE_ID.name()) && !value.equals(this.getAnlageId())) {
                throw new UnsupportedOperationException("Anlage::setAttributeValues: AnlageId darf nicht verändert werden!");
            }

            if (name.equals(Attributes.LAGE_LATITUDE.name()) && !value.equals(this.getLage().getLatitude())) {
                this.getLage().setLatitude((double) value);
            } else if (name.equals(Attributes.LAGE_LONGITUDE.name()) && !value.equals(this.getLage().getLongitude())) {
                this.getLage().setLongitude((double) value);
            }
        }
        return oldAttributeArray;
    }

    @Override
    public String toString() {
        return "Anlage{" +
                "lage=" + this.getLage() +
                ", bereich=" + this.getBereich() +
                ", fotos=[" + this.getFotos().stream().map(Objects::toString).collect(Collectors.joining(", ")) + "]" +
                '}';
    }

    public void addFoto(final Foto foto) {
        Validator.getInstance().validateNotNull(foto);
        this.fotos.add(foto);
    }

    @SuppressWarnings("unused")
    public void removeFoto(final Foto foto) {
        this.fotos.remove(foto);
    }
}
