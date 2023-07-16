package swe.ka.dhbw.control;

import de.dhbwka.swe.utils.event.GUIEvent;
import de.dhbwka.swe.utils.event.IGUIEventListener;
import de.dhbwka.swe.utils.util.AppLogger;
import de.dhbwka.swe.utils.util.IOUtilities;
import swe.ka.dhbw.database.CSVDatenbasis;
import swe.ka.dhbw.database.EntityFactory;
import swe.ka.dhbw.database.EntityManager;
import swe.ka.dhbw.ui.GUIBuchung;
import swe.ka.dhbw.util.ArgumentParseException;
import swe.ka.dhbw.util.ArgumentParser;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;

public final class Campingplatzverwaltung {
    public static final String VERSION = "1.0.0";
    private static Campingplatzverwaltung instance;

    private Campingplatzverwaltung() {
    }

    public static synchronized Campingplatzverwaltung getInstance() {
        if (instance == null) {
            instance = new Campingplatzverwaltung();
        }
        return instance;
    }

    public static void main(final String[] args) throws IOException {
        try {
            final var arguments = ArgumentParser.parse(args);
            final var campingplatz = Campingplatzverwaltung.getInstance();
            campingplatz.startApplication(arguments);
        } catch (final ArgumentParseException argumentException) {
            ArgumentParser.printCommandLineArguments();
            AppLogger.getInstance().error(argumentException);
            System.exit(1);
        }
    }

    public void startApplication(final ArgumentParser.ArgumentsParseResult arguments) throws IOException {
        final var controller = GUIController.getInstance();
        final var entityManager = EntityManager.getInstance();
        final var entityFactory = EntityFactory.getInstance();
        final var dbPath = Path.of(arguments.dataPath()).toAbsolutePath().normalize();
        final var database = new CSVDatenbasis(dbPath);

        entityFactory.setDatabase(database);
        entityFactory.setEntityManager(entityManager);
        entityFactory.loadAllEntities();

        entityManager.find().forEach(entity -> AppLogger.getInstance().info(entity.toString()));

        controller.setEntityManager(entityManager);
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
