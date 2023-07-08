package swe.ka.dhbw.control;

import de.dhbwka.swe.utils.util.IOUtilities;
import swe.ka.dhbw.event.GUIConfigurationObserver;
import swe.ka.dhbw.event.GUIObserver;
import swe.ka.dhbw.ui.GUIConfiguration;

import java.util.HashSet;
import java.util.Set;

public class GUIController {
    private static GUIController instance;
    private final Set<GUIObserver> observers = new HashSet<>();
    private GUIConfiguration guiConfiguration;

    private GUIController() {
    }

    public static synchronized GUIController getInstance() {
        if (instance == null) {
            instance = new GUIController();
        }
        return instance;
    }

    public void showConfiguration() {
        final var observer = new GUIConfigurationObserver();
        this.observers.add(observer);
        this.guiConfiguration = new GUIConfiguration();
        this.guiConfiguration.addObserver(observer);
        IOUtilities.openInJFrame(new GUIConfiguration(), 400, 400, 0, 0, "Configuration", java.awt.Color.black, true);

    }

    public void exitApplication() {
        System.exit(0);
    }

    public void gatherConfigurationAndOpenMainGUI() {
        //this.guiConfiguration
    }
}
