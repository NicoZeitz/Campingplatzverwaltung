package swe.ka.dhbw.ui.components;

import de.dhbwka.swe.utils.event.EventCommand;
import de.dhbwka.swe.utils.event.GUIEvent;
import de.dhbwka.swe.utils.event.IGUIEventListener;
import de.dhbwka.swe.utils.event.UpdateEvent;
import de.dhbwka.swe.utils.gui.ButtonComponent;
import de.dhbwka.swe.utils.gui.ButtonElement;
import de.dhbwka.swe.utils.gui.GUIConstants;
import de.dhbwka.swe.utils.gui.ObservableComponent;
import de.dhbwka.swe.utils.model.IDepictable;
import swe.ka.dhbw.control.ReadonlyConfiguration;
import swe.ka.dhbw.ui.GUIComponent;

import javax.swing.*;
import java.awt.*;

public class BookingCreateComponent extends GUIComponent implements IGUIEventListener {
    public enum Commands implements EventCommand {
        ADD_GUEST("BookingCreateComponent.addGuest"),
        ADD_SERVICE("BookingCreateComponent.addService"),
        ADD_EQUIPMENT("BookingCreateComponent.addEquipment");

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

    private static final String BUTTON_COMPONENT_ID = "BookingCreateComponent::BUTTON_COMPONENT_ID";
    private static final String CREATE_BOOKING_BUTTON_ELEMENT_ID = "BookingCreateComponent::CREATE_BOOKING_BUTTON_ELEMENT_ID";
    private static final String CANCEL_CREATE_BOOKING_BUTTON_ELEMENT_ID = "BookingCreateComponent::CANCEL_CREATE_BOOKING_BUTTON_ELEMENT_ID";
    private static final String ADD_GAST_BUTTON_ELEMENT_ID = "BookingCreateComponent::ADD_GAST_BUTTON_ELEMENT_ID";
    private static final String ADD_LEISTUNG_BUTTON_ELEMENT_ID = "BookingCreateComponent::ADD_LEISTUNG_BUTTON_ELEMENT_ID";
    private static final String ADD_AUSRUESTUNG_BUTTON_ELEMENT_ID = "BookingCreateComponent::ADD_AUSRUESTUNG_BUTTON_ELEMENT_ID";

    public BookingCreateComponent(final ReadonlyConfiguration config, final IDepictable depictable) {
        super("BookingCreateComponent", config);
        this.initUI();
    }

    @Override
    public void processGUIEvent(final GUIEvent ge) {
        if (ge.getSource() instanceof ObservableComponent component) {
            final var id = component.getID();
            switch (id) {
                case ADD_GAST_BUTTON_ELEMENT_ID -> this.fireGUIEvent(new GUIEvent(this, Commands.ADD_GUEST));
                case ADD_LEISTUNG_BUTTON_ELEMENT_ID -> this.fireGUIEvent(new GUIEvent(this, Commands.ADD_SERVICE));
                case ADD_AUSRUESTUNG_BUTTON_ELEMENT_ID -> this.fireGUIEvent(new GUIEvent(this, Commands.ADD_EQUIPMENT));
            }
        }
    }

    @Override
    public void processUpdateEvent(final UpdateEvent ue) {

    }

    private JComponent createAusruestungTable() {
        return new JPanel();
    }

    private JComponent createBuchungszeitraum() {
        return new JPanel();
    }

    private JComponent createChipkartenAuswahl() {
        return new JPanel();
    }

    private JComponent createGastTable() {
        return new JPanel();
    }

    private JComponent createLeistungTable() {
        return new JPanel();
    }

    private JComponent createStellplatzauswahl() {
        return new JPanel();
    }


    private void initUI() {
        this.setLayout(new GridLayout(1, 1));
        this.setOpaque(true);
        this.setBackground(this.config.getBackgroundColor());
        this.setForeground(this.config.getTextColor());

        final var mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(1, 2));
        mainPanel.setOpaque(true);
        mainPanel.setBackground(this.config.getBackgroundColor());
        mainPanel.setForeground(this.config.getTextColor());
        final var leftPanel = new JPanel();
        leftPanel.setLayout(new GridBagLayout());
        leftPanel.setOpaque(true);
        leftPanel.setBackground(this.config.getBackgroundColor());
        leftPanel.setForeground(this.config.getTextColor());
        mainPanel.add(leftPanel);
        final var rightPanel = new JPanel();
        rightPanel.setLayout(new GridBagLayout());
        rightPanel.setOpaque(true);
        rightPanel.setBackground(this.config.getBackgroundColor());
        rightPanel.setForeground(this.config.getTextColor());
        mainPanel.add(rightPanel);


