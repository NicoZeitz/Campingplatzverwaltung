package swe.ka.dhbw.ui.components;

import de.dhbwka.swe.utils.event.EventCommand;
import de.dhbwka.swe.utils.event.GUIEvent;
import de.dhbwka.swe.utils.event.IGUIEventListener;
import de.dhbwka.swe.utils.event.UpdateEvent;
import de.dhbwka.swe.utils.gui.*;
import de.dhbwka.swe.utils.model.IDepictable;
import swe.ka.dhbw.control.Payload;
import swe.ka.dhbw.control.ReadonlyConfiguration;
import swe.ka.dhbw.ui.GUIComponent;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ServiceSelectorComponent extends GUIComponent implements IGUIEventListener {
    public enum Mode {
        EDIT,
        CREATE
    }

    public enum Commands implements EventCommand {
        // outgoing gui events
        BUTTON_PRESSED_SELECT_START_DATE("ServiceSelectorComponent::BUTTON_PRESSED_SELECT_START_DATE", LocalDate.class),
        BUTTON_PRESSED_SELECT_END_DATE("ServiceSelectorComponent::BUTTON_PRESSED_SELECT_END_DATE", LocalDate.class),
        BUTTON_PRESSED_SAVE("ServiceSelectorComponent::BUTTON_PRESSED_SAVE", Payload.ServiceCreation.class),
        BUTTON_PRESSED_CANCEL("ServiceSelectorComponent::BUTTON_PRESSED_CANCEL"),
        // incoming update events
        UPDATE_SERVICE_TYPES("ServiceSelectorComponent::UPDATE_SERVICE_TYPES", List.class),
        SET_START_DATE("ServiceSelectorComponent::SET_START_DATE", LocalDate.class),
        SET_END_DATE("ServiceSelectorComponent::SET_END_DATE", LocalDate.class),
        SET_SELECTED_SERVICE_TYPE("ServiceSelectorComponent::SET_SELECTED_SERVICE_TYPE", Object.class),
        SET_MODE("ServiceSelectorComponent::SET_MODE", Mode.class);

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
    private static final String START_DATE_ELEMENT_ID = "ServiceSelectorComponent::START_DATE_ELEMENT_ID";
    private static final String END_DATE_ELEMENT_ID = "ServiceSelectorComponent::END_DATE_ELEMENT_ID";
    private static final String CANCEL_BUTTON_ELEMENT_ID = "ServiceSelectorComponent::CANCEL_BUTTON_ELEMENT_ID";
    private static final String SAVE_BUTTON_ELEMENT_ID = "ServiceSelectorComponent::SAVE_BUTTON_ELEMENT_ID";

    // Data
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.GERMANY);
    private List<? extends IDepictable> serviceTypes = new ArrayList<>();
    private Mode mode = Mode.CREATE;

    // Components
    private ButtonComponent container;
    private AttributeElement serviceTypeElement;
    private AttributeElement startDateElement;
    private AttributeElement endDateElement;

    public ServiceSelectorComponent(final ReadonlyConfiguration config) {
        super("ServiceSelectorComponent", config);
        this.initUI();
    }

    @Override
    public void processGUIEvent(final GUIEvent guiEvent) {
        if (guiEvent.getSource() instanceof ObservableComponent component) {
            final var id = component.getID();
            switch (id) {
                case START_DATE_ELEMENT_ID -> {
                    if (guiEvent.getCmd() == AttributeElement.Commands.BUTTON_PRESSED) {
                        final var startDate = tryOptional(() -> LocalDate.parse(this.startDateElement.getValueAsString(), this.dateTimeFormatter));
                        this.fireGUIEvent(new GUIEvent(this, Commands.BUTTON_PRESSED_SELECT_START_DATE, startDate));
                    }
                }
                case END_DATE_ELEMENT_ID -> {
                    if (guiEvent.getCmd() == AttributeElement.Commands.BUTTON_PRESSED) {
                        final var endDate = tryOptional(() -> LocalDate.parse(this.endDateElement.getValueAsString(), this.dateTimeFormatter));
                        this.fireGUIEvent(new GUIEvent(this, Commands.BUTTON_PRESSED_SELECT_END_DATE, endDate));
                    }
                }
                case CANCEL_BUTTON_ELEMENT_ID -> this.fireGUIEvent(new GUIEvent(this, Commands.BUTTON_PRESSED_CANCEL));
                case SAVE_BUTTON_ELEMENT_ID -> {
                    final var startDate = tryOptional(() -> LocalDate.parse(this.startDateElement.getValueAsString(), this.dateTimeFormatter));
                    final var endDate = tryOptional(() -> LocalDate.parse(this.endDateElement.getValueAsString(), this.dateTimeFormatter));

                    final var payload = new Payload.ServiceCreation(
                            (IDepictable) this.serviceTypeElement.getValue(),
                            startDate,
                            endDate
                    );
                    this.fireGUIEvent(new GUIEvent(this, Commands.BUTTON_PRESSED_SAVE, payload));
                }
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void processUpdateEvent(final UpdateEvent updateEvent) {
        if (updateEvent.getCmd() instanceof Commands command) {
            switch (command) {
                case UPDATE_SERVICE_TYPES -> {
                    this.serviceTypes = (List<? extends IDepictable>) updateEvent.getData();
                    this.serviceTypeElement.setData(this.serviceTypes.toArray(new IDepictable[0]));
                }
                case SET_START_DATE -> this.startDateElement.setValue(((LocalDate) updateEvent.getData()).format(this.dateTimeFormatter));
                case SET_END_DATE -> this.endDateElement.setValue(((LocalDate) updateEvent.getData()).format(this.dateTimeFormatter));
                case SET_SELECTED_SERVICE_TYPE -> {
                    this.serviceTypeElement.setValue(updateEvent.getData());
                }
                case SET_MODE -> {
                    this.mode = (Mode) updateEvent.getData();
                    this.serviceTypeElement.setEnabled(this.mode == Mode.CREATE);

                    final var border = BorderFactory.createTitledBorder(this.mode == Mode.CREATE ? "Leistung hinzufügen" : "Leistung bearbeiten");
                    border.setTitleColor(this.config.getTextColor());
                    border.setTitleFont(this.config.getHeaderFont());
                    this.container.setBorder(border);

                }
                default -> throw new IllegalArgumentException(String.valueOf(updateEvent));
            }
        }
    }

    private void initUI() {
        UIManager.put("ComboBox.disabledForeground", this.config.getTextColor());
        this.serviceTypeElement = AttributeElement
                .builder(super.generateRandomID())
                .labelName("Art der Leistung")
                .toolTip("Angabe des konkreten Leistungstyps")
                // label
                .labelSize(new Dimension(100, GUIConstants.IntSizes.DEFAULT_BUTTON_HEIGHT.getValue()))
                .labelFont(this.config.getFont())
                .labelTextColor(this.config.getTextColor())
                .labelBackgroundColor(this.config.getBackgroundColor())
                // input
                .mandatory(true)
                .formatType(AttributeElement.FormatType.TEXT)
                .autoformat()
                .modificationType(AttributeElement.ModificationType.INTERACTIVE_AND_DIRECT)
                // action button
                .data(this.serviceTypes.toArray())
                .actionElementSize(new Dimension(120, GUIConstants.IntSizes.DEFAULT_BUTTON_HEIGHT.getValue()))
                .actionType(AttributeElement.ActionType.COMBOBOX)
                .actionElementFont(this.config.getFont())
                .actionElementTextColor(this.config.getTextColor())
                .actionElementBackgroundColor(this.config.getBackgroundColor())
                .actionElementInsets(new Insets(0, 0, 0, 0))
                .build();
        serviceTypeElement.addObserver(this);

        this.startDateElement = AttributeElement
                .builder(START_DATE_ELEMENT_ID)
                .labelName("Startdatum")
                .toolTip("Angabe des Startdatums, ab wann die Leistung gebucht wird (Format: dd.MM.yyyy)")
                // label
                .labelSize(new Dimension(100, GUIConstants.IntSizes.DEFAULT_BUTTON_HEIGHT.getValue()))
                .labelFont(this.config.getFont())
                .labelTextColor(this.config.getTextColor())
                .labelBackgroundColor(this.config.getBackgroundColor())
                // input
                .mandatory(true)
                .modificationType(AttributeElement.ModificationType.INTERACTIVE_AND_DIRECT)
                .formatter(AttributeElement.FormatType.DATE.getFormatter())
                .allowedChars(AttributeElement.FormatType.DATE.getAllowedCharacterSet())
                // action button
                .data("Datum auswählen")
                .actionElementSize(new Dimension(120, GUIConstants.IntSizes.DEFAULT_BUTTON_HEIGHT.getValue()))
                .actionType(AttributeElement.ActionType.BUTTON)
                .actionElementFont(this.config.getFont())
                .actionElementTextColor(this.config.getTextColor())
                .actionElementBackgroundColor(this.config.getBackgroundColor())
                .actionElementInsets(new Insets(0, 0, 0, 0))
                .build();
        this.startDateElement.addObserver(this);

        this.endDateElement = AttributeElement
                .builder(END_DATE_ELEMENT_ID)
                .labelName("Enddatum")
                .toolTip("Angabe des Enddatums, bis wann die Leistung gebucht wird (Format: dd.MM.yyyy)")
                // label
                .labelSize(new Dimension(100, GUIConstants.IntSizes.DEFAULT_BUTTON_HEIGHT.getValue()))
                .labelFont(this.config.getFont())
                .labelTextColor(this.config.getTextColor())
                .labelBackgroundColor(this.config.getBackgroundColor())
                // input
                .mandatory(true)
                .modificationType(AttributeElement.ModificationType.INTERACTIVE_AND_DIRECT)
                .formatter(AttributeElement.FormatType.DATE.getFormatter())
                .allowedChars(AttributeElement.FormatType.DATE.getAllowedCharacterSet())
                // action button
                .data("Datum auswählen")
                .actionElementSize(new Dimension(120, GUIConstants.IntSizes.DEFAULT_BUTTON_HEIGHT.getValue()))
                .actionType(AttributeElement.ActionType.BUTTON)
                .actionElementFont(this.config.getFont())
                .actionElementTextColor(this.config.getTextColor())
                .actionElementBackgroundColor(this.config.getBackgroundColor())
                .actionElementInsets(new Insets(0, 0, 0, 0))
                .build();
        this.endDateElement.addObserver(this);

        final var servicePanel = AttributeComponent.builder(super.generateRandomID())
                .attributeElements(new AttributeElement[] {this.serviceTypeElement, this.startDateElement, this.endDateElement})
                .build();
        servicePanel.setBackground(this.config.getBackgroundColor());
        servicePanel.setForeground(this.config.getTextColor());
        servicePanel.setFont(this.config.getFont());
        servicePanel.getComponent(0).setBackground(this.config.getBackgroundColor());
        servicePanel.getComponent(0).setForeground(this.config.getTextColor());
        servicePanel.getComponent(0).setFont(this.config.getFont());
        servicePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Button component
        final var cancelButton = ButtonElement.builder(CANCEL_BUTTON_ELEMENT_ID)
                .buttonText("Abbrechen")
                .componentSize(new Dimension(150, GUIConstants.IntSizes.DEFAULT_BUTTON_HEIGHT.getValue()))
                .toolTip("Leistungsauswahl abbrechen")
                .font(this.config.getFont())
                .textColor(this.config.getTextColor())
                .backgroundColor(this.config.getBackgroundColor())
                .build();
        cancelButton.addObserver(this);

        final var saveButton = ButtonElement.builder(SAVE_BUTTON_ELEMENT_ID)
                .buttonText("Bestätigen")
                .componentSize(new Dimension(150, GUIConstants.IntSizes.DEFAULT_BUTTON_HEIGHT.getValue()))
                .toolTip("Leistungsauswahl bestätigen")
                .font(this.config.getFont())
                .textColor(this.config.getTextColor())
                .backgroundColor(this.config.getAccentColor())
                .build();
        saveButton.addObserver(this);

        this.container = ButtonComponent.builder(super.generateRandomID())
                .embeddedComponent(servicePanel)
                .buttonElements(new ButtonElement[] {cancelButton, saveButton})
                .position(ButtonComponent.Position.SOUTH)
                .orientation(ButtonComponent.Orientation.RIGHT)
                .build();
        this.container.setForeground(this.config.getTextColor());
        this.container.setBackground(this.config.getBackgroundColor());
        this.container.getComponent(1).setBackground(this.config.getBackgroundColor());
        this.container.getComponent(1).setForeground(this.config.getTextColor());
        final var border = BorderFactory.createTitledBorder("Leistung hinzufügen");
        border.setTitleColor(this.config.getTextColor());
        border.setTitleFont(this.config.getHeaderFont());
        this.container.setBorder(border);

        this.setLayout(new GridLayout(1, 1));
        this.add(this.container);
        this.setOpaque(true);

    }
}
