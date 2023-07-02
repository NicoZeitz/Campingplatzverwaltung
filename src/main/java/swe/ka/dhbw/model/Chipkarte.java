package swe.ka.dhbw.model;

public class Chipkarte {

    private int nummer;
    private Status status;

    public enum Status{
        VERFUEGBAR,
        IN_VERWENDUNG
    }

}