        // @formatter:off
        leftPanel.add(super.createAddableWrapper(
                "Gäste auswählen",
                super.generateRandomID(),
                ADD_GAST_BUTTON_ELEMENT_ID,
                "Fügt einen neuen Gast hinzu",
                this.createGastTable()
        ), new GridBagConstraints(1, 1, 1, 1, 1d, 0d, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        leftPanel.add(super.createAddableWrapper(
                "Leistungen auswählen",
                super.generateRandomID(),
                ADD_LEISTUNG_BUTTON_ELEMENT_ID,
                "Fügt eine neue gebuchte Leistung hinzu",
                this.createLeistungTable()
        ), new GridBagConstraints(1, 2, 1, 1, 1d, 0d, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        leftPanel.add(super.createAddableWrapper(
                "Mitgebrachte Ausrüstung auswählen",
                super.generateRandomID(),
                ADD_AUSRUESTUNG_BUTTON_ELEMENT_ID,
                "Fügt eine neue mitgebrachte Ausrüstung hinzu",
                this.createAusruestungTable()
        ), new GridBagConstraints(1, 3, 1, 1, 1d, 0d, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        leftPanel.add(super.createFillComponent(), new GridBagConstraints(1, 4, 1, 1, 1d, 1d, GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));

        rightPanel.add(super.createWrapper("Buchungszeitraum", this.createBuchungszeitraum()), new GridBagConstraints(1, 1, 1, 1, 1d, 0d, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        rightPanel.add(super.createWrapper("Stellplatzauswahl", this.createStellplatzauswahl()), new GridBagConstraints(1, 2, 1, 1, 1d, 0d, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        rightPanel.add(super.createWrapper("Chipkartenauswahl", this.createChipkartenAuswahl()), new GridBagConstraints(1, 3, 1, 1, 1d, 0d, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        rightPanel.add(super.createFillComponent(), new GridBagConstraints(1, 4, 1, 1, 1d, 1d, GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
        // @formatter:on

        final var cancelButton = ButtonElement.builder(CANCEL_CREATE_BOOKING_BUTTON_ELEMENT_ID)
                .buttonText("Erstellen abbrechen")
                .font(this.config.getFont())
                .backgroundColor(this.config.getBackgroundColor())
                .textColor(this.config.getTextColor())
                .componentSize(new Dimension(150, GUIConstants.IntSizes.DEFAULT_BUTTON_HEIGHT.getValue()))
                .toolTip("Bricht den Vorgang ab")
                .build();
        cancelButton.addObserver(this);

        final var createButton = ButtonElement.builder(CREATE_BOOKING_BUTTON_ELEMENT_ID)
                .buttonText("Buchung erstellen")
                .font(this.config.getFont())
                .backgroundColor(this.config.getAccentColor())
                .textColor(this.config.getTextColor())
                .componentSize(new Dimension(150, GUIConstants.IntSizes.DEFAULT_BUTTON_HEIGHT.getValue()))
                .toolTip("Erstellt die neue Buchung mit den eingegebenen Daten")
                .build();
        createButton.addObserver(this);

        final var buttonComponent = ButtonComponent.builder(BUTTON_COMPONENT_ID)
                .embeddedComponent(mainPanel)
                .buttonElements(new ButtonElement[] {cancelButton, createButton})
                .position(ButtonComponent.Position.SOUTH)
                .orientation(ButtonComponent.Orientation.RIGHT)
                .build();

        // TODO: why is the border background blue???
        final var border = BorderFactory.createTitledBorder("Neue Buchung anlegen");
        border.setTitleColor(this.config.getTextColor());
        border.setTitleFont(this.config.getHeaderFont());
        buttonComponent.setForeground(this.config.getTextColor());
        buttonComponent.setBackground(this.config.getBackgroundColor());
        buttonComponent.getComponents()[1].setBackground(this.config.getBackgroundColor());
        buttonComponent.getComponents()[1].setForeground(this.config.getTextColor());
        buttonComponent.setBorder(border);

        this.add(buttonComponent);
    }
}
