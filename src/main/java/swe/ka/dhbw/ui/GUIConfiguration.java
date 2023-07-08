package swe.ka.dhbw.ui;

import de.dhbwka.swe.utils.event.GUIEvent;
import de.dhbwka.swe.utils.event.IGUIEventListener;
import de.dhbwka.swe.utils.event.UpdateEvent;
import de.dhbwka.swe.utils.gui.ButtonComponent;
import de.dhbwka.swe.utils.gui.ButtonElement;
import swe.ka.dhbw.control.GUIController;

import javax.swing.*;
import java.awt.*;

public class GUIConfiguration extends GUIComponent implements IGUIEventListener {
    private static final String BUTTON_COMPONENT_ID = "BUTTON_COMPONENT_ID";
    private static final String BUTTON_START_APP_ID = "BUTTON_START_APP_ID";
    private static final String BUTTON_EXIT_APP_ID = "BUTTON_EXIT_APP_ID";

    public GUIConfiguration() {
        super();
        this.initUI();
    }

    @Override
    public void processUpdateEvent(UpdateEvent ue) {
        System.out.println(ue);
    }

    private void initUI() {
        // UNIMPLEMENTED:
        var configPanel = new JPanel();

        var buttonComponent = ButtonComponent.builder(BUTTON_COMPONENT_ID)
                .embeddedComponent(configPanel)
                .title("Campingplatzverwaltung - Konfiguration")
                .buttonElements(new ButtonElement[] {
                        ButtonElement.builder(BUTTON_START_APP_ID)
                                .buttonText("Campingplatzverwaltung starten")
                                .observer(this)
                                .toolTip("Starte die App mit den eingegebenen Konfigurationen")
                                .build(),
                        ButtonElement.builder(BUTTON_EXIT_APP_ID)
                                .buttonText("App verlassen")
                                .observer(this)
                                .toolTip("Beendet die App")
                                .build()
                })
                .position(ButtonComponent.Position.SOUTH)
                .orientation(ButtonComponent.Orientation.RIGHT)
                .build();
        this.setLayout(new GridLayout(1, 1));
        this.add(buttonComponent);
    }

    @Override
    public void processGUIEvent(GUIEvent ge) {
        final var source = ge.getSource();
        if (!(source instanceof ButtonElement)) {
            return;
        }

        final var command = ((ButtonElement) source).getCommand();
        if (command != ButtonElement.Commands.BUTTON_PRESSED) {
            return;
        }

        final var id = ((ButtonElement) source).getID();
        if (id.equals(BUTTON_START_APP_ID)) {
            GUIController.getInstance().gatherConfigurationAndOpenMainGUI();
        } else if (id.equals(BUTTON_EXIT_APP_ID)) {
            GUIController.getInstance().exitApplication();
        }
    }
}
