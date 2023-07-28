package swe.ka.dhbw.ui.components;

import de.dhbwka.swe.utils.event.EventCommand;
import de.dhbwka.swe.utils.event.GUIEvent;
import de.dhbwka.swe.utils.event.IGUIEventListener;
import de.dhbwka.swe.utils.event.UpdateEvent;
import de.dhbwka.swe.utils.gui.*;
import de.dhbwka.swe.utils.model.Attribute;
import de.dhbwka.swe.utils.model.IDepictable;
import swe.ka.dhbw.control.GUIController;
import swe.ka.dhbw.control.ReadonlyConfiguration;
import swe.ka.dhbw.event.LogObserver;
import swe.ka.dhbw.ui.GUIComponent;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.*;
import java.util.stream.Stream;

// TODO: save state in gui controller instead
public class BookingCreateComponent extends GUIComponent implements IGUIEventListener {
    public enum Commands implements EventCommand {
        // outgoing gui events
        ADD_GUEST("BookingCreateComponent::ADD_GUEST"),
        ADD_SERVICE("BookingCreateComponent::ADD_SERVICE"),
        ADD_EQUIPMENT("BookingCreateComponent::ADD_EQUIPMENT"),
        RESPONSIBLE_GUEST_SELECTED("BookingCreateComponent::RESPONSIBLE_GUEST_SELECTED", ResponsibleGuestSelectedPayload.class),
        GUEST_DELETED("BookingCreateComponent::GUEST_DELETED", GuestDeletedPayload.class),

        SELECT_CHIPCARD("BookingCreateComponent::SELECT_CHIPCARD", SelectChipkartePayload.class),
        DELETE_CHIPCARD("BookingCreateComponent::DELETE_CHIPCARD", DeleteChipkartePayload.class),
        CREATE_BOOKING("BookingCreateComponent::CREATE_BOOKING"),
        // incoming update events TODO: check if right like this
        RESET("BookingCreateComponent::RESET"),
        UPDATE_SELECTED_GUESTS("BookingCreateComponent::UPDATE_SELECTED_GUESTS", UpdateSelectedGuestsPayload.class),
        // Callback events
        SELECT_PITCH_INTERACTIVELY("BookingCreateComponent::SELECT_PITCH_INTERACTIVELY"),
        SELECT_START_DATE("BookingCreateComponent::SELECT_START_DATE", Optional.class),
        SELECT_END_DATE("BookingCreateComponent::SELECT_END_DATE", Optional.class);

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

    private static final String BUTTON_COMPONENT_ID = "BookingCreateComponent::BUTTON_COMPONENT_ID";
    private static final String CREATE_BOOKING_BUTTON_ELEMENT_ID = "BookingCreateComponent::CREATE_BOOKING_BUTTON_ELEMENT_ID";
    private static final String CANCEL_CREATE_BOOKING_BUTTON_ELEMENT_ID = "BookingCreateComponent::CANCEL_CREATE_BOOKING_BUTTON_ELEMENT_ID";
    private static final String ADD_GUEST_BUTTON_ELEMENT_ID = "BookingCreateComponent::ADD_GUEST_BUTTON_ELEMENT_ID";
    private static final String ADD_SERVICE_BUTTON_ELEMENT_ID = "BookingCreateComponent::ADD_SERVICE_BUTTON_ELEMENT_ID";
    private static final String ADD_EQUIPMENT_BUTTON_ELEMENT_ID = "BookingCreateComponent::ADD_EQUIPMENT_BUTTON_ELEMENT_ID";
    private static final String BOOKING_PERIOD_FROM_ATTRIBUTE_ELEMENT_ID = "BookingCreateComponent::BOOKING_PERIOD_FROM_ATTRIBUTE_ELEMENT_ID";
    private static final String BOOKING_PERIOD_TO_ATTRIBUTE_ELEMENT_ID = "BookingCreateComponent::BOOKING_PERIOD_TO_ATTRIBUTE_ELEMENT_ID";
    private static final String CHIPCARD_ATTRIBUTE_ELEMENT_ID = "BookingCreateComponent::CHIPCARD_ATTRIBUTE_ELEMENT_ID";
    private static final String CHIPCARD_SIMPLE_TABLE_COMPONENT_ID = "BookingCreateComponent::CHIPCARD_SIMPLE_TABLE_COMPONENT_ID";
    private static final String PITCH_ATTRIBUTE_ELEMENT_ID = "BookingCreateComponent::PITCH_ATTRIBUTE_ELEMENT_ID";
    private static final String SELECT_PITCH_BUTTON_ELEMENT_ID = "BookingCreateComponent::SELECT_PITCH_BUTTON_ELEMENT_ID";
    private static final String DELETE_GUEST_BUTTON_ELEMENT_ID = "BookingCreateComponent::DELETE_GUEST_BUTTON_ELEMENT_ID";

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm", Locale.GERMANY);
    // Components
    private AttributeElement arrivalDateComponent;
    private AttributeElement departureDateComponent;
    private AttributeElement chipCardSelector;
    private SimpleTableComponent chipCardTable;
    private AttributeElement pitchSelector;
    private ButtonGroup guestButtonGroup;
    private JPanel guestTable;

