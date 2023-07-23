package swe.ka.dhbw.event;

import de.dhbwka.swe.utils.event.GUIEvent;
import de.dhbwka.swe.utils.event.IGUIEventListener;
import swe.ka.dhbw.control.GUIController;
import swe.ka.dhbw.ui.GUIConfiguration;

import javax.swing.*;
import java.awt.*;

public class GUIConfigurationObserver implements IGUIEventListener {
    @Override
    public void processGUIEvent(GUIEvent ge) {
        if (ge.getCmd() == GUIConfiguration.Commands.OPEN_MAIN_GUI) {
            SwingUtilities.invokeLater(() -> GUIController.getInstance().openWindowMain());
        } else if (ge.getCmd() == GUIConfiguration.Commands.EXIT_APPLICATION) {
            GUIController.getInstance().exitApplication();
        } else if (ge.getCmd() == GUIConfiguration.Commands.CONFIGURATION_ACCENT_COLOR) {
            GUIController.getInstance().handleWindowConfigurationSetAccentColor((Color) ge.getData());
        } else if (ge.getCmd() == GUIConfiguration.Commands.CONFIGURATION_DARK_MODE) {
            GUIController.getInstance().handleWindowConfigurationSetDarkMode((Boolean) ge.getData());
        } else if (ge.getCmd() == GUIConfiguration.Commands.CONFIGURATION_TEXT_FONT) {
            GUIController.getInstance().handleWindowConfigurationSetTextFont((Font) ge.getData());
        }
    }
}
