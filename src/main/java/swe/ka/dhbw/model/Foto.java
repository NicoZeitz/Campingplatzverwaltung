package swe.ka.dhbw.model;

import de.dhbwka.swe.utils.model.*;
import de.dhbwka.swe.utils.util.AppLogger;
import de.dhbwka.swe.utils.util.ImageLoader;
import swe.ka.dhbw.util.Validator;

import java.nio.file.Path;
import java.util.Objects;

public class Foto implements IDepictable, ICSVPersistable, IPersistable {
    public enum Attributes {
        FOTO_ID,
        DATEIPFAD,
        TITEL,
        BESCHREIBUNG
    }

    public enum CSVPosition {
        FOTO_ID,
        DATEIPFAD,
        TITEL,
        BESCHREIBUNG
    }

    private final int fotoId;
    private Path dateipfad;
    private String titel;
    private String beschreibung;
    private ImageElement image;

    public Foto(
            final int fotoId,
            final Path dateipfad,
            final String titel,
            final String beschreibung
    ) {
        this.fotoId = fotoId;
        this.setDateipfad(dateipfad);
        this.setTitel(titel);
        this.setBeschreibung(beschreibung);
        try {
            this.setImage(ImageLoader.loadImageElement(dateipfad.toAbsolutePath().normalize().toString()));
        } catch (Exception e) {
            // supress errors
            AppLogger.getInstance().error(e.getMessage());
        }
    }

    public int getFotoId() {
        return this.fotoId;
    }

    public Path getDateipfad() {
        return this.dateipfad;
    }

    public void setDateipfad(final Path dateipfad) {
        Validator.getInstance().validateNotNull(dateipfad);
        this.dateipfad = dateipfad;
    }

    public String getTitel() {
        return this.titel;
    }

    public void setTitel(final String titel) {
        Validator.getInstance().validateNotNull(titel);
        this.titel = titel;
    }

    public String getBeschreibung() {
        return this.beschreibung;
    }

    public void setBeschreibung(final String beschreibung) {
        Validator.getInstance().validateNotNull(beschreibung);
        this.beschreibung = beschreibung;
    }

    public ImageElement getImage() {
        return this.image;
    }

    public void setImage(final ImageElement image) {
        Validator.getInstance().validateNotNull(image);
        this.image = image;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof final Foto that)) return false;
        return Objects.equals(this.getDateipfad(), that.getDateipfad()) &&
                Objects.equals(this.getTitel(), that.getTitel()) &&
                Objects.equals(this.getBeschreibung(), that.getBeschreibung());
    }

    @Override
    public Attribute[] getAttributeArray() {
        return new Attribute[] {
                new Attribute(Attributes.FOTO_ID.name(),
                        this,
                        Integer.class,
                        this.getFotoId(),
                        this.getFotoId(),
                        false,
                        false,
                        false,
                        true),
                new Attribute(Attributes.DATEIPFAD.name(), this, Path.class, this.getDateipfad(), this.getDateipfad(), true),
                new Attribute(Attributes.TITEL.name(), this, String.class, this.getTitel(), this.getTitel(), true),
                new Attribute(Attributes.BESCHREIBUNG.name(),
                        this,
                        String.class,
                        this.getBeschreibung(),
                        this.getBeschreibung(),
                        true)
        };
    }

    @Override
    public String[] getCSVData() {
        final String[] csvData = new String[CSVPosition.values().length];
        csvData[CSVPosition.FOTO_ID.ordinal()] = Integer.toString(this.getFotoId());
        csvData[CSVPosition.DATEIPFAD.ordinal()] = this.getDateipfad().toString().replaceAll("\\\\", "/");
        csvData[CSVPosition.TITEL.ordinal()] = this.getTitel();
        csvData[CSVPosition.BESCHREIBUNG.ordinal()] = this.getBeschreibung();
        return csvData;
    }

    @Override
    public String[] getCSVHeader() {
        return new String[] {
                CSVPosition.FOTO_ID.name(),
                CSVPosition.DATEIPFAD.name(),
                CSVPosition.TITEL.name(),
                CSVPosition.BESCHREIBUNG.name()
        };
    }

    @Override
    public String getElementID() {
        return Integer.toString(this.getFotoId());
    }

    @Override
    public Object getPrimaryKey() {
        return this.getFotoId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getDateipfad(), this.getTitel(), this.getBeschreibung());
    }

    @Override
    public Attribute[] setAttributeValues(final Attribute[] attributeArray) {
        final var oldAttributeArray = this.getAttributeArray();

        for (final var attribute : attributeArray) {
            final var name = attribute.getName();
            final var value = attribute.getValue();
            if (name.equals(Attributes.FOTO_ID.name()) && !value.equals(this.getFotoId())) {
                throw new IllegalArgumentException("Foto::setAttributeValues: Die Foto id darf nicht verändert werden!");
            }

            if (name.equals(Attributes.DATEIPFAD.name()) && !value.equals(this.getDateipfad())) {
                this.setDateipfad((Path) value);
            } else if (name.equals(Attributes.TITEL.name()) && !value.equals(this.getTitel())) {
                this.setTitel((String) value);
            } else if (name.equals(Attributes.BESCHREIBUNG.name()) && !value.equals(this.getBeschreibung())) {
                this.setBeschreibung((String) value);
            }
        }
        return oldAttributeArray;
    }

    @Override
    public String toString() {
        return "Foto{" +
                "titel='" + this.getTitel() +
                ", beschreibung='" + this.getBeschreibung() +
                ", dateipfad=" + this.getDateipfad() +
                '}';
    }
}
