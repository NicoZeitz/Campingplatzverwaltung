package swe.ka.dhbw.model;

public class Chipkarte {

    private int nummer;
    private Status status;

    public int getNummer() {
        return nummer;
    }

    public void setNummer(int nummer) {
        this.nummer = nummer;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public enum Status {
        VERFUEGBAR, IN_VERWENDUNG
    }
}
