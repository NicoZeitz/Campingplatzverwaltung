package swe.ka.dhbw.control;

import de.dhbwka.swe.utils.event.GUIEvent;
import de.dhbwka.swe.utils.event.IGUIEventListener;
import de.dhbwka.swe.utils.util.IOUtilities;
import swe.ka.dhbw.ui.GUIBuchung;
import swe.ka.dhbw.util.ArgumentParser;

import java.awt.*;

public final class Campingplatzverwaltung {
    private static Campingplatzverwaltung instance;

    private Campingplatzverwaltung() {
    }

    public static synchronized Campingplatzverwaltung getInstance() {
        if (instance == null) {
            instance = new Campingplatzverwaltung();
        }
        return instance;
    }

    public static void main(final String[] args) {
        try {
            final var arguments = ArgumentParser.parse(args);
            final var campingplatz = Campingplatzverwaltung.getInstance();
            campingplatz.startApplication(arguments);
        } catch (final Exception argumentException) {
            ArgumentParser.printCommandLineArguments();
            System.out.println(argumentException);
            System.exit(1);
        }
    }

    public void startApplication(final ArgumentParser.ArgumentsParseResult arguments) {
        var controller = GUIController.getInstance();
        controller.showConfiguration();


        var config = Configuration.builder().build();
        System.out.println(config);
        System.out.println(arguments);
        var main = new GUIBuchung(config);
        main.addObserver(new IGUIEventListener() {
            @Override
            public void processGUIEvent(GUIEvent ge) {
                System.out.println(ge);
            }
        });
        IOUtilities.openInJFrame(main, 400, 400, 0, 0,
                "Buchung", Color.black, true
        );
    }
}
