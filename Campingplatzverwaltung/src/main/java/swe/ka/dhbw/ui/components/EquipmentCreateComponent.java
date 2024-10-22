package swe.ka.dhbw.ui.components;

import de.dhbwka.swe.utils.event.EventCommand;
import de.dhbwka.swe.utils.event.GUIEvent;
import de.dhbwka.swe.utils.event.IGUIEventListener;
import de.dhbwka.swe.utils.event.UpdateEvent;
import de.dhbwka.swe.utils.gui.*;
import swe.ka.dhbw.control.ReadonlyConfiguration;
import swe.ka.dhbw.ui.GUIComponent;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class EquipmentCreateComponent extends GUIComponent implements IGUIEventListener {
    public record SavePayload(
            Optional<String> description,
            Integer amount,
            Optional<Double> width,
            Optional<Double> height,
            Optional<String> licensePlate,
            Optional<Object> vehicleTyp) {
    }

    public enum Commands implements EventCommand {
        // outgoing gui events
        BUTTON_PRESSED_CANCEL("ServiceSelectorComponent::BUTTON_PRESSED_CANCEL"),
        BUTTON_PRESSED_SAVE("ServiceSelectorComponent::BUTTON_PRESSED_SAVE", SavePayload.class),
        // incoming update events
        SET_VEHICLE_TYPES("ServiceSelectorComponent::SET_VEHICLE_TYPES", List.class);

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
    private static final String DESCRIPTION_ELEMENT_ID = "EquipmentSelectorComponent::DESCRIPTION_ELEMENT_ID";
    private static final String AMOUNT_ELEMENT_ID = "EquipmentSelectorComponent::AMOUNT_ELEMENT_ID";
    private static final String WIDTH_ELEMENT_ID = "EquipmentSelectorComponent::WIDTH_ELEMENT_ID";
    private static final String HEIGHT_ELEMENT_ID = "EquipmentSelectorComponent::HEIGHT_ELEMENT_ID";
    private static final String LICENSE_PLATE_ELEMENT_ID = "EquipmentSelectorComponent::LICENSE_PLATE_ELEMENT_ID";
    private static final String VEHICLE_TYP_ELEMENT_ID = "EquipmentSelectorComponent::VEHICLE_TYP_ELEMENT_ID";
    private static final String CANCEL_BUTTON_ELEMENT_ID = "EquipmentSelectorComponent::CANCEL_BUTTON_ELEMENT_ID";
    private static final String SAVE_BUTTON_ELEMENT_ID = "EquipmentSelectorComponent::SAVE_BUTTON_ELEMENT_ID";
    // Data
    private List<?> vehicleTypes = new ArrayList<>();
    // Components
    private AttributeElement descriptionElement;
    private AttributeElement amountElement;
    private AttributeElement widthElement;
    private AttributeElement heightElement;
    private AttributeElement licensePlateElement;
    private AttributeElement vehicleTypElement;

    public EquipmentCreateComponent(final ReadonlyConfiguration config) {
        super("EquipmentSelectorComponent", config);
        this.initUI();
    }

    @Override
    public void processGUIEvent(final GUIEvent guiEvent) {
        if (guiEvent.getSource() instanceof ObservableComponent component) {
            final var id = component.getID();
            switch (id) {
                case CANCEL_BUTTON_ELEMENT_ID -> this.fireGUIEvent(new GUIEvent(this, Commands.BUTTON_PRESSED_CANCEL));
                case SAVE_BUTTON_ELEMENT_ID -> {
                    this.widthElement.getValue();
                    final var description = Optional.of((String) this.descriptionElement.getValue()).filter(s -> !s.isBlank());
                    final var amount = (Integer) this.amountElement.getValue();
                    final var width = tryOptional(() -> Double.parseDouble((this.widthElement.getValueAsString()).replaceAll(",", ".")));
                    final var height = tryOptional(() -> Double.parseDouble((this.heightElement.getValueAsString()).replaceAll(",", ".")));
                    final var licensePlate = Optional.of((String) this.licensePlateElement.getValue()).filter(s -> !s.isBlank());
                    final var vehicleTyp = Optional.of(this.vehicleTypElement.getValue()).filter(s -> !s.equals(""));

                    final var payload = new SavePayload(description, amount, width, height, licensePlate, vehicleTyp);
                    this.fireGUIEvent(new GUIEvent(this, Commands.BUTTON_PRESSED_SAVE, payload));
                }
            }
        }
    }

    @Override
    public void processUpdateEvent(final UpdateEvent updateEvent) {
        if (updateEvent.getCmd() == Commands.SET_VEHICLE_TYPES) {
            this.vehicleTypes = (List<?>) updateEvent.getData();
            this.vehicleTypElement.setData(Stream.concat(Stream.of(""), this.vehicleTypes.stream()).toArray(Object[]::new));
        }
    }

    private void initUI() {
        this.descriptionElement = AttributeElement
                .builder(DESCRIPTION_ELEMENT_ID)
                .labelName("Bezeichnung")
                .toolTip("Bezeichnung für die mitgebrachte Ausrüstung")
                .textFieldFont(this.config.getFont())
                // label
                .labelSize(new Dimension(100, GUIConstants.IntSizes.DEFAULT_BUTTON_HEIGHT.getValue()))
                .labelFont(this.config.getFont())
                .labelTextColor(this.config.getTextColor())
                .labelBackgroundColor(this.config.getBackgroundColor())
                // input
                .mandatory(true)
                .modificationType(AttributeElement.ModificationType.INTERACTIVE)
                .formatType(AttributeElement.FormatType.TEXT)
                .build();
        this.descriptionElement.addObserver(this);

        this.amountElement = AttributeElement
                .builder(AMOUNT_ELEMENT_ID)
                .labelName("Anzahl")
                .toolTip("Anzahl der mitgebrachten Ausrüstung")
                .textFieldFont(this.config.getFont())
                // label
                .labelSize(new Dimension(100, GUIConstants.IntSizes.DEFAULT_BUTTON_HEIGHT.getValue()))
                .labelFont(this.config.getFont())
                .labelTextColor(this.config.getTextColor())
                .labelBackgroundColor(this.config.getBackgroundColor())
                // input
                .value(1)
                .mandatory(true)
                .modificationType(AttributeElement.ModificationType.INTERACTIVE)
                .autoformat()
                .formatType(AttributeElement.FormatType.INTEGER)
                .build();
        this.amountElement.addObserver(this);

        this.widthElement = AttributeElement
                .builder(WIDTH_ELEMENT_ID)
                .labelName("Breite (in m)")
                .toolTip("Breite der mitgebrachten Ausrüstung")
                .textFieldFont(this.config.getFont())
                // label
                .labelSize(new Dimension(100, GUIConstants.IntSizes.DEFAULT_BUTTON_HEIGHT.getValue()))
                .labelFont(this.config.getFont())
                .labelTextColor(this.config.getTextColor())
                .labelBackgroundColor(this.config.getBackgroundColor())
                // input
                .mandatory(true)
                .modificationType(AttributeElement.ModificationType.INTERACTIVE)
                .autoformat()
                .formatType(AttributeElement.FormatType.DOUBLE)
                .build();
        this.widthElement.addObserver(this);

        this.heightElement = AttributeElement
                .builder(HEIGHT_ELEMENT_ID)
                .labelName("Höhe (in m)")
                .toolTip("Höhe der mitgebrachten Ausrüstung")
                .textFieldFont(this.config.getFont())
                // label
                .labelSize(new Dimension(100, GUIConstants.IntSizes.DEFAULT_BUTTON_HEIGHT.getValue()))
                .labelFont(this.config.getFont())
                .labelTextColor(this.config.getTextColor())
                .labelBackgroundColor(this.config.getBackgroundColor())
                // input
                .mandatory(true)
                .modificationType(AttributeElement.ModificationType.INTERACTIVE)
                .autoformat()
                .formatType(AttributeElement.FormatType.DOUBLE)
                .build();
        this.heightElement.addObserver(this);

        this.licensePlateElement = AttributeElement
                .builder(LICENSE_PLATE_ELEMENT_ID)
                .labelName("Kennzeichen")
                .toolTip("Angabe des Kennzeichens, falls es sich bei der Ausrüstung um ein Fahrzeug handelt (optional)")
                .textFieldFont(this.config.getFont())
                // label
                .labelSize(new Dimension(100, GUIConstants.IntSizes.DEFAULT_BUTTON_HEIGHT.getValue()))
                .labelFont(this.config.getFont())
                .labelTextColor(this.config.getTextColor())
                .labelBackgroundColor(this.config.getBackgroundColor())
                // input
                .mandatory(true)
                .modificationType(AttributeElement.ModificationType.INTERACTIVE)
                .formatType(AttributeElement.FormatType.TEXT)
                .build();
        this.licensePlateElement.addObserver(this);

        this.vehicleTypElement = AttributeElement
                .builder(VEHICLE_TYP_ELEMENT_ID)
                .labelName("Fahrzeugtyp")
                .toolTip("Angabe des konkreten Fahrzeugtyps, falls es sich bei der Ausrüstung um ein Fahrzeug handelt (optional)")
                .textFieldFont(this.config.getFont())
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
                .data(Stream.concat(Stream.of(""), this.vehicleTypes.stream()).toArray(Object[]::new))
                .actionElementSize(new Dimension(120, GUIConstants.IntSizes.DEFAULT_BUTTON_HEIGHT.getValue()))
                .actionType(AttributeElement.ActionType.COMBOBOX)
                .actionElementFont(this.config.getFont())
                .actionElementTextColor(this.config.getTextColor())
                .actionElementBackgroundColor(this.config.getSecondaryBackgroundColor())
                .actionElementInsets(new Insets(0, 0, 0, 0))
                .build();
        vehicleTypElement.addObserver(this);

        final var equipmentPanel = AttributeComponent.builder(super.generateRandomID())
                .attributeElements(new AttributeElement[] {
                        this.descriptionElement,
                        this.amountElement,
                        this.widthElement,
                        this.heightElement,
                        this.licensePlateElement,
                        this.vehicleTypElement
                })
                .build();
        equipmentPanel.setBackground(this.config.getBackgroundColor());
        equipmentPanel.setForeground(this.config.getTextColor());
        equipmentPanel.setFont(this.config.getFont());
        equipmentPanel.getComponent(0).setBackground(this.config.getBackgroundColor());
        equipmentPanel.getComponent(0).setForeground(this.config.getTextColor());
        equipmentPanel.getComponent(0).setFont(this.config.getFont());
        equipmentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        super.colorizeAttributeComponent(equipmentPanel);

        // Button component
        final var cancelButton = ButtonElement.builder(CANCEL_BUTTON_ELEMENT_ID)
                .buttonText("Abbrechen")
                .componentSize(new Dimension(150, GUIConstants.IntSizes.DEFAULT_BUTTON_HEIGHT.getValue()))
                .toolTip("Ausrüstungsauswahl abbrechen")
                .font(this.config.getFont())
                .textColor(this.config.getTextColor())
                .backgroundColor(this.config.getBackgroundColor())
                .build();
        cancelButton.addObserver(this);

        final var saveButton = ButtonElement.builder(SAVE_BUTTON_ELEMENT_ID)
                .buttonText("Bestätigen")
                .componentSize(new Dimension(150, GUIConstants.IntSizes.DEFAULT_BUTTON_HEIGHT.getValue()))
                .toolTip("Ausrüstungsauswahl bestätigen")
                .font(this.config.getFont())
                .textColor(this.config.getTextColor())
                .backgroundColor(this.config.getAccentColor())
                .build();
        saveButton.addObserver(this);

        final var container = ButtonComponent.builder(super.generateRandomID())
                .embeddedComponent(equipmentPanel)
                .buttonElements(new ButtonElement[] {cancelButton, saveButton})
                .position(ButtonComponent.Position.SOUTH)
                .orientation(ButtonComponent.Orientation.RIGHT)
                .build();
        container.setForeground(this.config.getTextColor());
        container.setBackground(this.config.getBackgroundColor());
        container.getComponent(1).setBackground(this.config.getBackgroundColor());
        container.getComponent(1).setForeground(this.config.getTextColor());
        final var border = BorderFactory.createTitledBorder("Mitgebrachte Ausrüstung hinzufügen");
        border.setTitleColor(this.config.getTextColor());
        border.setTitleFont(this.config.getHeaderFont());
        container.setBorder(border);

        this.setLayout(new GridLayout(1, 1));
        this.add(container);
        this.setOpaque(true);
    }
}
