package swe.ka.dhbw.ui;

import de.dhbwka.swe.utils.event.EventCommand;
import de.dhbwka.swe.utils.event.GUIEvent;
import de.dhbwka.swe.utils.event.IGUIEventListener;
import de.dhbwka.swe.utils.event.UpdateEvent;
import de.dhbwka.swe.utils.gui.ButtonComponent;
import de.dhbwka.swe.utils.gui.ButtonElement;
import de.dhbwka.swe.utils.gui.GUIConstants;
import de.dhbwka.swe.utils.gui.ObservableComponent;
import swe.ka.dhbw.control.ReadonlyConfiguration;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GUIConfiguration extends GUIComponent implements IGUIEventListener {
    public enum Commands implements EventCommand {
        OPEN_MAIN_GUI("GUIConfiguration.openMainGUI"),
        EXIT_APPLICATION("GUIConfiguration.exitApplication"),
        CONFIGURATION_ACCENT_COLOR("GUIConfiguration.configurationAccentColor", Color.class),
        CONFIGURATION_DARK_MODE("GUIConfiguration.configurationDarkMode", Boolean.class),
        REBUILD_UI("GUIConfiguration.rebuildUI", ReadonlyConfiguration.class);

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
    private static final String START_APP_BUTTON_ELEMENT_ID = "GUIConfiguration::START_APP_BUTTON_ELEMENT_ID";
    private static final String EXIT_APP_BUTTON_ELEMENT_ID = "GUIConfiguration::EXIT_APP_BUTTON_ELEMENT_ID";
    private static final String ACCENT_COLOR_BUTTON_ELEMENT_ID = "GUIConfiguration::ACCENT_COLOR_BUTTON_ELEMENT_ID";
    private static final String DARK_MODE_BUTTON_ELEMENT_ID = "GUIConfiguration::DARK_MODE_BUTTON_ELEMENT_ID";
    private ReadonlyConfiguration config;
    private JPanel configPanel;
    private ButtonElement appExitButton;
    private ButtonElement appStartButton;
    private ButtonElement accentColorButton;
    private ButtonComponent buttonComponent;
    private List<JLabel> labels = new ArrayList<>();

    public GUIConfiguration(final ReadonlyConfiguration config) {
        super("GUIConfiguration");
        this.config = config;
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
                case START_APP_BUTTON_ELEMENT_ID -> this.fireGUIEvent(new GUIEvent(this, Commands.OPEN_MAIN_GUI));
                case EXIT_APP_BUTTON_ELEMENT_ID -> this.fireGUIEvent(new GUIEvent(this, Commands.EXIT_APPLICATION));
                case DARK_MODE_BUTTON_ELEMENT_ID ->
                        this.fireGUIEvent(new GUIEvent(this, Commands.CONFIGURATION_DARK_MODE, ((ButtonElement) component).isSelected()));
            }
        }
    }

    @Override
    public void processUpdateEvent(UpdateEvent ue) {
        if (ue.getCmd() == Commands.REBUILD_UI) {
            this.config = (ReadonlyConfiguration) ue.getData();
            this.rebuildUI();
        }
    }

    public void rebuildUI() {
        this.setBackground(this.config.getBackgroundColor());
        this.setForeground(this.config.getTextColor());

        this.configPanel.setForeground(this.config.getTextColor());
        this.configPanel.setBackground(this.config.getBackgroundColor());

        this.accentColorButton.setFont(this.config.getHeaderFont());
        this.accentColorButton.setTextColor(this.config.getTextColor());
        this.accentColorButton.setBackgroundColor(this.config.getAccentColor());

        this.appExitButton.setFont(this.config.getHeaderFont());
        this.appExitButton.setTextColor(this.config.getTextColor());
        this.appExitButton.setBackgroundColor(this.config.getBackgroundColor());
        this.appStartButton.setFont(this.config.getHeaderFont());
        this.appStartButton.setTextColor(this.config.getTextColor());
        this.appStartButton.setBackgroundColor(this.config.getAccentColor());

        this.buttonComponent.setForeground(this.config.getTextColor());
        this.buttonComponent.setBackground(this.config.getBackgroundColor());
        this.buttonComponent.getComponents()[1].setBackground(this.config.getBackgroundColor());
        this.buttonComponent.getComponents()[1].setForeground(this.config.getTextColor());

        final var border = BorderFactory.createTitledBorder("Campingplatzverwaltung - Konfiguration");
        border.setTitleColor(this.config.getTextColor());
        border.setTitleFont(this.config.getHeaderFont());

        for (final var label : this.labels) {
            label.setForeground(this.config.getTextColor());
            label.setBackground(this.config.getBackgroundColor());
            label.setFont(this.config.getLargeFont());
            label.setOpaque(true);
        }

        this.buttonComponent.setBorder(border);
        this.repaint();
    }

    private void initUI() {
        // UNIMPLEMENTED:
        this.configPanel = new JPanel();
        this.configPanel.setLayout(new GridBagLayout());
        this.configPanel.setOpaque(true);

        // FONT-FAMILY
        // FONT-SIZE

        // Accent Color
        final var accentColorLabel = new JLabel("Akzentfarbe");
        this.labels.add(accentColorLabel);
        this.configPanel.add(accentColorLabel);

        this.accentColorButton = ButtonElement.builder(ACCENT_COLOR_BUTTON_ELEMENT_ID)
                .buttonText(" ")
                .backgroundColor(this.config.getAccentColor())
                .toolTip("Akzentfarbe auswählen")
                .build();
        this.accentColorButton.setBorder(BorderFactory.createEmptyBorder());
        this.accentColorButton.addObserver(this);
        this.configPanel.add(this.accentColorButton);

        // Dark / Light Mode
        final var darkModeLabel = new JLabel("Dunkler Modus");
        this.labels.add(darkModeLabel);
        this.configPanel.add(darkModeLabel);

        final var checkBox = ButtonElement.builder(DARK_MODE_BUTTON_ELEMENT_ID)
                .buttonText("Dunkler Modus")
                .type(ButtonElement.Type.CHECKBOX)
                .toolTip("Zwischen hellem und dunklem Modus wechseln")
                .build();
        checkBox.addObserver(this);
        this.configPanel.add(checkBox);
        
        // Buttons zum Starten und Beenden der App
        this.appExitButton = ButtonElement.builder(EXIT_APP_BUTTON_ELEMENT_ID)
                .buttonText("App verlassen")
                .componentSize(new Dimension(150, GUIConstants.IntSizes.DEFAULT_BUTTON_HEIGHT.getValue()))
                .toolTip("Beendet die App")
                .build();
        this.appExitButton.addObserver(this);

        this.appStartButton = ButtonElement.builder(START_APP_BUTTON_ELEMENT_ID)
                .buttonText("App starten")
                .componentSize(new Dimension(150, GUIConstants.IntSizes.DEFAULT_BUTTON_HEIGHT.getValue()))
                .toolTip("Startet die Campingplatzverwaltung mit den eingegebenen Konfigurationen")
                .build();
        this.appStartButton.setBorder(BorderFactory.createEmptyBorder());
        this.appStartButton.addObserver(this);

        this.buttonComponent = ButtonComponent.builder(BUTTON_COMPONENT_ID)
                .embeddedComponent(configPanel)
                .buttonElements(new ButtonElement[] {this.appExitButton, this.appStartButton})
                .position(ButtonComponent.Position.SOUTH)
                .orientation(ButtonComponent.Orientation.RIGHT)
                .build();

        this.setLayout(new GridLayout(1, 1));
        this.add(this.buttonComponent);
        this.setOpaque(true);
        this.rebuildUI();
    }
}
