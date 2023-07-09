package swe.ka.dhbw.model;

import de.dhbwka.swe.utils.model.ImageElement;

public class Foto {
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
        return dateipfad;
    }

    public void setDateipfad(String dateipfad) {
        this.dateipfad = dateipfad;
    }

    public String getTitel() {
        return titel;
    }

    public void setTitel(String titel) {
        this.titel = titel;
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }

    public ImageElement getImage() {
        return image;
    }

    public void setImage(ImageElement image) {
        this.image = image;
    }
}
