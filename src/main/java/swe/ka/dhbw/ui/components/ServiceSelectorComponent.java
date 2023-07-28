package swe.ka.dhbw.ui.components;

import de.dhbwka.swe.utils.event.EventCommand;
import de.dhbwka.swe.utils.event.GUIEvent;
import de.dhbwka.swe.utils.event.IGUIEventListener;
import de.dhbwka.swe.utils.event.UpdateEvent;
import de.dhbwka.swe.utils.gui.*;
import de.dhbwka.swe.utils.model.IDepictable;
import swe.ka.dhbw.control.ReadonlyConfiguration;
import swe.ka.dhbw.ui.GUIComponent;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class ServiceSelectorComponent extends GUIComponent implements IGUIEventListener {
    // Commands
    public enum Commands implements EventCommand {
        // outgoing gui events
        DATE_PICKER_START_DATE("ServiceSelectorComponent::DATE_PICKER_START_DATE", LocalDate.class),
        DATE_PICKER_END_DATE("ServiceSelectorComponent::DATE_PICKER_END_DATE", LocalDate.class),
        CANCEL("ServiceSelectorComponent::CANCEL"),
        SAVE("ServiceSelectorComponent::SAVE", SavePayload.class);

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
    private static final String SERVICE_TYPE_ELEMENT_ID = "ServiceSelectorComponent::SERVICE_TYPE_ELEMENT_ID";
    private static final String START_DATE_ELEMENT_ID = "ServiceSelectorComponent::START_DATE_ELEMENT_ID";
    private static final String END_DATE_ELEMENT_ID = "ServiceSelectorComponent::END_DATE_ELEMENT_ID";
    private static final String CANCEL_BUTTON_ELEMENT_ID = "ServiceSelectorComponent::CANCEL_BUTTON_ELEMENT_ID";
    private static final String SAVE_BUTTON_ELEMENT_ID = "ServiceSelectorComponent::SAVE_BUTTON_ELEMENT_ID";

    // Components
    private AttributeElement serviceTypeElement;
    private AttributeElement startDateElement;
    private AttributeElement endDateElement;

    // Data
    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.GERMANY);
    private List<? extends IDepictable> serviceTypes;

    public ServiceSelectorComponent(final ReadonlyConfiguration config, final List<? extends IDepictable> serviceTypes, final EditPayload payload) {
        // edit mode
        super("ServiceSelectorComponent", config);
        this.serviceTypes = serviceTypes;
        this.initUI(payload);
    }

    public ServiceSelectorComponent(final ReadonlyConfiguration config, final List<? extends IDepictable> serviceTypes) {
        // creation mode
        super("ServiceSelectorComponent", config);
        this.serviceTypes = serviceTypes;
        this.initUI();
    }

    @Override
    public void processGUIEvent(final GUIEvent guiEvent) {
        if (guiEvent.getSource() instanceof ObservableComponent component) {
            final var id = component.getID();
            switch (id) {
                case SERVICE_TYPE_ELEMENT_ID -> {
                }
                case START_DATE_ELEMENT_ID -> {
                    if (guiEvent.getCmd() == AttributeElement.Commands.BUTTON_PRESSED) {
                        final var startDate = tryOptional(() -> LocalDate.parse(this.startDateElement.getValueAsString(), this.dateTimeFormatter));
                        this.fireGUIEvent(new GUIEvent(this, Commands.DATE_PICKER_START_DATE, startDate));
                    }
                }
                case END_DATE_ELEMENT_ID -> {
                    if (guiEvent.getCmd() == AttributeElement.Commands.BUTTON_PRESSED) {
                        final var endDate = tryOptional(() -> LocalDate.parse(this.endDateElement.getValueAsString(), this.dateTimeFormatter));
                        this.fireGUIEvent(new GUIEvent(this, Commands.DATE_PICKER_END_DATE, endDate));
                    }
                }
                case CANCEL_BUTTON_ELEMENT_ID -> this.fireGUIEvent(new GUIEvent(this, Commands.CANCEL));
                case SAVE_BUTTON_ELEMENT_ID -> {
                    final var startDate = tryOptional(() -> LocalDate.parse(this.startDateElement.getValueAsString(), this.dateTimeFormatter));
                    final var endDate = tryOptional(() -> LocalDate.parse(this.endDateElement.getValueAsString(), this.dateTimeFormatter));

                    final var payload = new SavePayload(
                            (IDepictable) this.serviceTypeElement.getValue(),
                            startDate,
                            endDate
                    );
                    this.fireGUIEvent(new GUIEvent(this, Commands.SAVE, payload));
                }
            }
        }
    }

    @Override
    public void processUpdateEvent(final UpdateEvent updateEvent) {
        if (updateEvent.getCmdText().equals(Commands.DATE_PICKER_START_DATE.getCmdText())) {
            this.startDateElement.setValue(((LocalDate) updateEvent.getData()).format(this.dateTimeFormatter));
        } else if (updateEvent.getCmdText().equals(Commands.DATE_PICKER_END_DATE.getCmdText())) {
            this.endDateElement.setValue(((LocalDate) updateEvent.getData()).format(this.dateTimeFormatter));
        }
    }

    private void initUI(final EditPayload payload) {
        this.initUI();
        this.serviceTypeElement.setEnabled(false);
        this.serviceTypeElement.setValue(payload.selectedServiceType);
        this.startDateElement.setValue(payload.startDate.format(this.dateTimeFormatter));
        this.endDateElement.setValue(payload.endDate.format(this.dateTimeFormatter));
    }

    private void initUI() {
        this.serviceTypeElement = AttributeElement
                .builder(SERVICE_TYPE_ELEMENT_ID)
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

        final var container = ButtonComponent.builder(super.generateRandomID())
                .embeddedComponent(servicePanel)
                .buttonElements(new ButtonElement[] {cancelButton, saveButton})
                .position(ButtonComponent.Position.SOUTH)
                .orientation(ButtonComponent.Orientation.RIGHT)
                .build();
        container.setForeground(this.config.getTextColor());
        container.setBackground(this.config.getBackgroundColor());
        container.getComponent(1).setBackground(this.config.getBackgroundColor());
        container.getComponent(1).setForeground(this.config.getTextColor());
        final var border = BorderFactory.createTitledBorder("Leistung hinzufügen");
        border.setTitleColor(this.config.getTextColor());
        border.setTitleFont(this.config.getHeaderFont());
        container.setBorder(border);

        this.setLayout(new GridLayout(1, 1));
        this.add(container);
        this.setOpaque(true);

    }

    public record EditPayload(IDepictable selectedServiceType, LocalDate startDate, LocalDate endDate) {
    }

    public record SavePayload(IDepictable selectedServiceType, Optional<LocalDate> startDate, Optional<LocalDate> endDate) {
    }
}
