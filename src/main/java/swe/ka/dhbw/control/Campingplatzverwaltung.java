package swe.ka.dhbw.control;

public final class Campingplatzverwaltung {
    private static Campingplatzverwaltung instance;

    private Campingplatzverwaltung() {}

    public static synchronized Campingplatzverwaltung getInstance() {
        if(instance == null) {
            instance = new Campingplatzverwaltung();
        }
        return instance;
    }

    public static void main(final String[] args) {
        var campingplatz = Campingplatzverwaltung.getInstance();
        campingplatz.startApplication();
    }

    public void startApplication() {
        System.out.println("Hello World");
    }
}
