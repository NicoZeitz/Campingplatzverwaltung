package swe.ka.dhbw.ui.components;

import de.dhbwka.swe.utils.event.EventCommand;
import de.dhbwka.swe.utils.event.GUIEvent;
import de.dhbwka.swe.utils.event.IGUIEventListener;
import de.dhbwka.swe.utils.event.UpdateEvent;
import de.dhbwka.swe.utils.gui.ButtonElement;
import de.dhbwka.swe.utils.gui.ObservableComponent;
import de.dhbwka.swe.utils.gui.SimpleListComponent;
import de.dhbwka.swe.utils.gui.SimpleTextComponent;
import de.dhbwka.swe.utils.model.IDepictable;
import swe.ka.dhbw.control.ReadonlyConfiguration;
import swe.ka.dhbw.ui.GUIComponent;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class EquipmentSelectorComponent extends GUIComponent implements IGUIEventListener {
    public record SearchInputChangedPayload(String text, List<? extends IDepictable> guests) {
    }

    public enum Commands implements EventCommand {
        // outgoing gui events
        BUTTON_PRESSED_EQUIPMENT_SELECTED("EquipmentSelectorComponent::BUTTON_PRESSED_EQUIPMENT_SELECTED", IDepictable.class),
        BUTTON_PRESSED_ADD_EQUIPMENT("EquipmentSelectorComponent::BUTTON_PRESSED_ADD_EQUIPMENT"),
        SEARCH_INPUT_CHANGED("EquipmentSelectorComponent::SEARCH_INPUT_CHANGED", SearchInputChangedPayload.class),
        // incoming update events
        SELECT_EQUIPMENT("EquipmentSelectorComponent::SELECT_EQUIPMENT", IDepictable.class),
        UPDATE_EQUIPMENT("EquipmentSelectorComponent::UPDATE_EQUIPMENT", List.class),
        UPDATE_FILTERED_EQUIPMENT("EquipmentSelectorComponent::UPDATE_FILTERED_EQUIPMENT", List.class);

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
    private static final String SEARCH_INPUT_ELEMENT_ID = "EquipmentSelectorComponent::SEARCH_INPUT_ELEMENT_ID";
    private static final String ADD_BUTTON_ELEMENT_ID = "EquipmentSelectorComponent::ADD_BUTTON_ELEMENT_ID";
    private static final String EQUIPMENT_LIST_ELEMENT_ID = "EquipmentSelectorComponent::EQUIPMENT_LIST_ELEMENT_ID";

    // Data
    private List<? extends IDepictable> allEquipment = new ArrayList<>();
    private List<? extends IDepictable> equipment = new ArrayList<>();

    // Components
    private SimpleTextComponent searchInputElement;
    private SimpleListComponent equipmentListElement;

    public EquipmentSelectorComponent(final ReadonlyConfiguration config) {
        super("EquipmentSelectorComponent", config);
        this.initUI();
    }

    @Override
    public void processGUIEvent(final GUIEvent guiEvent) {
        if (guiEvent.getSource() instanceof ObservableComponent component) {
            final var id = component.getID();
            switch (id) {
                case SEARCH_INPUT_ELEMENT_ID -> {
                    if (guiEvent.getCmd() == SimpleTextComponent.Commands.TEXT_CHANGED) {
                        final var text = this.searchInputElement.getText();
                        final var payload = new SearchInputChangedPayload(text, this.allEquipment);
                        this.fireGUIEvent(new GUIEvent(this, Commands.SEARCH_INPUT_CHANGED, payload));
                    }
                }
                case ADD_BUTTON_ELEMENT_ID -> {
                    if (guiEvent.getCmd() == ButtonElement.Commands.BUTTON_PRESSED) {
                        this.fireGUIEvent(new GUIEvent(this, Commands.BUTTON_PRESSED_ADD_EQUIPMENT));
                    }
                }
                case EQUIPMENT_LIST_ELEMENT_ID -> {
                    if (guiEvent.getCmd() == SimpleListComponent.Commands.ELEMENT_SELECTED) {
                        final var guest = (IDepictable) guiEvent.getData();

                        if (guest == null) {
                            return;
                        }

                        this.fireGUIEvent(new GUIEvent(this, Commands.BUTTON_PRESSED_EQUIPMENT_SELECTED, guest));
                    }
                }
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void processUpdateEvent(final UpdateEvent updateEvent) {
        if (updateEvent.getCmd() instanceof Commands command) {
            switch (command) {
                case UPDATE_EQUIPMENT -> {
                    this.allEquipment = (List<? extends IDepictable>) updateEvent.getData();
                    final var payload = new SearchInputChangedPayload(this.searchInputElement.getText(), this.allEquipment);
                    this.fireGUIEvent(new GUIEvent(this, Commands.SEARCH_INPUT_CHANGED, payload));
                }
                case UPDATE_FILTERED_EQUIPMENT -> {
                    this.equipment = (List<? extends IDepictable>) updateEvent.getData();
                    this.equipmentListElement.setListElements(this.equipment);
                }
                case SELECT_EQUIPMENT -> this.fireGUIEvent(new GUIEvent(
                        this,
                        Commands.BUTTON_PRESSED_EQUIPMENT_SELECTED,
                        updateEvent.getData()
                ));
                default -> throw new IllegalArgumentException(String.valueOf(updateEvent));
            }
        }
    }

    private void initUI() {
        final var border = BorderFactory.createTitledBorder("Ausrüstung auswählen");
        border.setTitleFont(this.config.getLargeFont());
        border.setTitleColor(this.config.getTextColor());
        this.setBorder(border);
        this.setBackground(this.config.getBackgroundColor());
        this.setForeground(this.config.getTextColor());
        this.setOpaque(true);
        this.setLayout(new BorderLayout());

        final var inputWrapper = new JPanel();
        inputWrapper.setLayout(new GridBagLayout());

        this.searchInputElement = SimpleTextComponent.builder(SEARCH_INPUT_ELEMENT_ID)
                .componentSize(new Dimension(200, 31))
                .build();
        this.searchInputElement.setBackgroundColor(this.config.getBackgroundColor());
        this.searchInputElement.setForeground(this.config.getTextColor());
        this.searchInputElement.setFont(this.config.getFont());
        this.searchInputElement.addObserver(this);
        final var scrollPane = (JScrollPane) this.searchInputElement.getComponent(0);
        final var viewPort = (JViewport) scrollPane.getComponent(0);
        final var textArea = (JTextArea) viewPort.getComponent(0);
        textArea.getDocument().putProperty("filterNewlines", Boolean.TRUE);

        final var addButton = super.createAddButton(ADD_BUTTON_ELEMENT_ID, "Ausrüstung hinzufügen");

        // @formatter:off
        inputWrapper.add(this.searchInputElement, new GridBagConstraints(1, 1, 1, 1, 1d, 0d, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        inputWrapper.add(addButton,               new GridBagConstraints(2, 1, 1, 1, 0d, 0d, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        // @formatter:on

        this.add(inputWrapper, BorderLayout.NORTH);

        this.equipmentListElement = SimpleListComponent.builder(EQUIPMENT_LIST_ELEMENT_ID)
                .font(this.config.getFont())
                .selectionMode(ListSelectionModel.SINGLE_SELECTION)
                .build();
        this.equipmentListElement.setFont(this.config.getFont());
        this.equipmentListElement.setBackground(this.config.getBackgroundColor());
        this.equipmentListElement.setForeground(this.config.getTextColor());
        this.equipmentListElement.addObserver(this);
        super.colorizeSimpleListComponent(this.equipmentListElement);

        this.add(this.equipmentListElement, BorderLayout.CENTER);
    }
}
