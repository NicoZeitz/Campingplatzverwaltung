package swe.ka.dhbw.ui.components;

import de.dhbwka.swe.utils.event.EventCommand;
import de.dhbwka.swe.utils.event.GUIEvent;
import de.dhbwka.swe.utils.event.IGUIEventListener;
import de.dhbwka.swe.utils.event.UpdateEvent;
import de.dhbwka.swe.utils.gui.*;
import de.dhbwka.swe.utils.model.IDepictable;
import swe.ka.dhbw.control.GUIController;
import swe.ka.dhbw.control.ReadonlyConfiguration;
import swe.ka.dhbw.ui.GUIBuchung;
import swe.ka.dhbw.ui.GUIComponent;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BookingChangeComponent extends GUIComponent implements IGUIEventListener {
    // discriminated union for mode
    public sealed interface Mode permits Mode.CREATE, Mode.EDIT {
        record CREATE() implements Mode {
        }

        record EDIT(IDepictable data) implements Mode {
        }

        Mode CREATE = new Mode.CREATE();

        static Mode EDIT(IDepictable data) {
            return new EDIT(data);
        }
    }

    public record ResponsibleGuestSelectPayload(List<? extends IDepictable> selectedGuests, IDepictable selectedResponsibleGuest) {
    }

    public record GuestDeletePayload(
            List<? extends IDepictable> selectedGuests,
            IDepictable deletedGuest,
            Optional<? extends IDepictable> responsibleGuest
    ) {
    }

    public record ServiceEditPayload(List<? extends IDepictable> selectedServices, IDepictable serviceToEdit) {
    }

    public record ServiceDeletePayload(List<? extends IDepictable> selectedServices, IDepictable serviceToDelete) {
    }

    public record EquipmentEditPayload(List<? extends IDepictable> rentedEquipment, IDepictable equipment) {
    }

    public record EquipmentDeletePayload(List<? extends IDepictable> rentedEquipment, IDepictable equipmentToDelete) {
    }

    public record ChipCardDeletePayload(List<? extends IDepictable> selectedChipCards, IDepictable chipCardToDelete) {
    }

    public record SavePayload(
            Optional<LocalDateTime> arrivalDate,
            Optional<LocalDateTime> departureDate,
            List<? extends IDepictable> associatedGuests,
            Optional<? extends IDepictable> responsibleGuest,
            List<? extends IDepictable> bookedServices,
            List<? extends IDepictable> rentedEquipment,
            IDepictable bookedPitch,
            List<? extends IDepictable> chipCards,
            Mode mode
    ) {
        public static SavePayload create(final BookingChangeComponent component) {
            final var arrivalDate = component.tryOptional(() -> LocalDateTime.parse(
                    component.arrivalDateComponent.getValueAsString(),
                    component.dateTimeFormatter
            ));
            final var departureDate = component.tryOptional(() -> LocalDateTime.parse(
                    component.departureDateComponent.getValueAsString(),
                    component.dateTimeFormatter
            ));

            return new SavePayload(
                    arrivalDate,
                    departureDate,
                    component.associatedGuests.stream().filter(g -> !g.equals(component.responsibleGuest.orElse(null))).collect(Collectors.toList()),
                    component.responsibleGuest,
                    component.bookedServices,
                    component.rentedEquipment,
                    (IDepictable) component.pitchSelector.getValue(),
                    component.selectedChipCards,
                    component.mode
            );
        }
    }

    public record GuestListPayload(List<? extends IDepictable> guests, Optional<? extends IDepictable> responsibleGuest) {
    }


    public enum Commands implements EventCommand {
        // outgoing gui events
        BUTTON_PRESSED_ADD_GUEST("BookingChangeComponent::BUTTON_PRESSED_ADD_GUEST", GuestListPayload.class),
        RADIO_BUTTON_PRESSED_SELECT_RESPONSIBLE_GUEST("BookingChangeComponent::RADIO_BUTTON_PRESSED_SELECT_RESPONSIBLE_GUEST",
                ResponsibleGuestSelectPayload.class),
        BUTTON_PRESSED_DELETE_GUEST("BookingChangeComponent::BUTTON_PRESSED_DELETE_GUEST", GuestDeletePayload.class),
        BUTTON_PRESSED_ADD_SERVICE("BookingChangeComponent::ADD_SERVICE", List.class),
        BUTTON_PRESSED_EDIT_SERVICE("BookingChangeComponent::BUTTON_PRESSED_EDIT_SERVICE", ServiceEditPayload.class),
        BUTTON_PRESSED_DELETE_SERVICE("BookingChangeComponent::BUTTON_PRESSED_DELETE_SERVICE", ServiceDeletePayload.class),
        BUTTON_PRESSED_ADD_EQUIPMENT("BookingChangeComponent::ADD_EQUIPMENT"),
        BUTTON_PRESSED_INCREMENT_EQUIPMENT_COUNT("BookingChangeComponent::BUTTON_PRESSED_INCREMENT_EQUIPMENT_COUNT", EquipmentEditPayload.class),
        BUTTON_PRESSED_DECREMENT_EQUIPMENT_COUNT("BookingChangeComponent::BUTTON_PRESSED_DECREMENT_EQUIPMENT_COUNT", EquipmentEditPayload.class),
        BUTTON_PRESSED_DELETE_EQUIPMENT("BookingChangeComponent::BUTTON_PRESSED_DELETE_EQUIPMENT", IDepictable.class),
        BUTTON_PRESSED_SELECT_START_DATE("BookingChangeComponent::BUTTON_PRESSED_SELECT_START_DATE", Optional.class),
        BUTTON_PRESSED_SELECT_END_DATE("BookingChangeComponent::SELECT_END_DATE", Optional.class),
        BUTTON_PRESSED_SELECT_PITCH("BookingChangeComponent::BUTTON_PRESSED_SELECT_PITCH"),
        BUTTON_PRESSED_SELECT_CHIPCARD("BookingChangeComponent::BUTTON_PRESSED_SELECT_CHIPCARD", IDepictable.class),
        BUTTON_PRESSED_DELETE_CHIPCARD("BookingChangeComponent::BUTTON_PRESSED_DELETE_CHIPCARD", ChipCardDeletePayload.class),
        BUTTON_PRESSED_DELETE_BOOKING("BookingChangeComponent::BUTTON_PRESSED_DELETE_BOOKING", IDepictable.class),
        BUTTON_PRESSED_SAVE_BOOKING("BookingChangeComponent::BUTTON_PRESSED_SAVE_BOOKING", SavePayload.class),
        BUTTON_PRESSED_CANCEL("BookingChangeComponent::BUTTON_PRESSED_CANCEL"),
        // incoming update events
        SET_MODE("BookingChangeComponent::SET_MODE", Mode.class),
        SET_START_DATE("BookingChangeComponent::SET_START_DATE", Temporal.class),
        SET_END_DATE("BookingChangeComponent::SET_END_DATE", Temporal.class),
        SET_PITCH("BookingChangeComponent::SET_PITCH", IDepictable.class),
        ADD_ASSOCIATED_GUEST("BookingChangeComponent::ADD_ASSOCIATED_GUEST", IDepictable.class),
        SET_ASSOCIATED_GUESTS("BookingChangeComponent::SET_ASSOCIATED_GUESTS", GuestListPayload.class),
        ADD_BOOKED_SERVICE("BookingChangeComponent::ADD_BOOKED_SERVICE", IDepictable.class),
        SET_BOOKED_SERVICES("BookingChangeComponent::SET_BOOKED_SERVICES", List.class),
        ADD_RENTED_EQUIPMENT("BookingChangeComponent::ADD_RENTED_EQUIPMENT", IDepictable.class),
        SET_RENTED_EQUIPMENT("BookingChangeComponent::SET_RENTED_EQUIPMENT", List.class),
        ADD_SELECTED_CHIPCARD("BookingChangeComponent::ADD_SELECTED_CHIPCARD", IDepictable.class),
        SET_SELECTED_CHIPCARDS("BookingChangeComponent::SET_SELECTED_CHIPCARDS", List.class),
        RESET_INPUT("BookingChangeComponent::RESET_INPUT"),
        // incoming update events to manage errors
        ERRORS_SHOW_START_DATE("BookingChangeComponent::ERRORS_SHOW_START_DATE", String.class),
        ERRORS_SHOW_END_DATE("BookingChangeComponent::ERRORS_SHOW_END_DATE", String.class),
        ERRORS_SHOW_PITCH("BookingChangeComponent::ERRORS_SHOW_PITCH", String.class),
        ERRORS_SHOW_GUEST("BookingChangeComponent::ERRORS_SHOW_GUEST", String.class),
        ERRORS_SHOW_SERVICES("BookingChangeComponent::ERRORS_SHOW_SERVICES", String.class),
        ERRORS_RESET("BookingChangeComponent::ERRORS_RESET");

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

    private static final String BUTTON_COMPONENT_ID = "BookingChangeComponent::BUTTON_COMPONENT_ID";
    private static final String DELETE_BOOKING_BUTTON_ELEMENT_ID = "BookingChangeComponent::DELETE_BOOKING_BUTTON_ELEMENT_ID";
    private static final String SAVE_BOOKING_BUTTON_ELEMENT_ID = "BookingChangeComponent::SAVE_BOOKING_BUTTON_ELEMENT_ID";
    private static final String CANCEL_BUTTON_ELEMENT_ID = "BookingChangeComponent::CANCEL_BUTTON_ELEMENT_ID";
    private static final String ADD_GUEST_BUTTON_ELEMENT_ID = "BookingChangeComponent::ADD_GUEST_BUTTON_ELEMENT_ID";
    private static final String ADD_SERVICE_BUTTON_ELEMENT_ID = "BookingChangeComponent::ADD_SERVICE_BUTTON_ELEMENT_ID";
    private static final String ADD_EQUIPMENT_BUTTON_ELEMENT_ID = "BookingChangeComponent::ADD_EQUIPMENT_BUTTON_ELEMENT_ID";
    private static final String BOOKING_PERIOD_FROM_ATTRIBUTE_ELEMENT_ID = "BookingChangeComponent::BOOKING_PERIOD_FROM_ATTRIBUTE_ELEMENT_ID";
    private static final String BOOKING_PERIOD_TO_ATTRIBUTE_ELEMENT_ID = "BookingChangeComponent::BOOKING_PERIOD_TO_ATTRIBUTE_ELEMENT_ID";
    private static final String CHIPCARD_ATTRIBUTE_ELEMENT_ID = "BookingChangeComponent::CHIPCARD_ATTRIBUTE_ELEMENT_ID";
    private static final String PITCH_ATTRIBUTE_ELEMENT_ID = "BookingChangeComponent::PITCH_ATTRIBUTE_ELEMENT_ID";
    private static final String SELECT_PITCH_BUTTON_ELEMENT_ID = "BookingChangeComponent::SELECT_PITCH_BUTTON_ELEMENT_ID";

    // Data
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm", Locale.GERMANY);
    private Mode mode = Mode.CREATE;
    private List<? extends IDepictable> availablePitches = new ArrayList<>();
    private List<? extends IDepictable> allChipCards = new ArrayList<>();
    private List<? extends IDepictable> availableChipCards = new ArrayList<>();
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private Optional<? extends IDepictable> responsibleGuest = Optional.empty();
    private List<? extends IDepictable> associatedGuests = new ArrayList<>();
    private List<? extends IDepictable> bookedServices = new ArrayList<>();
    private List<? extends IDepictable> rentedEquipment = new ArrayList<>();
    private List<? extends IDepictable> selectedChipCards = new ArrayList<>();

    // Components
    private AttributeElement arrivalDateComponent;
    private AttributeElement departureDateComponent;
    private AttributeElement chipCardSelector;
    private AttributeElement pitchSelector;
    private JPanel guestTable;
    private JPanel serviceTable;
    private JPanel chipCardTable;
    private JPanel equipmentTable;
    private ButtonElement deleteBookingButton;
    private ButtonElement cancelBookingButton;
    private ButtonElement saveBookingButton;
    private ButtonComponent buttonComponent;
    // Error Components
    private JLabel startDateErrorComponent;
    private JLabel endDateErrorComponent;
    private JLabel guestErrorComponent;
    private JLabel pitchErrorComponent;
    private JLabel serviceErrorComponent;


    public BookingChangeComponent(
            final ReadonlyConfiguration config
    ) {
        super("BookingChangeComponent", config);
        this.initUI();
    }

    @Override
    public void processGUIEvent(final GUIEvent guiEvent) {
        // Component in tab and tab is closed
        if (guiEvent.getSource() instanceof GUIBuchung && guiEvent.getCmd() == GUIBuchung.Commands.BUTTON_PRESSED_TAB_CLOSING) {
            this.fireGUIEvent(new GUIEvent(this, Commands.BUTTON_PRESSED_CANCEL, this.mode));
            return;
        }

        if (guiEvent.getSource() instanceof ObservableComponent component) {
            final var id = component.getID();
            switch (id) {
                case ADD_GUEST_BUTTON_ELEMENT_ID -> this.fireGUIEvent(new GUIEvent(
                        this,
                        Commands.BUTTON_PRESSED_ADD_GUEST,
                        new GuestListPayload(
                                this.associatedGuests,
                                this.responsibleGuest
                        )
                ));
                case ADD_SERVICE_BUTTON_ELEMENT_ID -> this.fireGUIEvent(new GUIEvent(this, Commands.BUTTON_PRESSED_ADD_SERVICE));
                case ADD_EQUIPMENT_BUTTON_ELEMENT_ID -> this.fireGUIEvent(new GUIEvent(this, Commands.BUTTON_PRESSED_ADD_EQUIPMENT));
                case BOOKING_PERIOD_FROM_ATTRIBUTE_ELEMENT_ID -> {
                    if (guiEvent.getCmd() != AttributeElement.Commands.BUTTON_PRESSED) {
                        return;
                    }
                    final var data = ((AttributeElement) guiEvent.getData()).getValueAsString();
                    final var startDate = tryOptional(() -> LocalDateTime.parse(data, dateTimeFormatter));
                    this.fireGUIEvent(new GUIEvent(this, Commands.BUTTON_PRESSED_SELECT_START_DATE, startDate));

                }
                case BOOKING_PERIOD_TO_ATTRIBUTE_ELEMENT_ID -> {
                    if (guiEvent.getCmd() != AttributeElement.Commands.BUTTON_PRESSED) {
                        return;
                    }
                    final var data = ((AttributeElement) guiEvent.getData()).getValueAsString();
                    final var endDate = tryOptional(() -> LocalDateTime.parse(data, dateTimeFormatter));
                    this.fireGUIEvent(new GUIEvent(this, Commands.BUTTON_PRESSED_SELECT_END_DATE, endDate));
                }
                case CHIPCARD_ATTRIBUTE_ELEMENT_ID -> {
                    final var value = ((AttributeElement) guiEvent.getData()).getValue();
                    if (value instanceof String str && str.isEmpty()) {
                        return;
                    }

                    this.fireGUIEvent(new GUIEvent(this, Commands.BUTTON_PRESSED_SELECT_CHIPCARD, value));
                }
                case SAVE_BOOKING_BUTTON_ELEMENT_ID -> this.fireGUIEvent(new GUIEvent(
                        this,
                        Commands.BUTTON_PRESSED_SAVE_BOOKING,
                        SavePayload.create(this))
                );
                case CANCEL_BUTTON_ELEMENT_ID -> this.fireGUIEvent(new GUIEvent(this, Commands.BUTTON_PRESSED_CANCEL, this.mode));
                case DELETE_BOOKING_BUTTON_ELEMENT_ID -> this.fireGUIEvent(new GUIEvent(
                        this,
                        Commands.BUTTON_PRESSED_DELETE_BOOKING,
                        ((Mode.EDIT) this.mode).data())
                );
                case SELECT_PITCH_BUTTON_ELEMENT_ID -> this.fireGUIEvent(new GUIEvent(this, Commands.BUTTON_PRESSED_SELECT_PITCH));
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void processUpdateEvent(final UpdateEvent updateEvent) {
        // handle events from gui controller
        if (updateEvent.getCmd() instanceof GUIController.Commands command) {
            switch (command) {
                case UPDATE_PITCHES -> {
                    this.availablePitches = (List<? extends IDepictable>) updateEvent.getData();
                    this.pitchSelector.setData(this.availablePitches.toArray(new IDepictable[0]));
                }
                case UPDATE_CHIPCARDS -> {
                    this.allChipCards = ((List<? extends IDepictable>) updateEvent.getData());
                    // available chip cards is just a derived attribute
                    this.availableChipCards = this.allChipCards.stream()
                            .filter(c -> !this.selectedChipCards.contains(c))
                            .sorted()
                            .collect(Collectors.toList());
                    this.chipCardSelector.setData(Stream.concat(Stream.of(""), this.availableChipCards.stream()).toArray(Object[]::new));
                    this.chipCardSelector.setEnabled(this.availableChipCards.size() > 0);
                }
            }
        }
        // handle own events
        else if (updateEvent.getCmd() instanceof Commands command) {
            switch (command) {
                // guest selection
                case ADD_ASSOCIATED_GUEST -> {
                    ((List<IDepictable>) this.associatedGuests).add((IDepictable) updateEvent.getData());
                    this.associatedGuests = this.associatedGuests.stream().sorted().collect(Collectors.toList());
                    this.buildGuestTable();
                }
                case SET_ASSOCIATED_GUESTS -> {
                    final var payload = (GuestListPayload) updateEvent.getData();
                    this.associatedGuests = payload.guests().stream().sorted().collect(Collectors.toList());
                    this.responsibleGuest = payload.responsibleGuest();
                    this.buildGuestTable();
                }
                // service selection
                case ADD_BOOKED_SERVICE -> {
                    ((List<IDepictable>) this.bookedServices).add((IDepictable) updateEvent.getData());
                    this.bookedServices = this.bookedServices.stream().sorted().collect(Collectors.toList());
                    this.buildServiceTable();
                }
                case SET_BOOKED_SERVICES -> {
                    this.bookedServices = ((List<? extends IDepictable>) updateEvent.getData())
                            .stream()
                            .sorted()
                            .collect(Collectors.toList());
                    this.buildServiceTable();
                }
                // equipment selection
                case ADD_RENTED_EQUIPMENT -> {
                    ((List<IDepictable>) this.rentedEquipment).add((IDepictable) updateEvent.getData());
                    this.rentedEquipment = this.rentedEquipment.stream().sorted().collect(Collectors.toList());
                    this.buildEquipmentTable();
                }
                case SET_RENTED_EQUIPMENT -> {
                    this.rentedEquipment = ((List<? extends IDepictable>) updateEvent.getData())
                            .stream()
                            .sorted()
                            .collect(Collectors.toList());
                    this.buildEquipmentTable();
                }
                // date selection
                case SET_START_DATE -> {
                    if (updateEvent.getData() instanceof LocalDate localDate) {
                        final var date = LocalDateTime.of(localDate, LocalTime.of(0, 0));
                        this.arrivalDateComponent.setValue(date.format(this.dateTimeFormatter));
                    } else if (updateEvent.getData() instanceof LocalDateTime date) {
                        this.arrivalDateComponent.setValue(date.format(this.dateTimeFormatter));
                    } else throw new IllegalArgumentException(String.valueOf(updateEvent.getData()));
                }
                case SET_END_DATE -> {
                    if (updateEvent.getData() instanceof LocalDate localDate) {
                        final var date = LocalDateTime.of(localDate, LocalTime.of(23, 59));
                        this.departureDateComponent.setValue(date.format(this.dateTimeFormatter));
                    } else if (updateEvent.getData() instanceof LocalDateTime date) {
                        this.departureDateComponent.setValue(date.format(this.dateTimeFormatter));
                    } else throw new IllegalArgumentException(String.valueOf(updateEvent.getData()));
                }
                // pitch selection
                case SET_PITCH -> this.pitchSelector.setValue(updateEvent.getData());
                // chip card selection
                case ADD_SELECTED_CHIPCARD -> {
                    ((List<IDepictable>) this.selectedChipCards).add((IDepictable) updateEvent.getData());
                    this.selectedChipCards = this.selectedChipCards.stream().sorted().collect(Collectors.toList());
                    // update derived attribute
                    this.availableChipCards = this.allChipCards.stream()
                            .filter(c -> !this.selectedChipCards.contains(c))
                            .sorted()
                            .collect(Collectors.toList());
                    this.chipCardSelector.setData(Stream.concat(Stream.of(""), this.availableChipCards.stream()).toArray(Object[]::new));
                    this.chipCardSelector.setEnabled(this.availableChipCards.size() > 0);
                    this.buildChipCardTable();
                }
                case SET_SELECTED_CHIPCARDS -> {
                    this.selectedChipCards = (List<IDepictable>) updateEvent.getData();
                    // update derived attribute
                    this.availableChipCards = this.allChipCards.stream()
                            .filter(c -> !this.selectedChipCards.contains(c))
                            .sorted()
                            .collect(Collectors.toList());
                    this.chipCardSelector.setData(Stream.concat(Stream.of(""), this.availableChipCards.stream()).toArray(Object[]::new));
                    this.chipCardSelector.setEnabled(this.availableChipCards.size() > 0);
                    this.buildChipCardTable();
                }
                // errors
                case ERRORS_SHOW_START_DATE -> {
                    this.startDateErrorComponent.setText("<html>%s</html>".formatted(((String) updateEvent.getData()).replaceAll("\n", "<br>")));
                    this.revalidate();
                }
                case ERRORS_SHOW_END_DATE -> {
                    this.endDateErrorComponent.setText("<html>%s</html>".formatted(((String) updateEvent.getData()).replaceAll("\n", "<br>")));
                    this.revalidate();
                }
                case ERRORS_SHOW_PITCH -> {
                    this.pitchErrorComponent.setText("<html>%s</html>".formatted(((String) updateEvent.getData()).replaceAll("\n", "<br>")));
                    this.revalidate();
                }
                case ERRORS_SHOW_GUEST -> {
                    this.guestErrorComponent.setText("<html>%s</html>".formatted(((String) updateEvent.getData()).replaceAll("\n", "<br>")));
                    this.revalidate();
                }
                case ERRORS_SHOW_SERVICES -> {
                    this.serviceErrorComponent.setText("<html>%s</html>".formatted(((String) updateEvent.getData()).replaceAll("\n", "<br>")));
                    this.revalidate();
                }
                case ERRORS_RESET -> {
                    this.resetErrors();
                    this.revalidate();
                }
                // other
                case SET_MODE -> {
                    final var newMode = (Mode) updateEvent.getData();
                    if (this.mode == newMode) {
                        return;
                    }

                    this.mode = newMode;
                    this.buildButtonComponentButtons();
                    this.resetInput();
                }
                case RESET_INPUT -> this.resetInput();
                default -> throw new IllegalArgumentException(String.valueOf(updateEvent));
            }
        }

    }

    public void resetInput() {
        this.associatedGuests.clear();
        this.responsibleGuest = Optional.empty();
        this.bookedServices.clear();
        this.rentedEquipment.clear();
        this.arrivalDateComponent.setValue("");
        this.departureDateComponent.setValue("");
        this.pitchSelector.setData(this.availablePitches);
        this.pitchSelector.setValue(null);
        this.availableChipCards = Stream.concat(this.availableChipCards.stream(), this.selectedChipCards.stream()).sorted().toList();
        this.selectedChipCards.clear();

        this.buildGuestTable();
        this.buildServiceTable();
        this.buildEquipmentTable();
        this.buildChipCardTable();
        this.resetErrors();
        this.repaint();
    }

    private void buildButtonComponentButtons() {
        if (this.mode instanceof Mode.CREATE) {
            this.cancelBookingButton.setButtonText("Erstellen abbrechen");
            this.saveBookingButton.setButtonText("Buchung erstellen");
            this.saveBookingButton.setToolTipText("Erstellt die neue Buchung mit den eingegebenen Daten");
            this.buttonComponent.replaceButtons(new ButtonElement[] {
                    this.cancelBookingButton,
                    this.saveBookingButton
            });
        } else {
            this.cancelBookingButton.setButtonText("Bearbeiten abbrechen");
            this.saveBookingButton.setButtonText("Buchung aktualisieren");
            this.saveBookingButton.setToolTipText("Aktualisiert die Buchung mit den neuen eingegebenen Daten");
            this.buttonComponent.replaceButtons(new ButtonElement[] {
                    this.deleteBookingButton,
                    this.cancelBookingButton,
                    this.saveBookingButton
            });
        }
        final var border = BorderFactory.createTitledBorder(this.mode instanceof Mode.CREATE ? "Neue Buchung anlegen" : "Buchung bearbeiten");
        border.setTitleColor(this.config.getTextColor());
        border.setTitleFont(this.config.getHeaderFont());
        this.buttonComponent.setBorder(border);
        this.buttonComponent.setForeground(this.config.getTextColor());
        this.buttonComponent.setBackground(this.config.getBackgroundColor());
        this.buttonComponent.getComponent(0).setBackground(this.config.getBackgroundColor());
        this.buttonComponent.getComponent(0).setForeground(this.config.getTextColor());
        this.buttonComponent.getComponent(1).setBackground(this.config.getBackgroundColor());
        this.buttonComponent.getComponent(1).setForeground(this.config.getTextColor());
        this.revalidate();
    }

    private void buildChipCardTable() {
        this.chipCardTable.removeAll();

        final var columns = new String[] {"Nummer", "Status", " "};
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
            this.chipCardTable.add(label, new GridBagConstraints(
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

        for (var i = 0; i < this.selectedChipCards.size(); ++i) {
            final var chipCard = this.selectedChipCards.get(i);
            final var attributes = chipCard.getAttributeArray();

            for (var j = 0; j < attributes.length && j < 2; ++j) {
                final var attribute = attributes[j];
                final var text = new JLabel();
                text.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(this.config.getTextColor(), 1),
                        BorderFactory.createEmptyBorder(2, 10, 2, 10)
                ));
                text.setHorizontalAlignment(SwingConstants.CENTER);
                text.setText(String.valueOf(attribute.getValue()));
                text.setOpaque(true);
                text.setFont(this.config.getFont());
                text.setForeground(this.config.getTextColor());
                text.setBackground(this.config.getBackgroundColor());
                if (j == 1) {
                    if (attribute.getDefaultValue().equals(attribute.getValue())) {
                        text.setBackground(this.config.getSuccessColor());
                    } else {
                        text.setBackground(this.config.getFailureColor());
                    }
                }

                // @formatter:off
                this.chipCardTable.add(text, new GridBagConstraints(j, i + 1, 1, 1, 1d - j * 0.25d, 0d, GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
                // @formatter:on
            }

            final var deleteButton = new JButton();
            deleteButton.setText("Löschen");
            deleteButton.setToolTipText("Löscht die Chipkarte aus der Buchung");
            deleteButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            deleteButton.setFont(this.config.getFont());
            deleteButton.setForeground(this.config.getTextColor());
            deleteButton.setBackground(this.config.getFailureColor());
            deleteButton.setSize(GUIConstants.DimSizes.DEFAULT_BUTTON_SIZE.getValue());
            deleteButton.addActionListener(e -> this.fireGUIEvent(new GUIEvent(
                    this,
                    Commands.BUTTON_PRESSED_DELETE_CHIPCARD,
                    new ChipCardDeletePayload(this.selectedChipCards, chipCard)
            )));
            deleteButton.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(this.config.getTextColor(), 1),
                    new EmptyBorder(0, 10, 0, 10)
            ));

            // @formatter:off
            this.chipCardTable.add(deleteButton, new GridBagConstraints(2, i + 1, 1, 1, 0d, 0d, GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
            // @formatter:on
        }
        this.revalidate();
    }

    private void buildEquipmentTable() {
        this.equipmentTable.removeAll();

        final var columns = new String[] {"Nummer", "Anzahl", " "};
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
            this.equipmentTable.add(label, new GridBagConstraints(
                    i > 1 ? i + 1 : i,
                    0,
                    i == 1 ? 2 : 1,
                    1,
                    i == 0 ? 1d : 0d,
                    0d,
                    GridBagConstraints.NORTH,
                    GridBagConstraints.HORIZONTAL,
                    new Insets(0, 0, 0, 0), 0,
                    0
            ));
        }

        for (var i = 0; i < this.rentedEquipment.size(); ++i) {
            final var equipment = this.rentedEquipment.get(i);

            final var text = new JLabel();
            text.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(this.config.getTextColor(), 1),
                    BorderFactory.createEmptyBorder(2, 10, 2, 10)
            ));
            text.setText(equipment.getVisibleText());
            text.setFont(this.config.getFont());
            text.setForeground(this.config.getTextColor());
            text.setBackground(this.config.getBackgroundColor());

            final var attribute = equipment.getAttributeArray()[2];
            final var decrementButton = new JButton();
            decrementButton.setText("-");
            decrementButton.setToolTipText("Verringert die Anzahl des Equipments um 1");
            decrementButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            decrementButton.setFont(this.config.getFont());
            decrementButton.setForeground(this.config.getTextColor());
            decrementButton.setBackground(this.config.getSecondaryBackgroundColor());
            decrementButton.setSize(GUIConstants.DimSizes.DEFAULT_BUTTON_SIZE.getValue());
            decrementButton.setEnabled(!attribute.getValue().equals(attribute.getDefaultValue()));
            decrementButton.addActionListener(e -> this.fireGUIEvent(new GUIEvent(
                    this,
                    Commands.BUTTON_PRESSED_DECREMENT_EQUIPMENT_COUNT,
                    new EquipmentEditPayload(this.rentedEquipment, equipment)
            )));
            decrementButton.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(this.config.getTextColor(), 1),
                    new EmptyBorder(0, 10, 0, 10)
            ));

            final var incrementButton = new JButton();
            incrementButton.setText("+");
            incrementButton.setToolTipText("Erhöht die Anzahl des Equipments um 1");
            incrementButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            incrementButton.setFont(this.config.getFont());
            incrementButton.setForeground(this.config.getTextColor());
            incrementButton.setBackground(this.config.getSecondaryBackgroundColor());
            incrementButton.setSize(GUIConstants.DimSizes.DEFAULT_BUTTON_SIZE.getValue());
            incrementButton.addActionListener(e -> this.fireGUIEvent(new GUIEvent(
                    this,
                    Commands.BUTTON_PRESSED_INCREMENT_EQUIPMENT_COUNT,
                    new EquipmentEditPayload(this.rentedEquipment, equipment)
            )));
            incrementButton.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(this.config.getTextColor(), 1),
                    new EmptyBorder(0, 10, 0, 10)
            ));

            final var deleteButton = new JButton();
            deleteButton.setText("Löschen");
            deleteButton.setToolTipText("Löscht die Ausrüstung aus der Buchung");
            deleteButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            deleteButton.setFont(this.config.getFont());
            deleteButton.setForeground(this.config.getTextColor());
            deleteButton.setBackground(this.config.getFailureColor());
            deleteButton.setSize(GUIConstants.DimSizes.DEFAULT_BUTTON_SIZE.getValue());
            deleteButton.addActionListener(e -> this.fireGUIEvent(new GUIEvent(
                    this,
                    Commands.BUTTON_PRESSED_DELETE_EQUIPMENT,
                    new EquipmentDeletePayload(this.rentedEquipment, equipment)
            )));
            deleteButton.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(this.config.getTextColor(), 1),
                    new EmptyBorder(0, 10, 0, 10)
            ));

            // @formatter:off
            this.equipmentTable.add(text,            new GridBagConstraints(0, i + 1, 1, 1, 1d, 0d, GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
            this.equipmentTable.add(decrementButton, new GridBagConstraints(1, i + 1, 1, 1, 0d, 0d, GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
            this.equipmentTable.add(incrementButton, new GridBagConstraints(2, i + 1, 1, 1, 0d, 0d, GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
            this.equipmentTable.add(deleteButton,    new GridBagConstraints(3, i + 1, 1, 1, 0d, 0d, GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
            // @formatter:on
        }
        this.revalidate();
    }

    private void buildGuestTable() {
        this.guestTable.removeAll();
        final var guestButtonGroup = new ButtonGroup();

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

        for (var i = 0; i < this.associatedGuests.size(); ++i) {
            final var guest = this.associatedGuests.get(i);

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
            guestButtonGroup.add(radioButton);
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
                    Commands.RADIO_BUTTON_PRESSED_SELECT_RESPONSIBLE_GUEST,
                    new ResponsibleGuestSelectPayload(this.associatedGuests, guest)
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
                    Commands.BUTTON_PRESSED_DELETE_GUEST,
                    new GuestDeletePayload(this.associatedGuests, guest, this.responsibleGuest)
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

    private void buildServiceTable() {
        this.serviceTable.removeAll();

        final var columns = new String[] {"Leistung", " ", " "};
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
            this.serviceTable.add(label, new GridBagConstraints(
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

        for (var i = 0; i < this.bookedServices.size(); ++i) {
            final var service = this.bookedServices.get(i);

            final var text = new JLabel();
            text.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(this.config.getTextColor(), 1),
                    BorderFactory.createEmptyBorder(2, 10, 2, 10)
            ));
            text.setText(service.getVisibleText());
            text.setFont(this.config.getFont());
            text.setForeground(this.config.getTextColor());
            text.setBackground(this.config.getBackgroundColor());

            final var editButton = new JButton();
            editButton.setText("Bearbeiten");
            editButton.setToolTipText("Diese Leistung bearbeiten");
            editButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            editButton.setFont(this.config.getFont());
            editButton.setForeground(this.config.getTextColor());
            editButton.setBackground(this.config.getSecondaryBackgroundColor());
            editButton.setSize(GUIConstants.DimSizes.DEFAULT_BUTTON_SIZE.getValue());
            editButton.addActionListener(e -> this.fireGUIEvent(new GUIEvent(
                    this,
                    Commands.BUTTON_PRESSED_EDIT_SERVICE,
                    new ServiceEditPayload(this.bookedServices, service)
            )));
            editButton.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(this.config.getTextColor(), 1),
                    new EmptyBorder(0, 10, 0, 10)
            ));

            final var deleteButton = new JButton();
            deleteButton.setText("Löschen");
            deleteButton.setToolTipText("Löscht die Leistung aus der Buchung");
            deleteButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            deleteButton.setFont(this.config.getFont());
            deleteButton.setForeground(this.config.getTextColor());
            deleteButton.setBackground(this.config.getFailureColor());
            deleteButton.setSize(GUIConstants.DimSizes.DEFAULT_BUTTON_SIZE.getValue());
            deleteButton.addActionListener(e -> this.fireGUIEvent(new GUIEvent(
                    this,
                    Commands.BUTTON_PRESSED_DELETE_SERVICE,
                    new ServiceDeletePayload(this.bookedServices, service)
            )));
            deleteButton.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(this.config.getTextColor(), 1),
                    new EmptyBorder(0, 10, 0, 10)
            ));

            // @formatter:off
            this.serviceTable.add(text,             new GridBagConstraints(0, i + 1, 1, 1, 1d, 0d, GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
            this.serviceTable.add(editButton, new GridBagConstraints(1, i + 1, 1, 1, 0d, 0d, GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
            this.serviceTable.add(deleteButton,     new GridBagConstraints(2, i + 1, 1, 1, 0d, 0d, GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
            // @formatter:on
        }
        this.revalidate();
    }

    private JComponent createBookingTimeSpan() {
        this.arrivalDateComponent = AttributeElement
                .builder(BOOKING_PERIOD_FROM_ATTRIBUTE_ELEMENT_ID)
                .labelName("Anreisedatum")
                .toolTip("Angabe des Anreisedatums der Buchung (Format: dd.MM.yyyy HH:mm)")
                .textFieldFont(this.config.getFont())
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
                .data("Kalenderauswahl")
                .actionElementSize(new Dimension(120, GUIConstants.IntSizes.DEFAULT_BUTTON_HEIGHT.getValue()))
                .actionType(AttributeElement.ActionType.BUTTON)
                .actionElementFont(this.config.getFont())
                .actionElementTextColor(this.config.getTextColor())
                .actionElementBackgroundColor(this.config.getSecondaryBackgroundColor())
                .actionElementInsets(new Insets(0, 0, 0, 0))
                .build();
        this.arrivalDateComponent.addObserver(this);

        this.departureDateComponent = AttributeElement
                .builder(BOOKING_PERIOD_TO_ATTRIBUTE_ELEMENT_ID)
                .labelName("Abreisedatum")
                .toolTip("Angabe des Abreisedatums der Buchung (Format: dd.MM.yyyy HH:mm)")
                .textFieldFont(this.config.getFont())
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
                .data("Kalenderauswahl")
                .actionElementSize(new Dimension(120, GUIConstants.IntSizes.DEFAULT_BUTTON_HEIGHT.getValue()))
                .actionType(AttributeElement.ActionType.BUTTON)
                .actionElementFont(this.config.getFont())
                .actionElementTextColor(this.config.getTextColor())
                .actionElementBackgroundColor(this.config.getSecondaryBackgroundColor())
                .actionElementInsets(new Insets(0, 0, 0, 0))
                .build();
        this.departureDateComponent.addObserver(this);

        final var attributeComponent = AttributeComponent.builder(super.generateRandomID())
                .attributeElements(new AttributeElement[] {this.arrivalDateComponent, this.departureDateComponent})
                .build();
        attributeComponent.setBackground(this.config.getBackgroundColor());
        attributeComponent.setForeground(this.config.getTextColor());
        attributeComponent.setFont(this.config.getFont());
        attributeComponent.getComponent(0).setBackground(this.config.getBackgroundColor());
        attributeComponent.getComponent(0).setForeground(this.config.getTextColor());
        attributeComponent.getComponent(0).setFont(this.config.getFont());
        super.colorizeAttributeComponent(attributeComponent);

        this.startDateErrorComponent = super.createErrorLabel();
        this.endDateErrorComponent = super.createErrorLabel();
        final var errorWrapper = super.createErrorWrapper(this.startDateErrorComponent, this.endDateErrorComponent);
        errorWrapper.add(attributeComponent);

        return errorWrapper;
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
                .actionElementSize(new Dimension(120, GUIConstants.IntSizes.DEFAULT_BUTTON_HEIGHT.getValue()))
                .actionType(AttributeElement.ActionType.COMBOBOX)
                .actionElementFont(this.config.getFont())
                .actionElementTextColor(this.config.getTextColor())
                .actionElementBackgroundColor(this.config.getSecondaryBackgroundColor())
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
        super.colorizeAttributeComponent(selectorComponent);
        panel.add(selectorComponent,
                new GridBagConstraints(0, 0, 1, 1, 1d, 0d, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));


        this.chipCardTable = new JPanel();
        this.chipCardTable.setBorder(new EmptyBorder(10, 2, 10, 2));
        this.chipCardTable.setForeground(this.config.getTextColor());
        this.chipCardTable.setBackground(this.config.getBackgroundColor());
        this.chipCardTable.setLayout(new GridBagLayout());
        this.chipCardTable.setOpaque(true);

        this.buildChipCardTable();

        panel.add(this.chipCardTable,
                new GridBagConstraints(0, 1, 1, 1, 1d, 1d, GridBagConstraints.SOUTH, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));

        return panel;
    }

    private JComponent createEquipmentTable() {
        this.equipmentTable = new JPanel();
        this.equipmentTable.setBorder(new EmptyBorder(10, 2, 10, 2));
        this.equipmentTable.setForeground(this.config.getTextColor());
        this.equipmentTable.setBackground(this.config.getBackgroundColor());
        this.equipmentTable.setLayout(new GridBagLayout());
        this.equipmentTable.setOpaque(true);

        this.buildEquipmentTable();

        return equipmentTable;
    }

    private JComponent createGuestTable() {
        this.guestTable = new JPanel();
        this.guestTable.setBorder(new EmptyBorder(10, 2, 10, 2));
        this.guestTable.setForeground(this.config.getTextColor());
        this.guestTable.setBackground(this.config.getBackgroundColor());
        this.guestTable.setLayout(new GridBagLayout());
        this.guestTable.setOpaque(true);

        this.guestErrorComponent = super.createErrorLabel();
        final var errorWrapper = super.createErrorWrapper(this.guestErrorComponent);
        errorWrapper.add(this.guestTable);

        this.buildGuestTable();

        return errorWrapper;
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
                .actionElementSize(new Dimension(120, GUIConstants.IntSizes.DEFAULT_BUTTON_HEIGHT.getValue()))
                .actionType(AttributeElement.ActionType.COMBOBOX)
                .actionElementFont(this.config.getFont())
                .actionElementTextColor(this.config.getTextColor())
                .actionElementBackgroundColor(this.config.getSecondaryBackgroundColor())
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
        super.colorizeAttributeComponent(selectorComponent);
        panel.add(selectorComponent,
                new GridBagConstraints(0, 0, 1, 1, 1d, 0d, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

        final var interactiveSelectorButton = super.createAddButton(SELECT_PITCH_BUTTON_ELEMENT_ID, "Stellplatz interaktiv auswählen");
        interactiveSelectorButton.setButtonText("Kartenauswahl");
        interactiveSelectorButton.setMargin(new Insets(0, 10, 0, 10));

        panel.add(interactiveSelectorButton,
                new GridBagConstraints(1, 0, 1, 1, 0d, 0d, GridBagConstraints.NORTH, GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 0), 0, 0));

        this.pitchErrorComponent = super.createErrorLabel();
        final var errorWrapper = super.createErrorWrapper(this.pitchErrorComponent);
        errorWrapper.add(panel);

        return errorWrapper;
    }

    private JComponent createServiceTable() {
        this.serviceTable = new JPanel();
        this.serviceTable.setBorder(new EmptyBorder(10, 2, 10, 2));
        this.serviceTable.setForeground(this.config.getTextColor());
        this.serviceTable.setBackground(this.config.getBackgroundColor());
        this.serviceTable.setLayout(new GridBagLayout());
        this.serviceTable.setOpaque(true);

        this.serviceErrorComponent = super.createErrorLabel();
        final var errorWrapper = super.createErrorWrapper(this.serviceErrorComponent);
        errorWrapper.add(this.serviceTable);

        this.buildServiceTable();

        return errorWrapper;
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

        this.deleteBookingButton = ButtonElement.builder(DELETE_BOOKING_BUTTON_ELEMENT_ID)
                .buttonText("Buchung löschen")
                .font(this.config.getFont())
                .backgroundColor(this.config.getFailureColor())
                .textColor(this.config.getTextColor())
                .componentSize(new Dimension(150, GUIConstants.IntSizes.DEFAULT_BUTTON_HEIGHT.getValue()))
                .toolTip("Löscht die Buchung")
                .build();
        this.deleteBookingButton.addObserver(this);

        this.cancelBookingButton = ButtonElement.builder(CANCEL_BUTTON_ELEMENT_ID)
                .buttonText("Erstellen abbrechen")
                .font(this.config.getFont())
                .backgroundColor(this.config.getBackgroundColor())
                .textColor(this.config.getTextColor())
                .componentSize(new Dimension(150, GUIConstants.IntSizes.DEFAULT_BUTTON_HEIGHT.getValue()))
                .toolTip("Bricht den Vorgang ab")
                .build();
        this.cancelBookingButton.addObserver(this);

        this.saveBookingButton = ButtonElement.builder(SAVE_BOOKING_BUTTON_ELEMENT_ID)
                .buttonText("Buchung erstellen")
                .font(this.config.getFont())
                .backgroundColor(this.config.getAccentColor())
                .textColor(this.config.getTextColor())
                .componentSize(new Dimension(150, GUIConstants.IntSizes.DEFAULT_BUTTON_HEIGHT.getValue()))
                .build();
        this.saveBookingButton.addObserver(this);

        this.buttonComponent = ButtonComponent.builder(BUTTON_COMPONENT_ID)
                .embeddedComponent(mainPanel)
                .buttonElements(new ButtonElement[] {this.cancelBookingButton, this.saveBookingButton})
                .position(ButtonComponent.Position.SOUTH)
                .orientation(ButtonComponent.Orientation.RIGHT)
                .build();

        this.buildButtonComponentButtons();

        this.add(this.buttonComponent);
    }

    private void resetErrors() {
        this.startDateErrorComponent.setText("");
        this.endDateErrorComponent.setText("");
        this.pitchErrorComponent.setText("");
        this.guestErrorComponent.setText("");
        this.serviceErrorComponent.setText("");
    }
}
