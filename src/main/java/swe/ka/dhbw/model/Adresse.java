package swe.ka.dhbw.model;

public class Adresse {

    private String strasse;
    private int hausnummer;
    private String zusatz;
    private String ort;
    private String plz;
    private String land;

    public enum Land{
        DE, US, NL, GB, PL, CA, BE, AT, SE, NO, FI, FR, CH
    }

}
