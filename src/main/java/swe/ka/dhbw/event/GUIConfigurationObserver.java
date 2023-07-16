package swe.ka.dhbw.event;

import de.dhbwka.swe.utils.event.GUIEvent;
import de.dhbwka.swe.utils.event.IGUIEventListener;
import swe.ka.dhbw.control.GUIController;
import swe.ka.dhbw.ui.GUIConfiguration;

import javax.swing.*;
import java.util.Optional;

public class GUIConfigurationObserver implements IGUIEventListener {
    @Override
    public void processGUIEvent(GUIEvent ge) {
        if (ge.getCmd() == GUIConfiguration.Commands.OPEN_MAIN_GUI) {
            SwingUtilities.invokeLater(() -> GUIController.getInstance().openGUIMain(Optional.empty()));
        } else if (ge.getCmd() == GUIConfiguration.Commands.EXIT_APPLICATION) {
            GUIController.getInstance().exitApplication();
        } else if (ge.getCmd() == GUIConfiguration.Commands.CONFIGURATION_ACCENT_COLOR) {
            // GUIController.getInstance().configurationAccentColor((Color) ge.getData());
        } else {
            LogObserver.logGUIEvent(ge);
        }
    }
}
