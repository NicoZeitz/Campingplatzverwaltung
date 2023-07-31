package swe.ka.dhbw.event;

import de.dhbwka.swe.utils.event.GUIEvent;
import de.dhbwka.swe.utils.event.IGUIEventListener;
import swe.ka.dhbw.control.GUIController;
import swe.ka.dhbw.ui.GUIConfiguration;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;

public class GUIConfigurationObserver implements IGUIEventListener {
    @Override
    public void processGUIEvent(GUIEvent guiEvent) {
        final var controller = GUIController.getInstance();
        if (guiEvent.getCmd() instanceof GUIConfiguration.Commands command) {
            switch (command) {
                case BUTTON_PRESSED_OPEN_MAIN_GUI -> SwingUtilities.invokeLater(controller::openWindowMain);
                case BUTTON_PRESSED_EXIT_APPLICATION -> {
                    final var window = SwingUtilities.getWindowAncestor((Component) guiEvent.getSource());
                    window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
                }
                case CONFIGURATION_ACCENT_COLOR -> controller.handleWindowConfigurationSetAccentColor((Color) guiEvent.getData());
                case CONFIGURATION_DARK_MODE -> controller.handleWindowConfigurationSetDarkMode((Boolean) guiEvent.getData());
                case CONFIGURATION_TEXT_FONT -> controller.handleWindowConfigurationSetTextFont((Font) guiEvent.getData());
            }
        }
    }
}
