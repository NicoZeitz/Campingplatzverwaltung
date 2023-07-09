package swe.ka.dhbw.model;

import de.dhbwka.swe.utils.model.ICSVPersistable;
import de.dhbwka.swe.utils.model.IDepictable;
import de.dhbwka.swe.utils.model.IPersistable;
import de.dhbwka.swe.utils.model.ImageElement;
import swe.ka.dhbw.util.Validator;

// TODO: how are images loaded in the application
public class Foto implements IDepictable, ICSVPersistable, IPersistable {
    private final int fotoID;
    private String dateipfad;
    private String titel;
    private String beschreibung;
    private ImageElement image;

    public Foto(
            final int fotoID,
            final String dateipfad,
            final String titel,
            final String beschreibung,
            final ImageElement image
    ) {

        this.fotoID = fotoID;
        this.setDateipfad(dateipfad);
        this.setTitel(titel);
        this.setBeschreibung(beschreibung);
        this.setImage(image);
    }

    public String getDateipfad() {
        return this.dateipfad;
    }

    public void setDateipfad(final String dateipfad) {
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
}