    // Data
    private List<? extends IDepictable> availablePitches = new ArrayList<>();
    private List<? extends IDepictable> associatedGuests = new ArrayList<>();
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private Optional<? extends IDepictable> responsibleGuest = Optional.empty();
    private List<? extends IDepictable> bookedServices = new ArrayList<>();
    private List<? extends IDepictable> rentedEquipment = new ArrayList<>();

    private List<? extends IDepictable> availableChipCards = new ArrayList<>();
    private List<? extends IDepictable> selectedChipCards = new ArrayList<>();
    private List<? extends IDepictable> selectedGuests = new ArrayList<>();

    public BookingCreateComponent(
            final ReadonlyConfiguration config
    ) {
        super("BookingCreateComponent", config);
        this.initUI();
    }

    @Override
    public void processGUIEvent(final GUIEvent guiEvent) {
        if (guiEvent.getSource() instanceof ObservableComponent component) {
            final var id = component.getID();
            switch (id) {
                case ADD_GUEST_BUTTON_ELEMENT_ID -> this.fireGUIEvent(new GUIEvent(this, Commands.ADD_GUEST));
                case ADD_SERVICE_BUTTON_ELEMENT_ID -> this.fireGUIEvent(new GUIEvent(this, Commands.ADD_SERVICE));
                case ADD_EQUIPMENT_BUTTON_ELEMENT_ID -> this.fireGUIEvent(new GUIEvent(this, Commands.ADD_EQUIPMENT));
                case BOOKING_PERIOD_FROM_ATTRIBUTE_ELEMENT_ID -> {
                    if (guiEvent.getCmd() != AttributeElement.Commands.BUTTON_PRESSED) {
                        return;
                    }
                    final var data = ((AttributeElement) guiEvent.getData()).getValueAsString();
                    final var startDate = tryOptional(() -> LocalDateTime.parse(data, dateTimeFormatter));
                    this.fireGUIEvent(new GUIEvent(this, Commands.SELECT_START_DATE, startDate));

                }
                case BOOKING_PERIOD_TO_ATTRIBUTE_ELEMENT_ID -> {
                    if (guiEvent.getCmd() != AttributeElement.Commands.BUTTON_PRESSED) {
                        return;
                    }
                    final var data = ((AttributeElement) guiEvent.getData()).getValueAsString();
                    final var endDate = tryOptional(() -> LocalDateTime.parse(data, dateTimeFormatter));
                    this.fireGUIEvent(new GUIEvent(this, Commands.SELECT_END_DATE, endDate));
                }
                case CHIPCARD_ATTRIBUTE_ELEMENT_ID -> {
                    final var value = ((AttributeElement) guiEvent.getData()).getValue();
                    if (value instanceof String str && str.isEmpty()) {
                        return;
                    }

                    this.fireGUIEvent(new GUIEvent(this, Commands.SELECT_CHIPCARD, new SelectChipkartePayload(
                            this.availableChipCards,
                            this.selectedChipCards,
                            value
                    )));
                }
                case CHIPCARD_SIMPLE_TABLE_COMPONENT_ID -> {
                    final var scrollPane = (JScrollPane) this.chipCardTable.getComponent(0);
                    final var viewport = scrollPane.getViewport();
                    final var tableComponent = (JTable) viewport.getComponent(0);
                    if (tableComponent.getSelectedColumn() != 2 || guiEvent.getCmd() != SimpleTableComponent.Commands.ROW_SELECTED) {
                        return;
                    }
                    final var chipkarte = this.selectedChipCards.stream()
                            .filter(c -> c.getElementID().equals(((IDepictable) guiEvent.getData()).getElementID()))
                            .findFirst();
                    chipkarte.ifPresent(iDepictable -> this.fireGUIEvent(new GUIEvent(this, Commands.DELETE_CHIPCARD, new DeleteChipkartePayload(
                            this.availableChipCards,
                            this.selectedChipCards,
                            iDepictable
                    ))));
                }
                case CREATE_BOOKING_BUTTON_ELEMENT_ID -> {
                    this.fireGUIEvent(new GUIEvent(this, Commands.CREATE_BOOKING, BookingCreatePayload.create(this)));
                }
                case CANCEL_CREATE_BOOKING_BUTTON_ELEMENT_ID -> {
                    this.fireGUIEvent(new GUIEvent(this, Commands.RESET));
                }
                case SELECT_PITCH_BUTTON_ELEMENT_ID -> {
                    this.fireGUIEvent(new GUIEvent(this, Commands.SELECT_PITCH_INTERACTIVELY));
                }
                default -> {
                    LogObserver.logGUIEvent(guiEvent);
                }
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void processUpdateEvent(final UpdateEvent updateEvent) {
        // guest selection
        if (updateEvent.getCmdText().equals(Commands.ADD_GUEST.getCmdText())) {
            ((List<IDepictable>) this.selectedGuests).add((IDepictable) updateEvent.getData());
            this.buildGuestTable();
        } else if (updateEvent.getCmd() == Commands.UPDATE_SELECTED_GUESTS) {
            final var payload = (UpdateSelectedGuestsPayload) updateEvent.getData();
            this.selectedGuests = payload.selectedGuests();
            this.responsibleGuest = payload.responsibleGuest();
            this.buildGuestTable();
        }

        // date selection
        else if (updateEvent.getCmdText().equals(Commands.SELECT_START_DATE.getCmdText())) {
            final var date = LocalDateTime.of((LocalDate) updateEvent.getData(), LocalTime.of(0, 0));
            this.arrivalDateComponent.setValue(date.format(this.dateTimeFormatter));
        } else if (updateEvent.getCmdText().equals(Commands.SELECT_END_DATE.getCmdText())) {
            final var date = LocalDateTime.of((LocalDate) updateEvent.getData(), LocalTime.of(23, 59));
            this.departureDateComponent.setValue(date.format(this.dateTimeFormatter));
        }

        // pitch selection
        else if (updateEvent.getCmd() == GUIController.Commands.UPDATE_PITCHES) {
            this.availablePitches = (List<? extends IDepictable>) updateEvent.getData();
            this.pitchSelector.setData(this.availablePitches.toArray(new IDepictable[0]));
        } else if (updateEvent.getCmdText().equals(Commands.SELECT_PITCH_INTERACTIVELY.getCmdText())) {
            this.pitchSelector.setValue(updateEvent.getData());
        }

        // chip card selection
        else if (updateEvent.getCmd() == Commands.SELECT_CHIPCARD) {
            final var payload = (SelectChipkartePayload) updateEvent.getData();
            this.availableChipCards = payload.availableChipkarten();
            this.selectedChipCards = payload.selectedChipkarten();

            this.chipCardSelector.setData(Stream.concat(Stream.of(""), this.availableChipCards.stream()).toArray(Object[]::new));
            this.chipCardSelector.setValue(payload.selectedChipkarte());
            if (this.availableChipCards.size() == 0) this.chipCardSelector.setEnabled(false);
            this.reloadChipkarten();
        }

        // other
        else if (updateEvent.getCmd() == Commands.RESET) {
            this.resetInput();
        }
    }

    @SuppressWarnings("unchecked")
    public void resetInput() {
        this.arrivalDateComponent.setValue("");
        this.departureDateComponent.setValue("");
        this.availableChipCards = Stream.concat(this.availableChipCards.stream(), this.selectedChipCards.stream()).sorted().toList();
        this.selectedChipCards.clear();
        this.reloadChipkarten();

        this.pitchSelector.setData(this.availablePitches);
        this.pitchSelector.setValue(null);

        this.repaint();
    }

    private void buildGuestTable() {
        this.guestTable.removeAll();
        this.guestButtonGroup = new ButtonGroup();

        final var columns = new String[] {"Gast", "Verantwortlich", " "};
        for (var i = 0; i < columns.length; ++i) {
            final var text = columns[i];
            final var label = new JLabel();
            label.setText(text);
            label.setFont(this.config.getFont());
            label.setForeground(this.config.getTextColor());
            label.setBackground(this.config.getSecondaryBackgroundColor());
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(this.config.getTextColor(), 1),
                    new EmptyBorder(2, 10, 2, 10)
            ));
            label.setOpaque(true);
            this.guestTable.add(label, new GridBagConstraints(
                    i,
                    0,
                    1,
                    1,
                    i == 0 ? 1d : 0d,
                    0d,
                    GridBagConstraints.NORTH,
                    GridBagConstraints.HORIZONTAL,
                    new Insets(0, 0, 0, 0), 0,
                    0
            ));
        }

        for (var i = 0; i < this.selectedGuests.size(); ++i) {
            final var guest = this.selectedGuests.get(i);

            final var text = new JLabel();
            text.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(this.config.getTextColor(), 1),
                    BorderFactory.createEmptyBorder(2, 10, 2, 10)
            ));
            text.setText(guest.getVisibleText());
            text.setFont(this.config.getFont());
            text.setForeground(this.config.getTextColor());
            text.setBackground(this.config.getBackgroundColor());

            final var radioButton = new JRadioButton();
            this.guestButtonGroup.add(radioButton);
            radioButton.setToolTipText("Wählt den Gast als verantwortlichen Gast für die Buchung aus");
            radioButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            radioButton.setBorder(BorderFactory.createLineBorder(this.config.getSecondaryBackgroundColor(), 1));
            radioButton.setBackground(this.config.getSecondaryBackgroundColor());
            radioButton.setForeground(this.config.getTextColor());
            radioButton.setFont(this.config.getFont());
            radioButton.setHorizontalAlignment(SwingConstants.CENTER);
            radioButton.setSelected(guest.equals(this.responsibleGuest.orElse(null)));
            radioButton.addActionListener(e -> this.fireGUIEvent(new GUIEvent(
                    this,
                    Commands.RESPONSIBLE_GUEST_SELECTED,
                    new ResponsibleGuestSelectedPayload(this.selectedGuests, guest)
            )));

            final var radioButtonPanel = new JPanel();
            radioButtonPanel.setLayout(new GridLayout(1, 1));
            radioButtonPanel.setBorder(BorderFactory.createLineBorder(this.config.getTextColor(), 1));
            radioButtonPanel.setBackground(this.config.getSecondaryBackgroundColor());
            radioButtonPanel.add(radioButton);

            final var deleteButton = new JButton();
            deleteButton.setText("Löschen");
            deleteButton.setToolTipText("Löscht den Gast aus der Buchung");
            deleteButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            deleteButton.setFont(this.config.getFont());
            deleteButton.setForeground(this.config.getTextColor());
            deleteButton.setBackground(this.config.getFailureColor());
            deleteButton.setSize(GUIConstants.DimSizes.DEFAULT_BUTTON_SIZE.getValue());
            deleteButton.addActionListener(e -> this.fireGUIEvent(new GUIEvent(
                    this,
                    Commands.GUEST_DELETED,
                    new GuestDeletedPayload(this.selectedGuests, guest)
            )));
            deleteButton.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(this.config.getTextColor(), 1),
                    new EmptyBorder(0, 10, 0, 10)
            ));

