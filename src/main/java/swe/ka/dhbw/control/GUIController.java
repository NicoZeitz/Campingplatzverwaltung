package swe.ka.dhbw.control;

import de.dhbwka.swe.utils.event.*;
import de.dhbwka.swe.utils.model.Attribute;
import de.dhbwka.swe.utils.model.IDepictable;
import de.dhbwka.swe.utils.model.ImageElement;
import de.dhbwka.swe.utils.util.PropertyManager;
import swe.ka.dhbw.database.EntityManager;
import swe.ka.dhbw.event.GUIBuchungObserver;
import swe.ka.dhbw.event.GUIConfigurationObserver;
import swe.ka.dhbw.event.GUIMainObserver;
import swe.ka.dhbw.model.*;
import swe.ka.dhbw.ui.*;
import swe.ka.dhbw.ui.components.*;
import swe.ka.dhbw.util.WindowLocation;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GUIController implements IUpdateEventSender, IUpdateEventListener {
    public enum Commands implements EventCommand {
        UPDATE_PITCHES("GUIController::UPDATE_PITCHES", List.class),
        UPDATE_BOOKINGS("GUIController::UPDATE_BOOKINGS", List.class),
        DATE_SELECTED("GUIController::DATE_SELECTED", Optional.class);

        public final Class<?> payloadType;
        public final String cmdText;

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

    private static GUIController instance;
    // Observers
    private final Set<EventListener> updateEventObservers = new HashSet<>();
    private GUIConfigurationObserver windowConfigurationObserver;
    // Windows
    private GUIBuchung windowBooking;
    private GUIPersonal windowStaff;
    private GUIGast windowGuest;
    private GUIEinrichtung windowFacility;
    private GUIStellplatz windowPitch;
    private GUIConfiguration windowConfiguration;
    private GUIMain windowMain;
    private GUICheckInCheckOut windowCheckInCheckOut;
    // Other properties
    private EntityManager entityManager;
    private Campingplatzverwaltung app;
    private Configuration.Builder configurationBuilder;

    private GUIController() {
    }

    public ReadonlyConfiguration getConfig() {
        return this.app.getConfig();
    }

    public static synchronized GUIController getInstance() {
        if (instance == null) {
            instance = new GUIController();
        }
        return instance;
    }

    private Map<LocalDate, List<? extends IDepictable>> getAppointments() {
        return this.entityManager.find(Buchung.class).stream()
                .sorted((buchung1, buchung2) -> {
                    final var res = buchung1.getAnreise().compareTo(buchung2.getAnreise());
                    if (res == 0) {
                        return buchung1.getAbreise().compareTo(buchung2.getAbreise());
                    }
                    return res;
                })
                .flatMap(buchung -> {
                    final var anreise = buchung.getAnreise().toLocalDate();
                    final var abreise = buchung.getAbreise().toLocalDate();
                    final var entries = new HashMap<LocalDate, ArrayList<IDepictable>>();
                    for (var date = anreise; date.isBefore(abreise); date = date.plusDays(1)) {
                        final var finalDate = date;
                        final var list = entries.computeIfAbsent(finalDate, k -> new ArrayList<>());
                        list.add(new IDepictable() {
                            @Override
                            public Attribute[] getAttributeArray() {
                                final var array = buchung.getAttributeArray();
                                try {
                                    array[Buchung.Attributes.ANREISE.ordinal()].setValue(finalDate);
                                } catch (Exception e) { /* Ignore Exception as it will not occur */ }
                                return array;
                            }

                            @Override
                            public String getElementID() {
                                return buchung.getElementID();
                            }

                            @Override
                            public String toString() {
                                final var gastName = buchung.getVerantwortlicherGast().getName();
                                final var stellplatzName = buchung.getGebuchterStellplatz().getStellplatz();
                                final var anreise = buchung.getAnreise().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                                final var abreise = buchung.getAbreise().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                                return gastName + "\n" + stellplatzName + "\n" + anreise + " - " + abreise;
                            }
                        });
                    }
                    return entries.entrySet().stream();
                }).collect(HashMap::new, (map, entry) -> map.put(entry.getKey(), entry.getValue()), HashMap::putAll);
    }

    private List<? extends IDepictable> getBookingsAsDisplayableList() {
        return this.entityManager
                .find(Buchung.class)
                .stream()
                .map(b -> new IDepictable() {
                    @Override
                    public Attribute[] getAttributeArray() {
                        final var verantwortlicherGast = b.getVerantwortlicherGast();
                        final var stellplatz = b.getGebuchterStellplatz();
                        final var bereich = stellplatz.getBereich();

                        // @formatter:off
                        return new Attribute[] {
                            new Attribute("Buchungsnummer", b, Integer.class, b.getBuchungsnummer(), null, true, false, false, true),
                            new Attribute("Zeitraum", b, String.class, b.getAnreise().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + " - " + b.getAbreise().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")), null, true, false, false, true),
                            new Attribute("Verantwortlicher Gast", b, String.class, verantwortlicherGast.getName() + " (" + verantwortlicherGast.getKundennummer() + ")", null, true, false, false, true),
                            new Attribute("Stellplatz", b, String.class, stellplatz.getStellplatz(), null, true, false, false, true),
                            new Attribute("Bereich", b, String.class, bereich.map(Bereich::getKennzeichen).orElse('-'), null, true, false, false, true),
                            new Attribute("Weitere Gäste", b, String.class, b.getZugehoerigeGaeste().stream().map(Person::getName).collect(Collectors.joining(", ")), null, true, false, false, true),
                            new Attribute("Stellplatzbilder", b, List.class, stellplatz.getFotos().stream().map(Foto::getImage).toList(), null, true, false, false, true),
                            new Attribute("Chipkarten", b, String.class, b.getAusgehaendigteChipkarten().stream().map(c -> c.getNummer() + " (" + c.getStatus() + ")").collect(Collectors.joining(", ")), null, true, false, false, true),
                        };
                        // @formatter:on
                    }

                    @Override
                    public String getElementID() {
                        return b.getElementID();
                    }
                })
                .toList();
    }

    public void setEntityManager(final EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void setApp(final Campingplatzverwaltung app) {
        this.app = app;
    }

    @Override
    public boolean addObserver(final EventListener eventListener) {
        return this.updateEventObservers.add(eventListener);
    }

    @Override
    public void processUpdateEvent(final UpdateEvent updateEvent) {
        // react to own events and fire additional companion events
        if (updateEvent.getCmd() == Commands.UPDATE_BOOKINGS) {
            this.fireUpdateEvent(new UpdateEvent(this, BookingOverviewComponent.Commands.UPDATE_APPOINTMENTS, this.getAppointments()));
            this.fireUpdateEvent(new UpdateEvent(this, BookingListComponent.Commands.UPDATE_BOOKINGS, this.getBookingsAsDisplayableList()));
        }
    }

    @Override
    public boolean removeObserver(final EventListener eventListener) {
        return this.updateEventObservers.remove(eventListener);
    }

    public void bookingCreateSelectChipkarte(final List<Chipkarte> availableChipkarten,
                                             final List<Chipkarte> selectedChipkarten,
                                             final Chipkarte newlySelectedChipkarte) {
        selectedChipkarten.add(newlySelectedChipkarte);
        this.fireUpdateEvent(new UpdateEvent(
                this,
                BookingCreateComponent.Commands.SELECT_CHIPCARD,
                new BookingCreateComponent.SelectChipkartePayload(
                        availableChipkarten.stream().filter(c -> !c.equals(newlySelectedChipkarte)).sorted().toList(),
                        selectedChipkarten,
                        ""
                )
        ));
    }

    public void bookingRemoveChipkarte(final List<Chipkarte> availableChipCards,
                                       final List<Chipkarte> selectedChipCards,
                                       final Chipkarte deletedChipCard) {
        selectedChipCards.remove(deletedChipCard);
        this.fireUpdateEvent(new UpdateEvent(
                this,
                BookingCreateComponent.Commands.SELECT_CHIPCARD,
                new BookingCreateComponent.SelectChipkartePayload(
                        Stream.concat(Stream.of(deletedChipCard), availableChipCards.stream()).sorted().toList(),
                        selectedChipCards,
                        ""
                )
        ));
    }

    public void exitApplication() {
        if (this.app.getConfig() == null) {
            this.app.setConfig(this.configurationBuilder.build());
        }
        this.app.exitApplication();
    }

    // Event Handlers

    public void fireUpdateEvent(final UpdateEvent updateEvent) {
        for (final var eventListener : this.updateEventObservers) {
            if (eventListener instanceof IUpdateEventListener updateListener) {
                updateListener.processUpdateEvent(updateEvent);
            }
        }
    }

    public void handleWindowBookingAppointmentOverviewNextWeek(final LocalDate currentWeek) {
        this.fireUpdateEvent(new UpdateEvent(this, BookingOverviewComponent.Commands.UPDATE_WEEK, currentWeek.plusWeeks(1)));
    }

    public void handleWindowBookingAppointmentOverviewPreviousWeek(final LocalDate currentWeek) {
        this.fireUpdateEvent(new UpdateEvent(this, BookingOverviewComponent.Commands.UPDATE_WEEK, currentWeek.minusWeeks(1)));
    }

    public void handleWindowBookingBookingSelected(final String elementID) {
        this.fireUpdateEvent(new UpdateEvent(
                        this,
                        GUIBuchung.Commands.OPEN_TAB,
                        new GUIBuchung.TabPayload(
                                // TODO: real data this.entityManager.findOne(Buchung.class, elementID),
                                // TODO: close tab panes after done editing
                                "Buchung " + elementID + " bearbeiten",
                                this.windowStaff,
                                "Die Buchung mit der Buchungsnummer " + elementID + " bearbeiten"
                        )
                )
        );
    }

    public void handleWindowBookingCreateBookingCancel() {
        final var decision = JOptionPane.showConfirmDialog(
                null,
                "Wollen Sie die Erstellung der Buchung wirklich abbrechen?",
                "Buchung abbrechen",
                JOptionPane.YES_NO_OPTION
        );

        if (decision == JOptionPane.YES_OPTION) {
            this.fireUpdateEvent(new UpdateEvent(this, BookingCreateComponent.Commands.RESET));
            this.fireUpdateEvent(new UpdateEvent(this, GUIBuchung.Commands.SWITCH_TAB, GUIBuchung.Tabs.BOOKING_LIST));
        }
    }

    public void handleWindowBookingCreateBookingCreate(final BookingCreateComponent.BookingCreatePayload payload) {
        if (payload.arrivalDate().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Bitte geben Sie ein Anreisedatum an.", "Fehler", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (payload.departureDate().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Bitte geben Sie ein Abreisedatum an.", "Fehler", JOptionPane.ERROR_MESSAGE);
            return;
        }

        final var arrivalDate = payload.arrivalDate().get();
        final var departureDate = payload.departureDate().get();

        if (arrivalDate.isAfter(departureDate)) {
            JOptionPane.showMessageDialog(null, "Das Abreisedatum muss nach dem Anreisedatum liegen.", "Fehler", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (arrivalDate.isBefore(LocalDateTime.now())) {
            JOptionPane.showMessageDialog(null, "Das Anreisedatum muss in der Zukunft liegen.", "Fehler", JOptionPane.ERROR_MESSAGE);
            return;
        }

        final var pitch = (Stellplatz) payload.bookedPitch();

        final var conflictingBookingsCount = this.entityManager
                .find(Buchung.class)
                .stream()
                .filter(b -> b.getGebuchterStellplatz().equals(pitch))
                .filter(b -> {
                    final var otherArrivalDate = b.getAnreise();
                    final var otherDepartureDate = b.getAbreise();
                    return otherDepartureDate.isAfter(arrivalDate) && otherArrivalDate.isBefore(departureDate) ||
                            otherArrivalDate.isBefore(departureDate) && otherDepartureDate.isAfter(arrivalDate);
                })
                .count();

        if (conflictingBookingsCount > 0) {
            JOptionPane.showMessageDialog(null,
                    "Der Stellplatz ist in diesem Zeitraum bereits belegt. Bitte wählen Sie einen anderen Stellplatz aus.",
                    "Fehler",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (payload.responsibleGuest().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Bitte wählen Sie einen verantwortlichen Gast aus.", "Fehler", JOptionPane.ERROR_MESSAGE);
            return;
        }

        final var responsibleGuest = (Gast) payload.responsibleGuest().get();

        final var booking = new Buchung(this.entityManager.generateNextPrimaryKey(Buchung.class), arrivalDate, departureDate);
        for (final var associatedGuest : payload.associatedGuests()) {
            booking.addZugehoerigerGast((Gast) associatedGuest);
        }
        booking.setVerantwortlicherGast(responsibleGuest);
        for (final var bookedService : payload.bookedServices()) {
            booking.addGebuchteLeistung((GebuchteLeistung) bookedService);
        }
        for (final var rentedEquipment : payload.rentedEquipment()) {
            booking.addMitgebrachteAusruestung((Ausruestung) rentedEquipment);
        }
        booking.setGebuchterStellplatz(pitch);
        for (final var chipCard : payload.chipCards()) {
            booking.addAusgehaendigteChipkarte((Chipkarte) chipCard);
        }

        this.entityManager.persist(booking);
        // TODO: save in db
        this.fireUpdateEvent(new UpdateEvent(this, BookingCreateComponent.Commands.RESET));
        this.fireUpdateEvent(new UpdateEvent(this, GUIBuchung.Commands.SWITCH_TAB, GUIBuchung.Tabs.BOOKING_LIST));
    }

    public void handleWindowConfigurationSetAccentColor(final Color currentColor) {
        final var nextColor = JColorChooser.showDialog(this.windowConfiguration, "Farbe auswählen", currentColor);
        if (nextColor == null) {
            return;
        }

        this.configurationBuilder.accentColor(nextColor);
        this.fireUpdateEvent(new UpdateEvent(this, GUIConfiguration.Commands.REBUILD_UI, this.configurationBuilder.build()));
    }

    public void handleWindowConfigurationSetDarkMode(final boolean darkModeActive) {
        if (darkModeActive) {
            this.configurationBuilder.darkMode();
        } else {
            this.configurationBuilder.lightMode();
        }
        this.fireUpdateEvent(new UpdateEvent(this, GUIConfiguration.Commands.REBUILD_UI, this.configurationBuilder.build()));
    }

    public void handleWindowConfigurationSetTextFont(final Font font) {
        this.configurationBuilder.font(font);
        this.fireUpdateEvent(new UpdateEvent(this, GUIConfiguration.Commands.REBUILD_UI, this.configurationBuilder.build()));
    }

    public void handleWindowMainCreateBooking() {
        this.openWindowBooking();
        this.fireUpdateEvent(new UpdateEvent(this, GUIBuchung.Commands.SWITCH_TAB, GUIBuchung.Tabs.BOOKING_CREATE));
    }

    public void handleWindowMainOpenBookingManagement() {
        this.openWindowBooking();
        this.fireUpdateEvent(new UpdateEvent(this, GUIBuchung.Commands.SWITCH_TAB, GUIBuchung.Tabs.APPOINTMENT_OVERVIEW));
    }

    // Dialogs

    public void initialize() {
        this.app.setConfig(this.configurationBuilder.build());

        // create all window components
        this.windowMain = new GUIMain(this.getConfig());
        this.windowBooking = new GUIBuchung(this.getConfig());
        this.windowPitch = new GUIStellplatz(this.getConfig());
        this.windowGuest = new GUIGast(this.getConfig());
        this.windowFacility = new GUIEinrichtung(this.getConfig());
        this.windowStaff = new GUIPersonal(this.getConfig());
        this.windowCheckInCheckOut = new GUICheckInCheckOut(this.getConfig());

        // register all window components as observers
        this.windowMain.addObserver(new GUIMainObserver());
        this.addObserver(this.windowMain);
        this.windowBooking.addObserver(new GUIBuchungObserver());
        this.addObserver(this.windowBooking);
        // and so on... but as the other windows are just dummy placeholders we don't need to register them

        // setup up initial data
        this.fireUpdateEvent(new UpdateEvent(this, Commands.UPDATE_PITCHES, this.entityManager.find(Stellplatz.class)));
        this.fireUpdateEvent(new UpdateEvent(this, Commands.UPDATE_BOOKINGS, this.entityManager.find(Buchung.class)));
        this.fireUpdateEvent(new UpdateEvent(this, BookingOverviewComponent.Commands.UPDATE_WEEK, LocalDate.now()));
        // TODO: chipkarten this.fireUpdateEvent(new UpdateEvent(this, BookingCreateComponent.Commands.CHI, this.getGuestsAsDisplayableList()));
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public void openDialogDatePicker(final Optional<LocalDate> optionalDate, final PayloadEvent callbackToEvent) {
        final var source = (GUIComponent) callbackToEvent.getSource();
        final var windowLocation = this.getConfig().getWindowLocation("Dialog::CalendarComponent").withWidth(300).withHeight(300);
        final var calendarComponent = new CalendarComponent(this.getConfig(), optionalDate);
        final var parentWindow = this.getNearestWindow(source);

        calendarComponent.addObserver((IGUIEventListener) guiEvent -> {
            if (guiEvent.getCmd() != CalendarComponent.Commands.DATE_SELECTED) {
                return;
            }
            final var dialog = SwingUtilities.getWindowAncestor(calendarComponent);
            dialog.dispatchEvent(new WindowEvent(dialog, WindowEvent.WINDOW_CLOSING));
            source.processUpdateEvent(new UpdateEvent(
                    GUIController.this,
                    new EventCommand() {
                        @Override
                        public String getCmdText() {
                            return callbackToEvent.getCmdText();
                        }

                        @Override
                        public Class<?> getPayloadType() {
                            return LocalDate.class;
                        }
                    },
                    guiEvent.getData()
            ));
        });

        this.openInDialog(calendarComponent, parentWindow, "Datum auswählen", windowLocation, (e) -> {
            final var window = e.getWindow();
            this.app.getConfig().setWindowLocation("Dialog::CalendarComponent", WindowLocation.from(window));
            window.dispose();
        });
    }

    public void openDialogEquipmentSelector(final PayloadEvent callbackToEvent) {
        final var source = (GUIComponent) callbackToEvent.getSource();
        final var windowLocation = this.getConfig().getWindowLocation("Dialog::EquipmentSelector").withWidth(440).withHeight(440);
        final var vehicleTypes = Arrays.stream(Fahrzeug.Typ.values()).toList();
        final var equipmentSelectorComponent = new EquipmentSelectorComponent(this.getConfig(), vehicleTypes);
        final var parentWindow = this.getNearestWindow(source);

        equipmentSelectorComponent.addObserver((IGUIEventListener) guiEvent -> {
            if (guiEvent.getCmd() == EquipmentSelectorComponent.Commands.CANCEL) {
                final var dialog = SwingUtilities.getWindowAncestor(equipmentSelectorComponent);
                dialog.dispatchEvent(new WindowEvent(dialog, WindowEvent.WINDOW_CLOSING));
            } else if (guiEvent.getCmd() == EquipmentSelectorComponent.Commands.SAVE) {
                final var payload = (EquipmentSelectorComponent.SavePayload) guiEvent.getData();
                if (payload.description().isEmpty()) {
                    JOptionPane.showMessageDialog(
                            source,
                            "Bitte geben Sie eine Beschreibung an.",
                            "Fehler",
                            JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }
                if (payload.height().isEmpty() || payload.width().isEmpty()) {
                    JOptionPane.showMessageDialog(
                            source,
                            "Bitte geben Sie eine Höhe und Breite an.",
                            "Fehler",
                            JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }

                var equipment = new Ausruestung(
                        this.entityManager.generateNextPrimaryKey(Ausruestung.class),
                        payload.description().get(),
                        payload.amount(),
                        payload.height().get(),
                        payload.width().get()
                );
                if (payload.licensePlate().isPresent()) {
                    equipment = new Fahrzeug(
                            this.entityManager.generateNextPrimaryKey(Ausruestung.class),
                            payload.description().get(),
                            payload.amount(),
                            payload.height().get(),
                            payload.width().get(),
                            payload.licensePlate().get(),
                            (Fahrzeug.Typ) payload.vehicleTyp()
                    );
                }
                final var dialog = SwingUtilities.getWindowAncestor(equipmentSelectorComponent);
                dialog.dispose();
                source.processUpdateEvent(new UpdateEvent(
                        GUIController.this,
                        new EventCommand() {
                            @Override
                            public String getCmdText() {
                                return callbackToEvent.getCmdText();
                            }

                            @Override
                            public Class<?> getPayloadType() {
                                return IDepictable.class;
                            }
                        },
                        equipment
                ));
            }
        });

        this.openInDialog(equipmentSelectorComponent, parentWindow, "Ausrüstung auswählen", windowLocation, (e) -> {
            final var decision = JOptionPane.showConfirmDialog(
                    null,
                    "Wollen Sie die Auswahl einer Ausrüstung wirklich abbrechen?",
                    "Ausrüstungsauswahl abbrechen",
                    JOptionPane.YES_NO_OPTION
            );

            if (decision == JOptionPane.YES_OPTION) {
                final var window = e.getWindow();
                this.app.getConfig().setWindowLocation("Dialog::EquipmentSelector", WindowLocation.from(window));
                window.dispose();
            }
        });
    }

    public void openDialogGuestCreate(final PayloadEvent callbackToEvent) {
        final var parentWindow = this.getNearestWindow((GUIComponent) callbackToEvent.getSource());
        final var windowLocation = this.getConfig().getWindowLocation("Dialog::GuestCreate").withWidth(350).withHeight(350);
        this.openInDialog(new GuestCreateComponent(this.getConfig()), parentWindow, "Gast anlegen", windowLocation, (event) -> {
            final var window = event.getWindow();
            this.app.getConfig().setWindowLocation("Dialog::GuestCreate", WindowLocation.from(window));
            window.dispose();
        });
    }

    public void openDialogGuestSelector(final PayloadEvent callbackToEvent, final Set<Gast> withoutGuests) {
        final var source = (GUIComponent) callbackToEvent.getSource();
        final var guests = this.entityManager.find(Gast.class).stream().filter(g -> !withoutGuests.contains(g)).toList();
        final var windowLocation = this.getConfig().getWindowLocation("Dialog::GuestSelector").withWidth(400).withHeight(320);
        final var guestSelectorComponent = new GuestSelectorComponent(this.getConfig(), guests);
        final var parentWindow = this.getNearestWindow((GUIComponent) callbackToEvent.getSource());
        this.addObserver(guestSelectorComponent);

        guestSelectorComponent.addObserver((IGUIEventListener) guiEvent -> {
            if (guiEvent.getCmd() == GuestSelectorComponent.Commands.SEARCH_INPUT_CHANGED) {
                final var payload = (GuestSelectorComponent.SearchInputChangedPayload) guiEvent.getData();
                final var filteredGuests = guests.stream()
                        .filter(g -> g.getName().toLowerCase().contains(payload.text().toLowerCase()))
                        .toList();
                this.fireUpdateEvent(new UpdateEvent(
                        this,
                        GuestSelectorComponent.Commands.UPDATE_GUESTS,
                        filteredGuests
                ));
            } else if (guiEvent.getCmd() == GuestSelectorComponent.Commands.GUEST_SELECTED) {
                final var guest = (Gast) guiEvent.getData();
                final var dialog = SwingUtilities.getWindowAncestor(guestSelectorComponent);
                dialog.dispose();
                source.processUpdateEvent(new UpdateEvent(
                        GUIController.this,
                        new EventCommand() {
                            @Override
                            public String getCmdText() {
                                return callbackToEvent.getCmdText();
                            }

                            @Override
                            public Class<?> getPayloadType() {
                                return IDepictable.class;
                            }
                        },
                        guest
                ));
            } else if (guiEvent.getCmd() == GuestSelectorComponent.Commands.ADD_GUEST_BUTTON_PRESSED) {
                this.openDialogGuestCreate(guiEvent);
            }
        });

        this.openInDialog(guestSelectorComponent, parentWindow, "Gast auswählen", windowLocation, (event) -> {
            final var decision = JOptionPane.showConfirmDialog(
                    null,
                    "Wollen Sie die Auswahl von einem neuen Gast wirklich abbrechen?",
                    "Gastauswahl abbrechen",
                    JOptionPane.YES_NO_OPTION
            );

            if (decision == JOptionPane.YES_OPTION) {
                final var window = event.getWindow();
                this.app.getConfig().setWindowLocation("Dialog::GuestSelector", WindowLocation.from(window));
                window.dispose();
            }
        });
    }

    public void openDialogPitchSelector(final PayloadEvent callbackToEvent) {
        final var source = (GUIComponent) callbackToEvent.getSource();
        final var pitches = this.entityManager
                .find(Stellplatz.class)
                .stream()
                .map(p -> new PitchSelectorComponent.Pitch(
                        p.getLage().getLatitude(),
                        p.getLage().getLongitude(),
                        p.getFotos().stream().findAny().map(Foto::getImage).map(ImageElement::getBaseImage),
                        p
                ))
                .toList();
        final var pitchSelectorComponent = new PitchSelectorComponent(this.getConfig());
        this.addObserver(pitchSelectorComponent);
        this.fireUpdateEvent(new UpdateEvent(this, PitchSelectorComponent.Commands.UPDATE_PITCHES, pitches));
        pitchSelectorComponent.setSizeWithWidth(720);
        final var windowLocation = this.getConfig().getWindowLocation("Dialog::PitchSelector")
                .withWidth(pitchSelectorComponent.getImageWidth())
                .withHeight(pitchSelectorComponent.getImageHeight());
        final var parentWindow = this.getNearestWindow(source);
        pitchSelectorComponent.addObserver((IGUIEventListener) guiEvent -> {
            if (guiEvent.getCmd() == PitchSelectorComponent.Commands.PITCH_SELECTED) {
                final var pitch = (Stellplatz) guiEvent.getData();
                final var dialog = SwingUtilities.getWindowAncestor(pitchSelectorComponent);
                dialog.dispose();
                source.processUpdateEvent(new UpdateEvent(
                        GUIController.this,
                        new EventCommand() {
                            @Override
                            public String getCmdText() {
                                return callbackToEvent.getCmdText();
                            }

                            @Override
                            public Class<?> getPayloadType() {
                                return IDepictable.class;
                            }
                        },
                        pitch
                ));
            }
        });
        // TODO: GUI DIalogs dont pass data in constructor but in processUpdateEvent

        this.openInDialog(pitchSelectorComponent, parentWindow, "Stellplatz auswählen", windowLocation, (e) -> {
            final var decision = JOptionPane.showConfirmDialog(
                    null,
                    "Wollen Sie die Auswahl eines Stellplatzes wirklich abbrechen?",
                    "Stellplatzauswahl abbrechen",
                    JOptionPane.YES_NO_OPTION
            );

            if (decision == JOptionPane.YES_OPTION) {
                final var window = e.getWindow();
                this.app.getConfig().setWindowLocation("Dialog::PitchSelector", WindowLocation.from(window));
                window.dispose();
            }
        });
    }

    // Windows

    @SuppressWarnings("unchecked")
    public void openDialogServiceSelector(final PayloadEvent callbackToEvent) {
        final var source = (GUIComponent) callbackToEvent.getSource();
        final var services = this.entityManager.find(Leistungsbeschreibung.class);
        final var windowLocation = this.getConfig().getWindowLocation("Dialog::ServiceSelector").withWidth(440).withHeight(240);
        final var serviceSelectorComponent = new ServiceSelectorComponent(this.getConfig(), services);
        final var parentWindow = this.getNearestWindow(source);

        serviceSelectorComponent.addObserver((IGUIEventListener) guiEvent -> {
            if (guiEvent.getCmd() == ServiceSelectorComponent.Commands.DATE_PICKER_START_DATE ||
                    guiEvent.getCmd() == ServiceSelectorComponent.Commands.DATE_PICKER_END_DATE) {
                this.openDialogDatePicker((Optional<LocalDate>) guiEvent.getData(), guiEvent);
            } else if (guiEvent.getCmd() == ServiceSelectorComponent.Commands.CANCEL) {
                final var dialog = SwingUtilities.getWindowAncestor(serviceSelectorComponent);
                dialog.dispatchEvent(new WindowEvent(dialog, WindowEvent.WINDOW_CLOSING));
            } else if (guiEvent.getCmd() == ServiceSelectorComponent.Commands.SAVE) {
                final var payload = (ServiceSelectorComponent.SavePayload) guiEvent.getData();
                if (payload.startDate().isEmpty() && payload.endDate().isEmpty()) {
                    JOptionPane.showMessageDialog(
                            source,
                            "Bitte geben Sie ein Start- und Enddatum an.",
                            "Fehler",
                            JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }
                if (payload.startDate().isEmpty()) {
                    JOptionPane.showMessageDialog(
                            source,
                            "Bitte geben Sie ein Startdatum an.",
                            "Fehler",
                            JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }
                if (payload.endDate().isEmpty()) {
                    JOptionPane.showMessageDialog(
                            source,
                            "Bitte geben Sie ein Enddatum an.",
                            "Fehler",
                            JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }

                final var bookedService = new GebuchteLeistung(
                        this.entityManager.generateNextPrimaryKey(GebuchteLeistung.class),
                        payload.startDate().get(),
                        payload.endDate().get()
                );
                final var leistungsbeschreibung = (Leistungsbeschreibung) payload.selectedServiceType();
                bookedService.setLeistungsbeschreibung(leistungsbeschreibung);
                final var dialog = SwingUtilities.getWindowAncestor(serviceSelectorComponent);
                dialog.dispose();
                source.processUpdateEvent(new UpdateEvent(
                        GUIController.this,
                        new EventCommand() {
                            @Override
                            public String getCmdText() {
                                return callbackToEvent.getCmdText();
                            }

                            @Override
                            public Class<?> getPayloadType() {
                                return IDepictable.class;
                            }
                        },
                        bookedService
                ));
            }
        });

        this.openInDialog(serviceSelectorComponent, parentWindow, "Leistung auswählen", windowLocation, (e) -> {
            final var decision = JOptionPane.showConfirmDialog(
                    null,
                    "Wollen Sie die Auswahl einer Leistung wirklich abbrechen?",
                    "Leistungsauswahl abbrechen",
                    JOptionPane.YES_NO_OPTION
            );

            if (decision == JOptionPane.YES_OPTION) {
                final var window = e.getWindow();
                this.app.getConfig().setWindowLocation("Dialog::ServiceSelector", WindowLocation.from(window));
                window.dispose();
            }
        });
    }

    @SuppressWarnings("unchecked")
    public void openWindowBooking() {
        if (this.windowBooking.isDisplayable()) {
            this.windowBooking.grabFocus();
            return;
        }

        this.openInWindow(this.windowBooking, "Buchungen", "Window::Booking", event -> {
            ((JFrame) SwingUtilities.getWindowAncestor(this.windowMain)).setState(Frame.NORMAL);
            this.windowMain.grabFocus();
        });
    }

    public void openWindowCheckInCheckOut() {
        if (this.windowCheckInCheckOut.isDisplayable()) {
            this.windowCheckInCheckOut.grabFocus();
            return;
        }

        this.openInWindow(this.windowCheckInCheckOut, "Check-In / Check-Out", "Window::CheckInCheckOut", event -> {
            ((JFrame) SwingUtilities.getWindowAncestor(this.windowMain)).setState(Frame.NORMAL);
            this.windowMain.grabFocus();
        });
    }

    public void openWindowConfiguration(final PropertyManager propertyManager) throws Exception {
        this.configurationBuilder = Configuration.builder().propertyManager(propertyManager);
        this.windowConfiguration = new GUIConfiguration(this.configurationBuilder.build());
        this.windowConfigurationObserver = new GUIConfigurationObserver();
        this.windowConfiguration.addObserver(this.windowConfigurationObserver);
        this.addObserver(this.windowConfiguration);
        // Main GUI and Configuration GUI have the same window location
        this.openInWindow(this.windowConfiguration, "Konfiguration", "Window::Main", event -> {
            this.exitApplication();
        });
    }

    public void openWindowFacility() {
        if (this.windowFacility.isDisplayable()) {
            this.windowFacility.grabFocus();
            return;
        }

        this.openInWindow(this.windowFacility, "Einrichtungen", "Window::Facility", event -> {
            ((JFrame) SwingUtilities.getWindowAncestor(this.windowMain)).setState(Frame.NORMAL);
            this.windowMain.grabFocus();
        });
    }

    public void openWindowGuest() {
        if (this.windowGuest.isDisplayable()) {
            this.windowGuest.grabFocus();
            return;
        }

        this.openInWindow(this.windowGuest, "Gäste", "Window::Guest", event -> {
            ((JFrame) SwingUtilities.getWindowAncestor(this.windowMain)).setState(Frame.NORMAL);
            this.windowMain.grabFocus();
        });
    }

    public void openWindowMain(final PropertyManager propertyManager) throws Exception {
        this.configurationBuilder = Configuration.builder().propertyManager(propertyManager);
        this.app.setConfig(this.configurationBuilder.build());

        this.initialize();

        this.openInWindow(this.windowMain, "Campingplatzverwaltung", "Window::Main", event -> {
            this.exitApplication();
        });
    }

    public void openWindowMain() {
        final var configWindow = (SwingUtilities.getWindowAncestor(this.windowConfiguration));
        this.windowConfiguration.removeObserver(this.windowConfigurationObserver);
        this.removeObserver(this.windowConfiguration);
        this.windowConfigurationObserver = null;
        this.app.getConfig().setWindowLocation("Window::Main", WindowLocation.from(configWindow));
        configWindow.dispose();

        this.initialize();

        this.openInWindow(this.windowMain, "Campingplatzverwaltung", "Window::Main", event -> {
            this.exitApplication();
        });
    }

    public void openWindowPitch() {
        if (this.windowPitch.isDisplayable()) {
            this.windowPitch.grabFocus();
            return;
        }

        this.openInWindow(this.windowPitch, "Stellplätze", "Window::Pitch", event -> {
            ((JFrame) SwingUtilities.getWindowAncestor(this.windowMain)).setState(Frame.NORMAL);
            this.windowMain.grabFocus();
        });
    }

    public void openWindowStaff() {
        if (this.windowStaff.isDisplayable()) {
            this.windowStaff.grabFocus();
            return;
        }

        this.openInWindow(this.windowStaff, "Personal", "Window::Staff", event -> {
            ((JFrame) SwingUtilities.getWindowAncestor(this.windowMain)).setState(Frame.NORMAL);
            this.windowMain.grabFocus();
        });
    }

    // Utility Methods

    private JFrame getNearestWindow(final Container content) {
        final var maybeFrame = SwingUtilities.getWindowAncestor(content);
        if (maybeFrame instanceof JFrame frame) {
            return frame;
        }

        return (JFrame) SwingUtilities.getWindowAncestor(maybeFrame);
    }

    private JDialog openInDialog(final Container content,
                                 final Frame parentWindow,
                                 final String title,
                                 final WindowLocation windowLocation,
                                 final Consumer<WindowEvent> onExit) {
        final var config = Optional.ofNullable(this.getConfig())
                .orElse(this.configurationBuilder.build());
        final var dialog = new JDialog(parentWindow);
        dialog.setForeground(config.getTextColor());
        dialog.setBackground(config.getBackgroundColor());
        dialog.setFont(config.getFont());
        dialog.setTitle(title);
        dialog.add(content);
        dialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.setLocation(Math.max(windowLocation.x(), 0), Math.max(windowLocation.y(), 0));
        dialog.setSize(windowLocation.width() > 0 ? windowLocation.width() : 100, windowLocation.height() > 0 ? windowLocation.height() : 100);
        dialog.setResizable(false);
        dialog.setBackground(this.getConfig().getBackgroundColor());
        dialog.setForeground(this.getConfig().getTextColor());
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent event) {
                onExit.accept(event);
            }
        });
        try {
            dialog.setIconImage(ImageIO.read(Objects.requireNonNull(this.getClass().getResourceAsStream("/Logo.png"))));
        } catch (IOException e) { /* Ignore This Case */ }
        dialog.setVisible(true);
        return dialog;
    }

    private JFrame openInWindow(final Container content,
                                final String title,
                                final WindowLocation windowLocation,
                                final Consumer<WindowEvent> onExit) {
        final var config = Optional.ofNullable(this.getConfig())
                .orElse(this.configurationBuilder.build());
        final JFrame frame = new JFrame(title);
        frame.setForeground(config.getTextColor());
        frame.setBackground(config.getBackgroundColor());
        frame.setFont(config.getFont());
        frame.setSize(windowLocation.width() > 0 ? windowLocation.width() : 100,
                windowLocation.height() > 0 ? windowLocation.height() : 100);
        frame.setLocation(Math.max(windowLocation.x(), 0), Math.max(windowLocation.y(), 0));
        frame.add(content);
        try {
            frame.setIconImage(ImageIO.read(Objects.requireNonNull(this.getClass().getResourceAsStream("/Logo.png"))));
        } catch (IOException e) { /* Ignore This Case */ }
        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent event) {
                onExit.accept(event);
                frame.dispose();
            }
        });
        return frame;
    }

    private JFrame openInWindow(final Container content,
                                final String title,
                                final String windowLocationKey,
                                final Consumer<WindowEvent> onExit) {
        final var windowLocation = Optional.ofNullable(this.getConfig())
                .orElse(this.configurationBuilder.build())
                .getWindowLocation(windowLocationKey);
        return this.openInWindow(content, title, windowLocation, (event) -> {
            onExit.accept(event);
            final var window = event.getWindow();
            this.app.getConfig().setWindowLocation(windowLocationKey, WindowLocation.from(window));
            window.dispose();
        });
    }
}
