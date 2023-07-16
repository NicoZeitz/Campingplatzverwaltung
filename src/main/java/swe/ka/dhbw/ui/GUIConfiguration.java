package swe.ka.dhbw.ui;

import de.dhbwka.swe.utils.event.EventCommand;
import de.dhbwka.swe.utils.event.GUIEvent;
import de.dhbwka.swe.utils.event.IGUIEventListener;
import de.dhbwka.swe.utils.event.UpdateEvent;
import de.dhbwka.swe.utils.gui.ButtonComponent;
import de.dhbwka.swe.utils.gui.ButtonElement;
import de.dhbwka.swe.utils.gui.ObservableComponent;
import swe.ka.dhbw.control.ReadonlyConfiguration;

import javax.swing.*;
import java.awt.*;

public class GUIConfiguration extends GUIComponent implements IGUIEventListener {
    public enum Commands implements EventCommand {
        OPEN_MAIN_GUI("GUIConfiguration.openMainGUI"),
        EXIT_APPLICATION("GUIConfiguration.exitApplication"),
        CONFIGURATION_ACCENT_COLOR("GUIConfiguration.configurationAccentColor", Color.class);

        public final Class<?> payloadType;
        public final String cmdText;

        Commands(final String cmdText) {
            this(cmdText, null);
        }

        Commands(final String cmdText, final Class<?> payloadType) {
            this.cmdText = cmdText;
            this.payloadType = payloadType;
        }

        @Override
        public String getCmdText() {
            return this.cmdText;
        }

        @Override
        public Class<?> getPayloadType() {
            return this.payloadType;
        }
    }

    private static final String BUTTON_COMPONENT_ID = "GUIConfiguration::BUTTON_COMPONENT_ID";
    private static final String BUTTON_START_APP_ID = "GUIConfiguration::BUTTON_START_APP_ID";
    private static final String BUTTON_EXIT_APP_ID = "GUIConfiguration::BUTTON_EXIT_APP_ID";
    private static final String ACCENT_COLOR_BUTTON_ELEMENT_ID = "GUIConfiguration::ACCENT_COLOR_BUTTON_ELEMENT_ID";

    public GUIConfiguration() {
        super();
        this.initUI();
    }

    @Override
    public void processGUIEvent(GUIEvent ge) {
        if (ge.getSource() instanceof ObservableComponent component) {
            final var id = component.getID();
            switch (id) {
                case ACCENT_COLOR_BUTTON_ELEMENT_ID -> {
                    final var button = (ButtonElement) component;
                    final var currentColor = button.getBackgroundColor();
                    final var nextColor = JColorChooser.showDialog(this, "Farbe auswählen", currentColor);
                    if (nextColor != null) {
                        button.setBackgroundColor(nextColor);
                        this.fireGUIEvent(new GUIEvent(this, Commands.CONFIGURATION_ACCENT_COLOR, nextColor));
                    }
                }
                case BUTTON_START_APP_ID -> this.fireGUIEvent(new GUIEvent(this, Commands.OPEN_MAIN_GUI));
                case BUTTON_EXIT_APP_ID -> this.fireGUIEvent(new GUIEvent(this, Commands.EXIT_APPLICATION));
            }
        }
    }

    @Override
    public void processUpdateEvent(UpdateEvent ue) {
        System.out.println(ue);
    }

    private void initUI() {
        // UNIMPLEMENTED:
        var configPanel = new JPanel();

        // FONT-FAMILY
        // FONT-SIZE
        // ACCENT-COLOR
        // DARK / LIGHT THEME

        //JColorChooser.showDialog(this, "Farbe auswählen", ReadonlyConfiguration.DEFAULT_ACCENT_COLOR);

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
                                .build(),
                        ButtonElement.builder(ACCENT_COLOR_BUTTON_ELEMENT_ID)
                                .buttonText(" ")
                                .backgroundColor(ReadonlyConfiguration.DEFAULT_ACCENT_COLOR)
                                .toolTip("Akzentfarbe auswählen")
                                .observer(this)
                                .build()
                })
                .position(ButtonComponent.Position.SOUTH)
                .orientation(ButtonComponent.Orientation.RIGHT)
                .build();
        this.setLayout(new GridLayout(1, 1));
        this.add(buttonComponent);
    }
}
