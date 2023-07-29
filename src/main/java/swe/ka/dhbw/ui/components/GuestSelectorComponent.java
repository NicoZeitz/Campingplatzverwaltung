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

public class GuestSelectorComponent extends GUIComponent implements IGUIEventListener {
    public record SearchInputChangedPayload(String text, List<? extends IDepictable> guests) {
    }

    public enum Commands implements EventCommand {
        // outgoing gui events
        BUTTON_PRESSED_GUEST_SELECTED("GuestSelectorComponent::BUTTON_PRESSED_GUEST_SELECTED", IDepictable.class),
        BUTTON_PRESSED_ADD_GUEST("GuestSelectorComponent::BUTTON_PRESSED_ADD_GUEST"),
        SEARCH_INPUT_CHANGED("GuestSelectorComponent::SEARCH_INPUT_CHANGED", SearchInputChangedPayload.class),
        // incoming update events
        SELECT_GUEST("GuestSelectorComponent::SELECT_GUEST", IDepictable.class),
        UPDATE_GUESTS("GuestSelectorComponent::UPDATE_GUESTS", List.class);

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
    private static final String SEARCH_INPUT_ELEMENT_ID = "GuestSelectorComponent::SEARCH_INPUT_ELEMENT_ID";
    private static final String ADD_BUTTON_ELEMENT_ID = "GuestSelectorComponent::ADD_BUTTON_ELEMENT_ID";
    private static final String GUEST_LIST_ELEMENT_ID = "GuestSelectorComponent::GUEST_LIST_ELEMENT_ID";

    // Data
    private List<? extends IDepictable> guests = new ArrayList<>();

    // Components
    private SimpleTextComponent searchInputElement;
    private SimpleListComponent guestListElement;

    public GuestSelectorComponent(final ReadonlyConfiguration config) {
        super("GuestSelectorComponent", config);
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
                        final var payload = new SearchInputChangedPayload(text, this.guests);
                        this.fireGUIEvent(new GUIEvent(this, Commands.SEARCH_INPUT_CHANGED, payload));
                    }
                }
                case ADD_BUTTON_ELEMENT_ID -> {
                    if (guiEvent.getCmd() == ButtonElement.Commands.BUTTON_PRESSED) {
                        this.fireGUIEvent(new GUIEvent(this, Commands.BUTTON_PRESSED_ADD_GUEST));
                    }
                }
                case GUEST_LIST_ELEMENT_ID -> {
                    if (guiEvent.getCmd() == SimpleListComponent.Commands.ELEMENT_SELECTED) {
                        final var guest = (IDepictable) guiEvent.getData();

                        if (guest == null) {
                            return;
                        }

                        this.fireGUIEvent(new GUIEvent(this, Commands.BUTTON_PRESSED_GUEST_SELECTED, guest));
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
                case UPDATE_GUESTS -> {
                    this.guests = (List<? extends IDepictable>) updateEvent.getData();
                    this.guestListElement.setListElements(this.guests);
                    final var text = this.searchInputElement.getText();
                    if (!text.isEmpty()) {
                        this.fireGUIEvent(new GUIEvent(
                                this,
                                Commands.SEARCH_INPUT_CHANGED,
                                new SearchInputChangedPayload(text, this.guests)
                        ));
                    }
                }
                case SELECT_GUEST -> this.fireGUIEvent(new GUIEvent(
                        this,
                        Commands.BUTTON_PRESSED_GUEST_SELECTED,
                        updateEvent.getData()
                ));
                default -> throw new IllegalArgumentException(String.valueOf(updateEvent));
            }
        }
    }

    private void initUI() {
        final var border = BorderFactory.createTitledBorder("Gast hinzufügen");
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

        final var addButton = super.createAddButton(ADD_BUTTON_ELEMENT_ID, "Gast hinzufügen");

        // @formatter:off
        inputWrapper.add(this.searchInputElement, new GridBagConstraints(1, 1, 1, 1, 1d, 0d, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        inputWrapper.add(addButton,               new GridBagConstraints(2, 1, 1, 1, 0d, 0d, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        // @formatter:on

        this.add(inputWrapper, BorderLayout.NORTH);

        this.guestListElement = SimpleListComponent.builder(GUEST_LIST_ELEMENT_ID)
                .font(this.config.getFont())
                .selectionMode(ListSelectionModel.SINGLE_SELECTION)
                .build();
        this.guestListElement.setFont(this.config.getFont());
        this.guestListElement.setBackground(this.config.getBackgroundColor());
        this.guestListElement.setForeground(this.config.getTextColor());
        this.guestListElement.addObserver(this);

        this.add(this.guestListElement, BorderLayout.CENTER);
    }
}