            // @formatter:off
            this.guestTable.add(text,             new GridBagConstraints(0, i + 1, 1, 1, 1d, 0d, GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
            this.guestTable.add(radioButtonPanel, new GridBagConstraints(1, i + 1, 1, 1, 0d, 0d, GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
            this.guestTable.add(deleteButton,     new GridBagConstraints(2, i + 1, 1, 1, 0d, 0d, GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
            // @formatter:on
        }
        this.revalidate();
    }

    private JComponent createBookingTimeSpan() {
        this.arrivalDateComponent = AttributeElement
                .builder(BOOKING_PERIOD_FROM_ATTRIBUTE_ELEMENT_ID)
                .labelName("Anreisedatum")
                .toolTip("Angabe des Anreisedatums der Buchung (Format: dd.MM.yyyy HH:mm)")
                // label
                .labelSize(new Dimension(100, GUIConstants.IntSizes.DEFAULT_BUTTON_HEIGHT.getValue()))
                .labelFont(this.config.getFont())
                .labelTextColor(this.config.getTextColor())
                .labelBackgroundColor(this.config.getBackgroundColor())
                // input
                .mandatory(true)
                .modificationType(AttributeElement.ModificationType.INTERACTIVE_AND_DIRECT)
                .formatter(dateTimeFormatter)
                .allowedChars(AttributeElement.FormatType.DATETIME.getAllowedCharacterSet())
                // action button
                .data("Datum auswählen")
                .actionElementSize(new Dimension(120, GUIConstants.IntSizes.DEFAULT_BUTTON_HEIGHT.getValue()))
                .actionType(AttributeElement.ActionType.BUTTON)
                .actionElementFont(this.config.getFont())
                .actionElementTextColor(this.config.getTextColor())
                .actionElementBackgroundColor(this.config.getBackgroundColor())
                .actionElementInsets(new Insets(0, 0, 0, 0))
                .build();
        this.arrivalDateComponent.addObserver(this);

        this.departureDateComponent = AttributeElement
                .builder(BOOKING_PERIOD_TO_ATTRIBUTE_ELEMENT_ID)
                .labelName("Abreisedatum")
                .toolTip("Angabe des Abreisedatums der Buchung (Format: dd.MM.yyyy HH:mm)")
                // label
                .labelSize(new Dimension(100, GUIConstants.IntSizes.DEFAULT_BUTTON_HEIGHT.getValue()))
                .labelFont(this.config.getFont())
                .labelTextColor(this.config.getTextColor())
                .labelBackgroundColor(this.config.getBackgroundColor())
                // input
                .mandatory(true)
                .modificationType(AttributeElement.ModificationType.INTERACTIVE_AND_DIRECT)
                .formatter(dateTimeFormatter)
                .allowedChars(AttributeElement.FormatType.DATETIME.getAllowedCharacterSet())
                // action button
                .data("Datum auswählen")
                .actionElementSize(new Dimension(120, GUIConstants.IntSizes.DEFAULT_BUTTON_HEIGHT.getValue()))
                .actionType(AttributeElement.ActionType.BUTTON)
                .actionElementFont(this.config.getFont())
                .actionElementTextColor(this.config.getTextColor())
                .actionElementBackgroundColor(this.config.getBackgroundColor())
                .actionElementInsets(new Insets(0, 0, 0, 0))
                .build();
        this.departureDateComponent.addObserver(this);

        final var panel = AttributeComponent.builder(super.generateRandomID())
                .attributeElements(new AttributeElement[] {this.arrivalDateComponent, this.departureDateComponent})
                .build();
        panel.setBackground(this.config.getBackgroundColor());
        panel.setForeground(this.config.getTextColor());
        panel.setFont(this.config.getFont());
        panel.getComponent(0).setBackground(this.config.getBackgroundColor());
        panel.getComponent(0).setForeground(this.config.getTextColor());
        panel.getComponent(0).setFont(this.config.getFont());
        return panel;
    }

    private JComponent createChipCardSelector() {
        final var panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.getInsets().set(10, 10, 10, 10);
        panel.setOpaque(true);
        panel.setBackground(this.config.getBackgroundColor());
        panel.setForeground(this.config.getTextColor());

        this.chipCardSelector = AttributeElement.builder(CHIPCARD_ATTRIBUTE_ELEMENT_ID)
                .labelName("Neue Karte")
                .toolTip("Auswahl der Chipkarten")
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
                .actionElementSize(new Dimension(120, GUIConstants.IntSizes.DEFAULT_BUTTON_HEIGHT.getValue()))
                .actionType(AttributeElement.ActionType.COMBOBOX)
                .actionElementFont(this.config.getFont())
                .actionElementTextColor(this.config.getTextColor())
                .actionElementBackgroundColor(this.config.getBackgroundColor())
                .actionElementInsets(new Insets(0, 0, 0, 0))
                .build();
        this.chipCardSelector.addObserver(this);

        final var selectorComponent = AttributeComponent.builder(super.generateRandomID())
                .attributeElements(new AttributeElement[] {this.chipCardSelector})
                .build();

        selectorComponent.setFont(this.config.getFont());
        selectorComponent.setForeground(this.config.getTextColor());
        selectorComponent.setBackground(this.config.getBackgroundColor());
        selectorComponent.getComponent(0).setBackground(this.config.getBackgroundColor());
        selectorComponent.getComponent(0).setForeground(this.config.getTextColor());
        selectorComponent.getComponent(0).setFont(this.config.getFont());
        panel.add(selectorComponent,
                new GridBagConstraints(0, 0, 1, 1, 1d, 0d, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));


        this.chipCardTable = SimpleTableComponent.builder(CHIPCARD_SIMPLE_TABLE_COMPONENT_ID)
                .columnNames(new String[] {"Nummer", "Status", ""})
                .preferredScrollableViewportSize(new Dimension(500, 100))
                .cellRenderer((table, value, isSelected, hasFocus, row, column) -> {
                    if (column == 2) {
                        final var button = new JButton();
                        button.setText("Löschen");
                        button.setFont(this.config.getFont());
                        button.setForeground(this.config.getTextColor());
                        button.setBackground(this.config.getFailureColor());
                        button.setOpaque(true);
                        return button;
                    }

                    return new DefaultTableCellRenderer().getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                }, Object.class)
                .build();
        this.chipCardTable.addObserver(this);
        super.colorizeTable(this.chipCardTable);
        panel.add(this.chipCardTable,
                new GridBagConstraints(0, 1, 1, 1, 1d, 1d, GridBagConstraints.SOUTH, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));

        // TODO: work on this
//        final var table = super.createTable(new String[] {"Nummer", "Status", ""});
//        table.setPreferredScrollableViewportSize(table.getPreferredSize());
//        final var scrollPane = new JScrollPane(table);
//        panel.add(scrollPane,
//                new GridBagConstraints(0, 1, 1, 1, 1d, 1d, GridBagConstraints.SOUTH, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));

        this.resetInput();
        return panel;
    }

    private JComponent createEquipmentTable() {
        return new JPanel();
    }

    private JComponent createGuestTable() {
        this.guestTable = new JPanel();
        this.guestTable.setBorder(new EmptyBorder(10, 2, 10, 2));
        this.guestTable.setForeground(this.config.getTextColor());
        this.guestTable.setBackground(this.config.getBackgroundColor());
        this.guestTable.setLayout(new GridBagLayout());
        this.guestTable.setOpaque(true);

        this.buildGuestTable();

        return this.guestTable;
    }

    private JComponent createPitchSelector() {
        final var panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.getInsets().set(10, 10, 10, 10);
        panel.setOpaque(true);
        panel.setBackground(this.config.getBackgroundColor());
        panel.setForeground(this.config.getTextColor());

        this.pitchSelector = AttributeElement.builder(PITCH_ATTRIBUTE_ELEMENT_ID)
                .labelName("Stellplatz")
                .toolTip("Auswahl des Stellplatzes")
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
                .actionElementSize(new Dimension(120, GUIConstants.IntSizes.DEFAULT_BUTTON_HEIGHT.getValue()))
                .actionType(AttributeElement.ActionType.COMBOBOX)
                .actionElementFont(this.config.getFont())
                .actionElementTextColor(this.config.getTextColor())
                .actionElementBackgroundColor(this.config.getBackgroundColor())
                .actionElementInsets(new Insets(0, 0, 0, 0))
                .build();
        this.pitchSelector.addObserver(this);

        final var selectorComponent = AttributeComponent.builder(super.generateRandomID())
                .attributeElements(new AttributeElement[] {this.pitchSelector})
                .build();

        selectorComponent.setFont(this.config.getFont());
        selectorComponent.setForeground(this.config.getTextColor());
        selectorComponent.setBackground(this.config.getBackgroundColor());
        selectorComponent.getComponent(0).setBackground(this.config.getBackgroundColor());
        selectorComponent.getComponent(0).setForeground(this.config.getTextColor());
        selectorComponent.getComponent(0).setFont(this.config.getFont());
        panel.add(selectorComponent,
                new GridBagConstraints(0, 0, 1, 1, 1d, 0d, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

        final var interactiveSelectorButton = super.createAddButton(SELECT_PITCH_BUTTON_ELEMENT_ID, "Stellplatz interaktiv auswählen");
        interactiveSelectorButton.setButtonText("Kartenauswahl");
        interactiveSelectorButton.setMargin(new Insets(0, 10, 0, 10));

        panel.add(interactiveSelectorButton,
                new GridBagConstraints(1, 0, 1, 1, 0d, 0d, GridBagConstraints.NORTH, GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 0), 0, 0));

        return panel;
    }

    private JComponent createServiceTable() {
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
                ADD_GUEST_BUTTON_ELEMENT_ID,
                "Fügt einen neuen Gast hinzu",
                this.createGuestTable()
        ), new GridBagConstraints(1, 1, 1, 1, 1d, 0d, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        leftPanel.add(super.createAddableWrapper(
                "Leistungen auswählen",
                super.generateRandomID(),
                ADD_SERVICE_BUTTON_ELEMENT_ID,
                "Fügt eine neue gebuchte Leistung hinzu",
                this.createServiceTable()
        ), new GridBagConstraints(1, 2, 1, 1, 1d, 0d, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        leftPanel.add(super.createAddableWrapper(
                "Mitgebrachte Ausrüstung auswählen",
                super.generateRandomID(),
                ADD_EQUIPMENT_BUTTON_ELEMENT_ID,
                "Fügt eine neue mitgebrachte Ausrüstung hinzu",
                this.createEquipmentTable()
        ), new GridBagConstraints(1, 3, 1, 1, 1d, 0d, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        leftPanel.add(super.createFillComponent(), new GridBagConstraints(1, 4, 1, 1, 1d, 1d, GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));

        rightPanel.add(super.createWrapper("Buchungszeitraum", this.createBookingTimeSpan()),   new GridBagConstraints(1, 1, 1, 1, 1d, 0d, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        rightPanel.add(super.createWrapper("Stellplatzauswahl", this.createPitchSelector()), new GridBagConstraints(1, 2, 1, 1, 1d, 0d, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        rightPanel.add(super.createWrapper("Chipkartenauswahl", this.createChipCardSelector()), new GridBagConstraints(1, 3, 1, 1, 1d, 0d, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        rightPanel.add(super.createFillComponent(),                                              new GridBagConstraints(1, 4, 1, 1, 1d, 1d, GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
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

        final var border = BorderFactory.createTitledBorder("Neue Buchung anlegen");
        border.setTitleColor(this.config.getTextColor());
        border.setTitleFont(this.config.getHeaderFont());
        buttonComponent.setForeground(this.config.getTextColor());
        buttonComponent.setBackground(this.config.getBackgroundColor());
        buttonComponent.getComponent(0).setBackground(this.config.getBackgroundColor());
        buttonComponent.getComponent(0).setForeground(this.config.getTextColor());
        buttonComponent.getComponent(1).setBackground(this.config.getBackgroundColor());
        buttonComponent.getComponent(1).setForeground(this.config.getTextColor());
        buttonComponent.setBorder(border);

        this.add(buttonComponent);
    }

    private void reloadChipkarten() {
        this.chipCardSelector.setData(Stream.concat(
                Stream.of(""),
                this.availableChipCards.stream()
        ).toArray(Object[]::new));
        this.chipCardSelector.setValue("");
        this.chipCardTable.setMinimumSize(new Dimension(0, 100));
        this.chipCardTable.setData(this.selectedChipCards.stream().map(c ->
                new IDepictable() {
                    @Override
                    public Attribute[] getAttributeArray() {
                        final var superAttributes = c.getAttributeArray();
                        final var attributes = Arrays.copyOf(superAttributes, superAttributes.length + 1);
                        attributes[superAttributes.length] = new Attribute("",
                                c,
                                Object.class,
                                null,
                                null,
                                true,
                                false,
                                false,
                                true
                        );
                        return attributes;
                    }

                    @Override
                    public String getElementID() {
                        return c.getElementID();
                    }
                }).toArray(IDepictable[]::new), new String[] {"Nummer", "Status", ""});
        this.revalidate();
    }

    public record ResponsibleGuestSelectedPayload(List<? extends IDepictable> selectedGuests, IDepictable selectedGuest) {
    }

    public record GuestDeletedPayload(List<? extends IDepictable> selectedGuests, IDepictable deletedGuest) {
    }

    public record UpdateSelectedGuestsPayload(List<? extends IDepictable> selectedGuests, Optional<IDepictable> responsibleGuest) {
    }

    public record SelectChipkartePayload(List<? extends IDepictable> availableChipkarten, List<? extends IDepictable> selectedChipkarten,
                                         Object selectedChipkarte) {
    }

    public record DeleteChipkartePayload(List<? extends IDepictable> availableChipkarten, List<? extends IDepictable> selectedChipkarten,
                                         IDepictable deletedChipkarte) {
    }

    public record BookingCreatePayload(
            Optional<LocalDateTime> arrivalDate,
            Optional<LocalDateTime> departureDate,
            List<? extends IDepictable> associatedGuests,
            Optional<? extends IDepictable> responsibleGuest,
            List<? extends IDepictable> bookedServices,
            List<? extends IDepictable> rentedEquipment,
            IDepictable bookedPitch,
            List<? extends IDepictable> chipCards
    ) {
        public static BookingCreatePayload create(final BookingCreateComponent component) {
            final var arrivalDate = component.tryOptional(() -> LocalDateTime.parse(
                    component.arrivalDateComponent.getValueAsString(),
                    component.dateTimeFormatter
            ));
            final var departureDate = component.tryOptional(() -> LocalDateTime.parse(
                    component.departureDateComponent.getValueAsString(),
                    component.dateTimeFormatter
            ));

            return new BookingCreatePayload(
                    arrivalDate,
                    departureDate,
                    component.associatedGuests,
                    component.responsibleGuest,
                    component.bookedServices,
                    component.rentedEquipment,
                    (IDepictable) component.pitchSelector.getValue(),
                    component.selectedChipCards
            );
        }
    }
}
