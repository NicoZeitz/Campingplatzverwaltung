package swe.ka.dhbw.control;

import swe.ka.dhbw.util.ArgumentParser;

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
        try {
            final var arguments = ArgumentParser.parse(args);
            final var campingplatz = Campingplatzverwaltung.getInstance();
            campingplatz.startApplication(arguments);
            ArgumentParser.printCommandLineArguments();
        } catch(final Exception argumentException) {
            ArgumentParser.printCommandLineArguments();
            System.out.println(argumentException);
            System.exit(1);
        }
    }

    public void startApplication(final ArgumentParser.ArgumentsParseResult arguments) {
        System.out.println(arguments);
    }
}
