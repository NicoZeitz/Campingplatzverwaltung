package swe.ka.dhbw.control;

import de.dhbwka.swe.utils.util.AppLogger;
import de.dhbwka.swe.utils.util.IAppLogger;
import de.dhbwka.swe.utils.util.PropertyManager;
import swe.ka.dhbw.database.CSVDatenbasis;
import swe.ka.dhbw.database.EntityFactory;
import swe.ka.dhbw.database.EntityManager;
import swe.ka.dhbw.util.ArgumentParseException;
import swe.ka.dhbw.util.ArgumentParser;

import java.io.IOException;
import java.nio.file.Path;

public final class Campingplatzverwaltung {
    public static final String VERSION = "1.0.0";
    private static Campingplatzverwaltung instance;
    private Configuration config;

    private Campingplatzverwaltung() {
    }

    public static synchronized Campingplatzverwaltung getInstance() {
        if (instance == null) {
            instance = new Campingplatzverwaltung();
        }
        return instance;
    }

    public Configuration getConfig() {
        return this.config;
    }

    public void setConfig(final Configuration config) {
        this.config = config;
    }

    public static void main(final String[] args) throws Exception {
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

    public void exitApplication() {
        try {
            this.config.saveConfiguration();
        } catch (IOException e) {
            AppLogger.getInstance().error(e);
        }
        System.exit(0);
    }

    public void startApplication(final ArgumentParser.ArgumentsParseResult arguments) throws Exception {
        final var propManager = new PropertyManager(arguments.propertiesPath(), Configuration.class, "/configuration.properties");
        final var controller = GUIController.getInstance();
        final var entityManager = EntityManager.getInstance();
        final var entityFactory = EntityFactory.getInstance();
        final var dbPath = Path.of(arguments.dataPath()).toAbsolutePath().normalize();
        final var database = new CSVDatenbasis(dbPath);

        AppLogger.getInstance().setSeverity(IAppLogger.Severity.INFO);
        
        entityFactory.setDatabase(database);
        entityFactory.setEntityManager(entityManager);
        entityFactory.loadAllEntities();
        controller.setDatabase(database);
        controller.setEntityManager(entityManager);
        controller.setApp(this);

        if (arguments.skipConfiguration()) {
            controller.openWindowMain(propManager);
        } else {
            controller.openWindowConfiguration(propManager);
        }
    }
}
