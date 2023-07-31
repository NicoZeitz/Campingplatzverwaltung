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

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class GUIConfiguration extends GUIComponent implements IGUIEventListener {
    // Commands
    public enum Commands implements EventCommand {
        // outgoing gui events
        BUTTON_PRESSED_OPEN_MAIN_GUI("GUIConfiguration::BUTTON_PRESSED_OPEN_MAIN_GUI"),
        BUTTON_PRESSED_EXIT_APPLICATION("GUIConfiguration::BUTTON_PRESSED_EXIT_APPLICATION"),
        CONFIGURATION_ACCENT_COLOR("GUIConfiguration::CONFIGURATION_ACCENT_COLOR", Color.class),
        CONFIGURATION_DARK_MODE("GUIConfiguration::CONFIGURATION_DARK_MODE", Boolean.class),
        CONFIGURATION_TEXT_FONT("GUIConfiguration::CONFIGURATION_TEXT_FONT", Font.class),
        // incoming update events
        REBUILD_UI("GUIConfiguration::REBUILD_UI", ReadonlyConfiguration.class);

        public final Class<?> payloadType;
        public final String cmdText;

        Commands(final String cmdText) {
            this(cmdText, Void.class);
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

    // UI IDs
    private static final String START_APP_BUTTON_ELEMENT_ID = "GUIConfiguration::START_APP_BUTTON_ELEMENT_ID";
    private static final String EXIT_APP_BUTTON_ELEMENT_ID = "GUIConfiguration::EXIT_APP_BUTTON_ELEMENT_ID";
    private static final String ACCENT_COLOR_BUTTON_ELEMENT_ID = "GUIConfiguration::ACCENT_COLOR_BUTTON_ELEMENT_ID";
    private static final String DARK_MODE_BUTTON_ELEMENT_ID = "GUIConfiguration::DARK_MODE_BUTTON_ELEMENT_ID";
    // Components
    private final List<JLabel> labels = new ArrayList<>();
    private ButtonElement appExitButton;
    private ButtonElement appStartButton;
    private ButtonElement accentColorButton;
    private ButtonElement darkModeButton;
    private ButtonComponent buttonContainer;
    private JTextPane welcomeText;
    private JComboBox<String> fontFamilyInput;
    private JSpinner fontSizeInput;

    public GUIConfiguration(final ReadonlyConfiguration config) {
        super("GUIConfiguration", config);
        this.initUI();
    }

    @Override
    public void processGUIEvent(GUIEvent guiEvent) {
        if (guiEvent.getSource() instanceof ObservableComponent component) {
            final var id = component.getID();
            switch (id) {
                case ACCENT_COLOR_BUTTON_ELEMENT_ID -> {
                    final var currentColor = this.accentColorButton.getBackgroundColor();
                    this.fireGUIEvent(new GUIEvent(this, Commands.CONFIGURATION_ACCENT_COLOR, currentColor));
                }
                case START_APP_BUTTON_ELEMENT_ID -> this.fireGUIEvent(new GUIEvent(this, Commands.BUTTON_PRESSED_OPEN_MAIN_GUI));
                case EXIT_APP_BUTTON_ELEMENT_ID -> this.fireGUIEvent(new GUIEvent(this, Commands.BUTTON_PRESSED_EXIT_APPLICATION));
                case DARK_MODE_BUTTON_ELEMENT_ID -> this.fireGUIEvent(new GUIEvent(
                        this,
                        Commands.CONFIGURATION_DARK_MODE,
                        ((ButtonElement) component).isSelected()
                ));
            }
        }
    }

    @Override
    public void processUpdateEvent(UpdateEvent updateEvent) {
        if (updateEvent.getCmd() == Commands.REBUILD_UI) {
            this.config = (ReadonlyConfiguration) updateEvent.getData();
            this.rebuildUI();
        }
    }

    public void rebuildUI() {
        UIManager.put("ToggleButton.select", this.config.getBackgroundColor());
        SwingUtilities.updateComponentTreeUI(this.darkModeButton);

        this.setBackground(this.config.getBackgroundColor());
        this.setForeground(this.config.getTextColor());

        this.fontFamilyInput.setFont(this.config.getLargeFont());
        this.fontFamilyInput.setForeground(this.config.getTextColor());
        this.fontFamilyInput.setBackground(this.config.getSecondaryBackgroundColor());

        this.fontSizeInput.setFont(this.config.getLargeFont());
        this.fontSizeInput.setForeground(this.config.getTextColor());
        this.fontSizeInput.setBackground(this.config.getSecondaryBackgroundColor());
        this.fontSizeInput.getEditor().getComponent(0).setFont(this.config.getLargeFont());
        this.fontSizeInput.getEditor().getComponent(0).setForeground(this.config.getTextColor());
        this.fontSizeInput.getEditor().getComponent(0).setBackground(this.config.getSecondaryBackgroundColor());

        this.accentColorButton.setFont(this.config.getHeaderFont());
        this.accentColorButton.setTextColor(this.config.getTextColor());
        this.accentColorButton.setBackgroundColor(this.config.getAccentColor());

        this.darkModeButton.setFont(this.config.getHeaderFont());
        this.darkModeButton.setTextColor(this.config.getTextColor());
        this.darkModeButton.setBackgroundColor(this.config.getBackgroundColor());
        this.darkModeButton.setBorder(BorderFactory.createLineBorder(this.config.getTextColor()));
        if (this.darkModeButton.isSelected()) {
            this.darkModeButton.setButtonText("Dunkler Modus");
        } else {
            this.darkModeButton.setButtonText("Heller Modus");
        }

        this.appExitButton.setFont(this.config.getHeaderFont());
        this.appExitButton.setTextColor(this.config.getTextColor());
        this.appExitButton.setBackgroundColor(this.config.getBackgroundColor());
        this.appStartButton.setFont(this.config.getHeaderFont());
        this.appStartButton.setTextColor(this.config.getTextColor());
        this.appStartButton.setBackgroundColor(this.config.getAccentColor());

        this.buttonContainer.setForeground(this.config.getTextColor());
        this.buttonContainer.setBackground(this.config.getBackgroundColor());
        this.buttonContainer.getComponent(1).setBackground(this.config.getBackgroundColor());
        this.buttonContainer.getComponent(1).setForeground(this.config.getTextColor());

        final var border = BorderFactory.createTitledBorder("Campingplatzverwaltung - Konfiguration");
        border.setTitleColor(this.config.getTextColor());
        border.setTitleFont(this.config.getHeaderFont());
        this.buttonContainer.setBorder(border);

        for (final var label : this.labels) {
            label.setForeground(this.config.getTextColor());
            label.setBackground(this.config.getBackgroundColor());
            label.setFont(this.config.getLargeFont());
            label.setOpaque(true);
        }

        this.welcomeText.setForeground(this.config.getTextColor());
        this.welcomeText.setBackground(this.config.getBackgroundColor());
        var doc = this.welcomeText.getStyledDocument();
        var styles = new SimpleAttributeSet();
        StyleConstants.setAlignment(styles, StyleConstants.ALIGN_CENTER);
        StyleConstants.setFontFamily(styles, this.config.getFontFamily());
        StyleConstants.setFontSize(styles, this.config.getLargeFont().getSize());
        StyleConstants.setBackground(styles, this.config.getBackgroundColor());
        StyleConstants.setForeground(styles, this.config.getTextColor());
        doc.setParagraphAttributes(0, doc.getLength(), styles, false);

        Optional.ofNullable(SwingUtilities.getWindowAncestor(this)).ifPresent(w -> {
            w.setBackground(this.config.getBackgroundColor());
            w.setForeground(this.config.getTextColor());
            w.revalidate();
            w.repaint();
        });
    }

    private void initUI() {
        final var configPanel = new JPanel();
        configPanel.setLayout(new GridLayout(1, 2));
        configPanel.setOpaque(true);
        configPanel.setBackground(null);

        // Left Side (Configuration)
        final var leftSide = new JPanel();
        leftSide.setLayout(new GridBagLayout());
        leftSide.setBackground(null);
        leftSide.setOpaque(true);
        configPanel.add(leftSide);

        // Font Family
        final var fontFamilyLabel = new JLabel("Schriftart");
        this.labels.add(fontFamilyLabel);
        this.fontFamilyInput = new JComboBox<>(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
        this.fontFamilyInput.setSelectedItem(this.config.getFontFamily());
        this.fontFamilyInput.addActionListener(e -> {
            final var selectedFontFamily = (String) this.fontFamilyInput.getSelectedItem();
            final var selectedFont = new Font(selectedFontFamily, Font.PLAIN, this.config.getFontSize());
            this.fireGUIEvent(new GUIEvent(this, Commands.CONFIGURATION_TEXT_FONT, selectedFont));
        });

        // Font Size
        final var fontSizeLabel = new JLabel("Schriftgröße");
        this.labels.add(fontSizeLabel);
        this.fontSizeInput = new JSpinner(new SpinnerNumberModel(this.config.getFontSize(), 1, 100, 1));
        this.fontSizeInput.setFont(this.config.getLargeFont());
        this.fontSizeInput.addChangeListener(e -> {
            final var selectedFontSize = (int) this.fontSizeInput.getValue();
            final var selectedFont = new Font(this.config.getFontFamily(), Font.PLAIN, selectedFontSize);
            this.fireGUIEvent(new GUIEvent(this, Commands.CONFIGURATION_TEXT_FONT, selectedFont));
        });

        // Accent Color
        final var accentColorLabel = new JLabel("Akzentfarbe");
        this.labels.add(accentColorLabel);
        this.accentColorButton = ButtonElement.builder(ACCENT_COLOR_BUTTON_ELEMENT_ID)
                .buttonText(" ")
                .backgroundColor(this.config.getAccentColor())
                .toolTip("Akzentfarbe auswählen")
                .build();
        this.accentColorButton.setBorder(BorderFactory.createEmptyBorder());
        this.accentColorButton.addObserver(this);

        // Dark / Light Mode
        final var darkModeLabel = new JLabel("Dunkler Modus");
        this.labels.add(darkModeLabel);
        this.darkModeButton = ButtonElement.builder(DARK_MODE_BUTTON_ELEMENT_ID)
                .buttonText("Heller Modus")
                .type(ButtonElement.Type.TOGGLE_BUTTON)
                .toolTip("Zwischen hellem und dunklem Modus wechseln")
                .build();
        this.darkModeButton.addObserver(this);

        // @formatter:off
        leftSide.add(fontFamilyLabel,        new GridBagConstraints(1, 1, 1, 1, 1d, 0d, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 10, 10), 0, 0));
        leftSide.add(this.fontFamilyInput,   new GridBagConstraints(2, 1, 1, 1, 1d, 0d, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 10, 10), 0, 0));
        leftSide.add(fontSizeLabel,          new GridBagConstraints(1, 2, 1, 1, 1d, 0d, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 10, 10), 0, 0));
        leftSide.add(this.fontSizeInput,     new GridBagConstraints(2, 2, 1, 1, 1d, 0d, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 10, 10), 0, 0));
        leftSide.add(accentColorLabel,       new GridBagConstraints(1, 3, 1, 1, 1d, 0d, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 10, 10), 0, 0));
        leftSide.add(this.accentColorButton, new GridBagConstraints(2, 3, 1, 1, 1d, 0d, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 10, 10), 0, 0));
        leftSide.add(darkModeLabel,          new GridBagConstraints(1, 4, 1, 1, 1d, 0d, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 10, 10), 0, 0));
        leftSide.add(this.darkModeButton,    new GridBagConstraints(2, 4, 1, 1, 1d, 0d, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 10, 10), 0, 0));
        // @formatter:on

        // Right Side (Welcome Messages)
        final var rightSide = new JPanel();
        rightSide.setLayout(new GridBagLayout());
        rightSide.setBackground(null);
        rightSide.setOpaque(true);
        configPanel.add(rightSide);

        // @formatter:off - Logo Image
        try {
            final var logo = ImageIO.read(Objects.requireNonNull(this.getClass().getResourceAsStream("/Logo.png")));
            final var logoLabel = new JLabel(new ImageIcon(logo.getScaledInstance(201, 141, Image.SCALE_SMOOTH)));
            rightSide.add(logoLabel, new GridBagConstraints(1, 1, 1, 1, 1d, 1d, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        } catch (IOException e) { /* Ignore Errors */ }
        // @formatter:on

        // @formatter:off - Welcome Message
        this.welcomeText = new JTextPane();
        this.welcomeText.setText("Willkommen zur Campingplatzverwaltungssoftware von Wolf & Zeitz");
        this.welcomeText.setEditable(false);
        this.welcomeText.setOpaque(true);
        rightSide.add(this.welcomeText, new GridBagConstraints(1, 2, 1, 1, 1d, 1d, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 10, 10, 10), 0, 0));
        // @formatter:on

        // Buttons to Start and Exit the Application
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

        this.buttonContainer = ButtonComponent.builder(super.generateRandomID())
                .embeddedComponent(configPanel)
                .buttonElements(new ButtonElement[] {this.appExitButton, this.appStartButton})
                .position(ButtonComponent.Position.SOUTH)
                .orientation(ButtonComponent.Orientation.RIGHT)
                .build();

        this.setLayout(new GridLayout(1, 1));
        this.add(this.buttonContainer);
        this.setOpaque(true);
        this.rebuildUI();
    }
}
