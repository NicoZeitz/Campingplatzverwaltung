package swe.ka.dhbw.control;

import de.dhbwka.swe.utils.event.*;
import de.dhbwka.swe.utils.model.Attribute;
import de.dhbwka.swe.utils.model.ICSVPersistable;
import de.dhbwka.swe.utils.model.IDepictable;
import de.dhbwka.swe.utils.model.ImageElement;
import de.dhbwka.swe.utils.util.AppLogger;
import de.dhbwka.swe.utils.util.PropertyManager;
import swe.ka.dhbw.database.Datenbasis;
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
import java.util.function.Function;
import java.util.stream.Collectors;

public class GUIController implements IUpdateEventSender, IUpdateEventListener {
    public enum Commands implements EventCommand {
        UPDATE_ADDRESSES("GUIController::UPDATE_ADDRESSES", List.class),
        UPDATE_EQUIPMENT("GUIController::UPDATE_EQUIPMENT", List.class),
        UPDATE_AREAS("GUIController::UPDATE_AREAS", List.class),
        UPDATE_BOOKINGS("GUIController::UPDATE_BOOKINGS", List.class),
        UPDATE_CHIPCARDS("GUIController::UPDATE_CHIPCARDS", List.class),
        UPDATE_FACILITIES("GUIController::UPDATE_FACILITIES", List.class),
        UPDATE_CONTRACTORS("GUIController::UPDATE_CONTRACTORS", List.class),
        UPDATE_GUESTS("GUIController::UPDATE_GUESTS", List.class),
        UPDATE_BOOKED_SERVICES("GUIController::UPDATE_BOOKED_SERVICES", List.class),
        UPDATE_SERVICES("GUIController::UPDATE_SERVICES", List.class),
        UPDATE_OPENING_DAYS("GUIController::UPDATE_OPENING_DAYS", List.class),
        UPDATE_OPENING_HOURS("GUIController::UPDATE_OPENING_HOURS", List.class),
        UPDATE_PERSONS("GUIController::UPDATE_PERSONS", List.class),
        UPDATE_STAFF("GUIController::UPDATE_STAFF", List.class),
        UPDATE_INVOICES("GUIController::UPDATE_INVOICES", List.class),
        UPDATE_PITCHES("GUIController::UPDATE_PITCHES", List.class),
        UPDATE_DISTURBANCES("GUIController::UPDATE_DISTURBANCES", List.class),
        UPDATE_MAINTENANCE("GUIController::UPDATE_MAINTENANCE", List.class);

        public final Class<?> payloadType;
        public final String cmdText;

        @SuppressWarnings("SameParameterValue")
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
    private Datenbasis<ICSVPersistable> database;
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

    // get tranformed data

    private Map<LocalDate, List<IDepictable>> getAppointments() {
        return this.entityManager.find(Buchung.class).stream()
                .sorted(Buchung::compareTo)
                .flatMap(booking -> {
                    final var arrivalDate = booking.getAnreise().toLocalDate();
                    final var departureDate = booking.getAbreise().toLocalDate();
                    final var entries = new HashMap<LocalDate, IDepictable>();
                    for (var date = arrivalDate; date.isBefore(departureDate) || date.equals(departureDate); date = date.plusDays(1)) {
                        final var finalDate = date;
                        entries.put(finalDate, new IDepictable() {
                            @Override
                            public Attribute[] getAttributeArray() {
                                final var array = booking.getAttributeArray();
                                try {
                                    array[Buchung.Attributes.ANREISE.ordinal()].setValue(finalDate);
                                } catch (Exception e) { /* Ignore Exception as it will not occur */ }
                                return array;
                            }

                            @Override
                            public String getElementID() {
                                return booking.getElementID();
                            }

                            @Override
                            public String toString() {
                                final var guestName = booking.getVerantwortlicherGast().getName();
                                final var pitchName = booking.getGebuchterStellplatz().getStellplatz();
                                final var arrivalDate = booking.getAnreise().format(DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.GERMANY));
                                final var depatureDate = booking.getAbreise().format(DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.GERMANY));
                                return guestName + "\n" + pitchName + "\n" + arrivalDate + " - " + depatureDate;
                            }
                        });
                    }
                    return entries.entrySet().stream();
                })
                .collect(
                        HashMap::new,
                        (map, entry) -> map.computeIfAbsent(entry.getKey(), k -> new ArrayList<>()).add(entry.getValue()),
                        (hashMap1, hashMap2) -> {
                            for (final var entry : hashMap2.entrySet()) {
                                final var list = hashMap1.getOrDefault(entry.getKey(), new ArrayList<>());
                                list.addAll(entry.getValue());
                                hashMap1.put(entry.getKey(), list);
                            }
                        }
                );
    }

    private List<? extends IDepictable> getBookingsAsDisplayableList() {
        return this.entityManager
                .find(Buchung.class)
                .stream()
                .sorted(Buchung::compareTo)
                .map(b -> new IDepictable() {
                    @Override
                    public Attribute[] getAttributeArray() {
                        final var verantwortlicherGast = b.getVerantwortlicherGast();
                        final var stellplatz = b.getGebuchterStellplatz();
                        final var bereich = stellplatz.getBereich();

                        // @formatter:off
                        return new Attribute[] {
                            new Attribute("Buchungsnummer", b, Integer.class, b.getBuchungsnummer(), null, true, false, false, true),
                            new Attribute("Zeitraum", b, String.class, b.getAnreise().format(DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.GERMANY)) + " - " + b.getAbreise().format(DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.GERMANY)), null, true, false, false, true),
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

    // setters

    public void setEntityManager(final EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void setApp(final Campingplatzverwaltung app) {
        this.app = app;
    }

    public void setDatabase(final Datenbasis<ICSVPersistable> database) {
        this.database = database;
    }

    // observer pattern

    @Override
    public boolean addObserver(final EventListener eventListener) {
        return this.updateEventObservers.add(eventListener);
    }

    @SuppressWarnings("SwitchStatementWithTooFewBranches")
    @Override
    public void processUpdateEvent(final UpdateEvent updateEvent) {
        // react to own events and fire additional companion events
        if (updateEvent.getCmd() instanceof Commands command) {
            switch (command) {
                case UPDATE_BOOKINGS -> {
                    this.fireUpdateEvent(new UpdateEvent(this, BookingOverviewComponent.Commands.UPDATE_APPOINTMENTS, this.getAppointments()));
                    this.fireUpdateEvent(new UpdateEvent(this, BookingListComponent.Commands.UPDATE_BOOKINGS, this.getBookingsAsDisplayableList()));
                }
            }
        }
    }

    @Override
    public boolean removeObserver(final EventListener eventListener) {
        return this.updateEventObservers.remove(eventListener);
    }

    // general methods

    public void exitApplication() {
        if (this.app.getConfig() == null) {
            this.app.setConfig(this.configurationBuilder.build());
        }
        this.app.exitApplication();
    }

    // oberver pattern

    public void fireUpdateEvent(final UpdateEvent updateEvent) {
        for (final var eventListener : this.updateEventObservers) {
            if (eventListener instanceof IUpdateEventListener updateListener) {
                updateListener.processUpdateEvent(updateEvent);
            }
        }
    }

    // event Handlers

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
            this.fireUpdateEvent(new UpdateEvent(this, BookingCreateComponent.Commands.RESET_INPUT));
            this.fireUpdateEvent(new UpdateEvent(this, GUIBuchung.Commands.SWITCH_TAB, GUIBuchung.Tabs.BOOKING_LIST));
        }
    }

    public void handleWindowBookingCreateBookingCreate(final BookingCreateComponent.BookingCreatePayload payload) {
        var hasError = false;
        this.fireUpdateEvent(new UpdateEvent(this, BookingCreateComponent.Commands.ERRORS_RESET));

        if (payload.arrivalDate().isEmpty()) {
            this.fireUpdateEvent(new UpdateEvent(this,
                    BookingCreateComponent.Commands.ERRORS_SHOW_START_DATE,
                    "Bitte geben Sie ein Anreisedatum an."));
            hasError = true;
        } else if (payload.arrivalDate().get().isBefore(LocalDateTime.now())) {
            this.fireUpdateEvent(new UpdateEvent(this,
                    BookingCreateComponent.Commands.ERRORS_SHOW_START_DATE,
                    "Das Anreisedatum muss in der Zukunft liegen."));
            hasError = true;
        }

        if (payload.departureDate().isEmpty()) {
            this.fireUpdateEvent(new UpdateEvent(this, BookingCreateComponent.Commands.ERRORS_SHOW_END_DATE, "Bitte geben Sie ein Abreisedatum an."));
            hasError = true;
        }


        if (payload.responsibleGuest().isEmpty()) {
            this.fireUpdateEvent(new UpdateEvent(this,
                    BookingCreateComponent.Commands.ERRORS_SHOW_GUEST,
                    "Bitte wählen Sie einen verantwortlichen Gast aus."));
            hasError = true;
        }

        final var pitch = (Stellplatz) payload.bookedPitch();

        if (payload.arrivalDate().isPresent() && payload.departureDate().isPresent()) {
            final var arrivalDate = payload.arrivalDate().get();
            final var departureDate = payload.departureDate().get();

            if (arrivalDate.isAfter(departureDate)) {
                this.fireUpdateEvent(new UpdateEvent(this,
                        BookingCreateComponent.Commands.ERRORS_SHOW_END_DATE,
                        "Bitte geben Sie ein Abreisedatum an."));
                hasError = true;
            }

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
                this.fireUpdateEvent(new UpdateEvent(this,
                        BookingCreateComponent.Commands.ERRORS_SHOW_PITCH,
                        "Der Stellplatz ist in diesem Zeitraum bereits belegt. Bitte wählen Sie einen anderen Stellplatz aus."));
                hasError = true;
            }

            final var serviceErrors = payload.bookedServices()
                    .stream()
                    .map(s -> (GebuchteLeistung) s)
                    .filter(s -> s.getBuchungStart().isBefore(arrivalDate.toLocalDate()) || s.getBuchungsEnde().isAfter(departureDate.toLocalDate()))
                    .map(s -> "Gebuchte Leistung " + s.getVisibleText() + " ist nicht im Buchungszeitraum")
                    .collect(Collectors.joining("\n"));

            if (!serviceErrors.isEmpty()) {
                this.fireUpdateEvent(new UpdateEvent(this, BookingCreateComponent.Commands.ERRORS_SHOW_SERVICES, serviceErrors));
                hasError = true;
            }
        }

        if (hasError) {
            return;
        }

        final var arrivalDate = payload.arrivalDate().get();
        final var departureDate = payload.departureDate().get();
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

        try {
            this.database.transaction(() -> {
                this.database.upsert(Gast.class, booking.getVerantwortlicherGast());
                for (final var associatedGuest : booking.getZugehoerigeGaeste()) {
                    this.database.upsert(Gast.class, associatedGuest);
                }
                for (final var bookedService : booking.getGebuchteLeistungen()) {
                    this.database.upsert(GebuchteLeistung.class, bookedService);
                }
                for (final var equipment : booking.getMitgebrachteAusruestung()) {
                    this.database.upsert(Ausruestung.class, equipment);
                }
                this.database.create(Buchung.class, booking);
            });
        } catch (IOException e) {
            AppLogger.getInstance().error("Failed to create booking in database.");
            AppLogger.getInstance().error(e);
            JOptionPane.showMessageDialog(null, "Die Buchung konnte nicht erstellt werden.", "Fehler", JOptionPane.ERROR_MESSAGE);
            return;
        }
        this.entityManager.persist(booking.getVerantwortlicherGast());
        for (final var associatedGuest : booking.getZugehoerigeGaeste()) {
            this.entityManager.persist(associatedGuest);
        }
        for (final var bookedService : booking.getGebuchteLeistungen()) {
            this.entityManager.persist(bookedService);
        }
        for (final var equipment : booking.getMitgebrachteAusruestung()) {
            this.entityManager.persist(equipment);
        }
        this.entityManager.persist(booking);
        this.doEntityUpdate();
        this.fireUpdateEvent(new UpdateEvent(this, BookingCreateComponent.Commands.RESET_INPUT));
        this.fireUpdateEvent(new UpdateEvent(this, GUIBuchung.Commands.SWITCH_TAB, GUIBuchung.Tabs.BOOKING_LIST));
    }

    public void handleWindowBookingCreateDeleteChipCard(final List<Chipkarte> selectedChipCards, final Chipkarte deletedChipCard) {
        final var newSelectedChipCards = new ArrayList<>(selectedChipCards);
        newSelectedChipCards.remove(deletedChipCard);
        if (newSelectedChipCards.size() != selectedChipCards.size()) {
            this.fireUpdateEvent(new UpdateEvent(
                    this,
                    BookingCreateComponent.Commands.SET_SELECTED_CHIPCARDS,
                    newSelectedChipCards
            ));
        }
    }

    public void handleWindowBookingCreateDeleteEquipment(final List<Ausruestung> rentedEquipment, final Ausruestung equipmentToDelete) {
        final var newRentedEquipment = new ArrayList<>(rentedEquipment);
        newRentedEquipment.remove(equipmentToDelete);
        if (newRentedEquipment.size() != rentedEquipment.size()) {
            this.fireUpdateEvent(new UpdateEvent(
                    this,
                    BookingCreateComponent.Commands.SET_RENTED_EQUIPMENT,
                    newRentedEquipment
            ));
        }
    }

    public void handleWindowBookingCreateDeleteService(final List<GebuchteLeistung> bookedServices, final GebuchteLeistung serviceToDelete) {
        final var newBookedServices = new ArrayList<>(bookedServices);
        newBookedServices.remove(serviceToDelete);
        if (newBookedServices.size() != bookedServices.size()) {
            this.fireUpdateEvent(new UpdateEvent(
                    this,
                    BookingCreateComponent.Commands.SET_BOOKED_SERVICES,
                    newBookedServices
            ));
        }
    }

    public void handleWindowBookingCreateEditEquipment(final List<Ausruestung> rentedEquipment,
                                                       final Ausruestung equipmentToEdit,
                                                       final int countDelta) {
        final var newRentEquipment = new ArrayList<>(rentedEquipment);
        final var index = newRentEquipment.indexOf(equipmentToEdit);
        if (index == -1) {
            return;
        }

        final var newCount = equipmentToEdit.getAnzahl() + countDelta;
        if (newCount <= 0) {
            newRentEquipment.remove(equipmentToEdit);
            this.fireUpdateEvent(new UpdateEvent(
                    this,
                    BookingCreateComponent.Commands.SET_RENTED_EQUIPMENT,
                    rentedEquipment
            ));
        } else {
            rentedEquipment.get(index).setAnzahl(newCount);
            this.fireUpdateEvent(new UpdateEvent(
                    this,
                    BookingCreateComponent.Commands.SET_RENTED_EQUIPMENT,
                    rentedEquipment
            ));
        }

    }

    @SuppressWarnings("unchecked")
    public void handleWindowBookingCreateGuestDeleted(final List<Gast> selectedGuests,
                                                      final Gast deletedGuest,
                                                      @SuppressWarnings("OptionalUsedAsFieldOrParameterType") final Optional<Gast> responsibleGuest) {
        final var newSelectedGuests = new ArrayList<>(selectedGuests);
        newSelectedGuests.remove(deletedGuest);

        if (selectedGuests.size() == newSelectedGuests.size()) {
            return;
        }

        final var newResponsibleGuest = responsibleGuest.isPresent() && responsibleGuest.get().equals(deletedGuest)
                ? Optional.<Gast>empty()
                : responsibleGuest;

        this.fireUpdateEvent(new UpdateEvent(
                this,
                BookingCreateComponent.Commands.SET_ASSOCIATED_GUESTS,
                new Payload.GuestList(newSelectedGuests, newResponsibleGuest)
        ));
    }

    public void handleWindowBookingCreateResponsibleGuestSelected(final List<Gast> selectedGuests, final Gast responsibleGuest) {
        this.fireUpdateEvent(new UpdateEvent(
                this,
                BookingCreateComponent.Commands.SET_ASSOCIATED_GUESTS,
                new Payload.GuestList(
                        selectedGuests,
                        Optional.of(responsibleGuest)
                )
        ));
    }

    public void handleWindowBookingCreateSelectChipCard(final Chipkarte newlySelectedChipkarte) {
        this.fireUpdateEvent(new UpdateEvent(
                this,
                BookingCreateComponent.Commands.ADD_SELECTED_CHIPCARD,
                newlySelectedChipkarte
        ));
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

    // Initialization

    public void initialize() {
        this.addObserver(this);
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
        this.doEntityUpdate();
        this.fireUpdateEvent(new UpdateEvent(this, BookingOverviewComponent.Commands.UPDATE_WEEK, LocalDate.now()));

        // Custom text for JOptionPane in correct language
        UIManager.put("OptionPane.yesButtonText", "Ja");
        UIManager.put("OptionPane.noButtonText", "Nein");
    }

    // Dialogs

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public void openDialogDatePicker(
            final GUIComponent parentComponent,
            final EventCommand eventToEmit,
            final Optional<LocalDate> optionalDate
    ) {
        // create dialog
        final var calendarComponent = new CalendarComponent(this.getConfig(), optionalDate);

        // set window properties
        final var windowLocation = this.getConfig().getWindowLocation("Dialog::CalendarComponent").withWidth(300).withHeight(300);
        final var parentWindow = this.getNearestWindow(parentComponent);

        // react to changes in dialog
        calendarComponent.addObserver((IGUIEventListener) guiEvent -> {
            if (guiEvent.getCmd() != CalendarComponent.Commands.BUTTON_PRESSED_DATE_SELECTED) {
                return;
            }
            final var dialog = SwingUtilities.getWindowAncestor(calendarComponent);
            dialog.dispatchEvent(new WindowEvent(dialog, WindowEvent.WINDOW_CLOSING));
            this.fireUpdateEvent(new UpdateEvent(
                    this,
                    eventToEmit,
                    guiEvent.getData()
            ));
        });

        // open Dialog
        this.openInDialog(calendarComponent, parentWindow, "Datum auswählen", windowLocation, (e) -> {
            final var window = e.getWindow();
            this.app.getConfig().setWindowLocation("Dialog::CalendarComponent", WindowLocation.from(window));
            window.dispose();
            return true;
        });
    }

    @SuppressWarnings("unchecked")
    public void openDialogEditService(
            final GUIComponent parentComponent,
            final EventCommand eventToEmit,
            final List<GebuchteLeistung> services,
            final GebuchteLeistung serviceToEdit
    ) {
        // create dialog
        final var serviceSelectorComponent = new ServiceSelectorComponent(this.getConfig());
        this.addObserver(serviceSelectorComponent);

        // create data for dialog
        final var servicesTypes = this.entityManager.find(Leistungsbeschreibung.class);
        this.fireUpdateEvent(new UpdateEvent(this, ServiceSelectorComponent.Commands.UPDATE_SERVICE_TYPES, servicesTypes));
        this.fireUpdateEvent(new UpdateEvent(this, ServiceSelectorComponent.Commands.SET_MODE, ServiceSelectorComponent.Mode.EDIT));
        this.fireUpdateEvent(new UpdateEvent(this, ServiceSelectorComponent.Commands.SET_START_DATE, serviceToEdit.getBuchungStart()));
        this.fireUpdateEvent(new UpdateEvent(this, ServiceSelectorComponent.Commands.SET_END_DATE, serviceToEdit.getBuchungsEnde()));
        this.fireUpdateEvent(new UpdateEvent(this,
                ServiceSelectorComponent.Commands.SET_SELECTED_SERVICE_TYPE,
                serviceToEdit.getLeistungsbeschreibung()));

        // set window properties
        final var parentWindow = this.getNearestWindow(parentComponent);
        final var windowLocation = this.getConfig().getWindowLocation("Dialog::ServiceSelector")
                .withWidth(440)
                .withHeight(240);

        // react to changes in dialog
        serviceSelectorComponent.addObserver((IGUIEventListener) guiEvent -> {
            if (guiEvent.getCmd() == ServiceSelectorComponent.Commands.BUTTON_PRESSED_SELECT_START_DATE) {
                this.openDialogDatePicker(
                        parentComponent,
                        ServiceSelectorComponent.Commands.SET_START_DATE,
                        (Optional<LocalDate>) guiEvent.getData()
                );
            } else if (guiEvent.getCmd() == ServiceSelectorComponent.Commands.BUTTON_PRESSED_SELECT_END_DATE) {
                this.openDialogDatePicker(
                        parentComponent,
                        ServiceSelectorComponent.Commands.SET_END_DATE,
                        (Optional<LocalDate>) guiEvent.getData()
                );
            } else if (guiEvent.getCmd() == ServiceSelectorComponent.Commands.BUTTON_PRESSED_CANCEL) {
                final var dialog = SwingUtilities.getWindowAncestor(serviceSelectorComponent);
                dialog.dispatchEvent(new WindowEvent(dialog, WindowEvent.WINDOW_CLOSING));
            } else if (guiEvent.getCmd() == ServiceSelectorComponent.Commands.BUTTON_PRESSED_SAVE) {
                final var payload = (Payload.ServiceCreation) guiEvent.getData();
                if (payload.startDate().isEmpty() && payload.endDate().isEmpty()) {
                    JOptionPane.showMessageDialog(
                            parentComponent,
                            "Bitte geben Sie ein Start- und Enddatum an.",
                            "Fehler",
                            JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }
                if (payload.startDate().isEmpty()) {
                    JOptionPane.showMessageDialog(
                            parentComponent,
                            "Bitte geben Sie ein Startdatum an.",
                            "Fehler",
                            JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }
                if (payload.endDate().isEmpty()) {
                    JOptionPane.showMessageDialog(
                            parentComponent,
                            "Bitte geben Sie ein Enddatum an.",
                            "Fehler",
                            JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }
                if (payload.startDate().get().isAfter(payload.endDate().get())) {
                    JOptionPane.showMessageDialog(
                            parentComponent,
                            "Das Startdatum muss vor dem Enddatum liegen.",
                            "Fehler",
                            JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }

                final var dialog = SwingUtilities.getWindowAncestor(serviceSelectorComponent);
                final var index = services.indexOf(serviceToEdit);
                if (index == -1) {
                    JOptionPane.showMessageDialog(null, "Die Leistung konnte nicht bearbeitet werden.", "Fehler", JOptionPane.ERROR_MESSAGE);
                    this.removeObserver(serviceSelectorComponent);
                    dialog.dispose();
                    return;
                }

                final var serviceType = (Leistungsbeschreibung) payload.serviceType();
                services.get(index).setLeistungsbeschreibung(serviceType);
                services.get(index).setBuchungStart(payload.startDate().get());
                services.get(index).setBuchungsEnde(payload.endDate().get());

                this.removeObserver(serviceSelectorComponent);
                dialog.dispose();
                this.fireUpdateEvent(new UpdateEvent(
                        this,
                        eventToEmit,
                        services
                ));

            }
        });

        // open Dialog
        this.openInDialog(serviceSelectorComponent, parentWindow, "Leistung bearbeiten", windowLocation, (e) -> {
            final var decision = JOptionPane.showConfirmDialog(
                    null,
                    "Wollen Sie die Bearbeitung der Leistung wirklich abbrechen?",
                    "Leistungsbearbeitung abbrechen",
                    JOptionPane.YES_NO_OPTION
            );

            final var close = decision == JOptionPane.YES_OPTION;
            if (close) {
                this.removeObserver(serviceSelectorComponent);
                final var window = e.getWindow();
                this.app.getConfig().setWindowLocation("Dialog::ServiceSelector", WindowLocation.from(window));
            }
            return close;
        });
    }

    public void openDialogEquipmentSelector(
            final GUIComponent parentComponent,
            final EventCommand eventToEmit
    ) {
        // create dialog
        final var equipmentSelectorComponent = new EquipmentSelectorComponent(this.getConfig());
        this.addObserver(equipmentSelectorComponent);

        // create data for dialog
        final var vehicleTypes = Arrays.stream(Fahrzeug.Typ.values()).toList();
        this.fireUpdateEvent(new UpdateEvent(this, EquipmentSelectorComponent.Commands.SET_VEHICLE_TYPES, vehicleTypes));

        // set window properties
        final var windowLocation = this.getConfig().getWindowLocation("Dialog::EquipmentSelector").withWidth(440).withHeight(440);
        final var parentWindow = this.getNearestWindow(parentComponent);

        // react to changes in dialog
        equipmentSelectorComponent.addObserver((IGUIEventListener) guiEvent -> {
            if (guiEvent.getCmd() == EquipmentSelectorComponent.Commands.BUTTON_PRESSED_CANCEL) {
                final var dialog = SwingUtilities.getWindowAncestor(equipmentSelectorComponent);
                dialog.dispatchEvent(new WindowEvent(dialog, WindowEvent.WINDOW_CLOSING));
            } else if (guiEvent.getCmd() == EquipmentSelectorComponent.Commands.BUTTON_PRESSED_SAVE) {
                final var payload = (EquipmentSelectorComponent.SavePayload) guiEvent.getData();
                if (payload.description().isEmpty()) {
                    JOptionPane.showMessageDialog(
                            parentComponent,
                            "Bitte geben Sie eine Bezeichnung an.",
                            "Fehler",
                            JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }
                if (payload.height().isEmpty() || payload.width().isEmpty()) {
                    JOptionPane.showMessageDialog(
                            parentComponent,
                            "Bitte geben Sie eine Höhe und Breite an.",
                            "Fehler",
                            JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }
                if ((payload.licensePlate().isEmpty() && payload.vehicleTyp().isPresent()) || (payload.licensePlate()
                        .isPresent() && payload.vehicleTyp().isEmpty())) {
                    JOptionPane.showMessageDialog(
                            parentComponent,
                            "Wenn Sie ein Fahrzeug erstellen möchten, muss sowohl Kennzeichen als auch Fahrzeugtyp angegeben werden.",
                            "Fehler",
                            JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }

                final var equipment = payload.licensePlate().isPresent() && payload.vehicleTyp().isPresent()
                        ? new Fahrzeug(
                        this.entityManager.generateNextPrimaryKey(Ausruestung.class),
                        payload.description().get(),
                        payload.amount(),
                        payload.height().get(),
                        payload.width().get(),
                        payload.licensePlate().get(),
                        (Fahrzeug.Typ) payload.vehicleTyp().get()
                ) : new Ausruestung(
                        this.entityManager.generateNextPrimaryKey(Ausruestung.class),
                        payload.description().get(),
                        payload.amount(),
                        payload.height().get(),
                        payload.width().get()
                );

                final var dialog = SwingUtilities.getWindowAncestor(equipmentSelectorComponent);
                this.removeObserver(equipmentSelectorComponent);
                dialog.dispose();
                this.fireUpdateEvent(new UpdateEvent(
                        this,
                        eventToEmit,
                        equipment
                ));
            }
        });

        // open Dialog
        this.openInDialog(equipmentSelectorComponent, parentWindow, "Ausrüstung auswählen", windowLocation, (e) -> {
            final var decision = JOptionPane.showConfirmDialog(
                    null,
                    "Wollen Sie die Auswahl einer Ausrüstung wirklich abbrechen?",
                    "Ausrüstungsauswahl abbrechen",
                    JOptionPane.YES_NO_OPTION
            );

            final var close = decision == JOptionPane.YES_OPTION;
            if (close) {
                this.removeObserver(equipmentSelectorComponent);
                final var window = e.getWindow();
                this.app.getConfig().setWindowLocation("Dialog::EquipmentSelector", WindowLocation.from(window));
            }
            return close;
        });
    }

    public void openDialogGuestCreate(
            final GUIComponent parentComponent,
            @SuppressWarnings("unused") final EventCommand eventToEmit
    ) {
        final var parentWindow = this.getNearestWindow(parentComponent);
        final var windowLocation = this.getConfig().getWindowLocation("Dialog::GuestCreate").withWidth(350).withHeight(350);
        this.openInDialog(new GuestCreateComponent(this.getConfig()), parentWindow, "Gast anlegen", windowLocation, (event) -> {
            final var window = event.getWindow();
            this.app.getConfig().setWindowLocation("Dialog::GuestCreate", WindowLocation.from(window));
            return true;
        });
    }

    public void openDialogGuestSelector(
            final GUIComponent parentComponent,
            final EventCommand eventToEmit,
            final Set<Gast> excludedGuests
    ) {
        // create dialog
        final var guestSelectorComponent = new GuestSelectorComponent(this.getConfig());
        this.addObserver(guestSelectorComponent);

        // set window properties
        final var parentWindow = this.getNearestWindow(parentComponent);
        final var windowLocation = this.getConfig().getWindowLocation("Dialog::GuestSelector").withWidth(400).withHeight(320);

        // create data for dialog
        final var guests = this.entityManager
                .find(Gast.class)
                .stream()
                .filter(g -> !excludedGuests.contains(g))
                .toList();
        this.fireUpdateEvent(new UpdateEvent(this, GuestSelectorComponent.Commands.UPDATE_GUESTS, guests));

        // react to changes in dialog
        guestSelectorComponent.addObserver((IGUIEventListener) guiEvent -> {
            if (guiEvent.getCmd() == GuestSelectorComponent.Commands.SEARCH_INPUT_CHANGED) {
                final var payload = ((GuestSelectorComponent.SearchInputChangedPayload) guiEvent.getData());
                final var searchInput = payload.text().toLowerCase();
                final var filteredGuests = payload.guests()
                        .stream()
                        .filter(g -> ((Gast) g).getName().toLowerCase().contains(searchInput))
                        .toList();

                if (filteredGuests.size() != payload.guests().size()) {
                    this.fireUpdateEvent(new UpdateEvent(
                            this,
                            GuestSelectorComponent.Commands.UPDATE_GUESTS,
                            filteredGuests
                    ));
                }
            } else if (guiEvent.getCmd() == GuestSelectorComponent.Commands.BUTTON_PRESSED_ADD_GUEST) {
                this.openDialogGuestCreate((GUIComponent) guiEvent.getSource(), GuestSelectorComponent.Commands.SELECT_GUEST);
            } else if (guiEvent.getCmd() == GuestSelectorComponent.Commands.BUTTON_PRESSED_GUEST_SELECTED) {
                final var guest = (Gast) guiEvent.getData();

                final var dialog = SwingUtilities.getWindowAncestor(guestSelectorComponent);
                this.removeObserver(guestSelectorComponent);
                dialog.dispose();

                this.fireUpdateEvent(new UpdateEvent(
                        GUIController.this,
                        eventToEmit,
                        guest
                ));
            }
        });

        // open Dialog
        this.openInDialog(guestSelectorComponent, parentWindow, "Gast auswählen", windowLocation, (event) -> {
            final var decision = JOptionPane.showConfirmDialog(
                    null,
                    "Wollen Sie die Auswahl von einem neuen Gast wirklich abbrechen?",
                    "Gastauswahl abbrechen",
                    JOptionPane.YES_NO_OPTION
            );

            final var close = decision == JOptionPane.YES_OPTION;
            if (close) {
                this.removeObserver(guestSelectorComponent);
                final var window = event.getWindow();
                this.app.getConfig().setWindowLocation("Dialog::GuestSelector", WindowLocation.from(window));
            }
            return close;
        });
    }

    public void openDialogPitchSelector(
            final GUIComponent parentComponent,
            final EventCommand eventToEmit
    ) {
        // create dialog
        final var pitchSelectorComponent = new PitchSelectorComponent(this.getConfig());
        this.addObserver(pitchSelectorComponent);

        // create data for dialog
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
        this.fireUpdateEvent(new UpdateEvent(this, PitchSelectorComponent.Commands.UPDATE_PITCHES, pitches));

        // set window properties
        final var parentWindow = this.getNearestWindow(parentComponent);
        pitchSelectorComponent.setSizeWithWidth(720);
        final var windowLocation = this.getConfig().getWindowLocation("Dialog::PitchSelector")
                .withWidth(pitchSelectorComponent.getImageWidth())
                .withHeight(pitchSelectorComponent.getImageHeight());

        // react to changes in dialog
        pitchSelectorComponent.addObserver((IGUIEventListener) guiEvent -> {
            if (guiEvent.getCmd() == PitchSelectorComponent.Commands.PITCH_SELECTED) {
                final var pitch = (Stellplatz) guiEvent.getData();
                final var dialog = SwingUtilities.getWindowAncestor(pitchSelectorComponent);
                this.removeObserver(pitchSelectorComponent);
                dialog.dispose();
                this.fireUpdateEvent(new UpdateEvent(
                        this,
                        eventToEmit,
                        pitch
                ));
            }
        });

        // open Dialog
        this.openInDialog(pitchSelectorComponent, parentWindow, "Stellplatz auswählen", windowLocation, (e) -> {
            final var decision = JOptionPane.showConfirmDialog(
                    null,
                    "Wollen Sie die Auswahl eines Stellplatzes wirklich abbrechen?",
                    "Stellplatzauswahl abbrechen",
                    JOptionPane.YES_NO_OPTION
            );

            final var close = decision == JOptionPane.YES_OPTION;
            if (close) {
                this.removeObserver(pitchSelectorComponent);
                final var window = e.getWindow();
                this.app.getConfig().setWindowLocation("Dialog::PitchSelector", WindowLocation.from(window));
            }
            return close;
        });
    }

    @SuppressWarnings("unchecked")
    public void openDialogServiceSelector(
            final GUIComponent parentComponent,
            final EventCommand eventToEmit
    ) {
        // create dialog
        final var serviceSelectorComponent = new ServiceSelectorComponent(this.getConfig());
        this.addObserver(serviceSelectorComponent);

        // create data for dialog
        final var servicesTypes = this.entityManager.find(Leistungsbeschreibung.class);
        this.fireUpdateEvent(new UpdateEvent(
                this,
                ServiceSelectorComponent.Commands.UPDATE_SERVICE_TYPES,
                servicesTypes
        ));

        // set window properties
        final var parentWindow = this.getNearestWindow(parentComponent);
        final var windowLocation = this.getConfig().getWindowLocation("Dialog::ServiceSelector")
                .withWidth(440)
                .withHeight(240);

        // react to changes in dialog
        serviceSelectorComponent.addObserver((IGUIEventListener) guiEvent -> {
            if (guiEvent.getCmd() == ServiceSelectorComponent.Commands.BUTTON_PRESSED_SELECT_START_DATE) {
                this.openDialogDatePicker(
                        parentComponent,
                        ServiceSelectorComponent.Commands.SET_START_DATE,
                        (Optional<LocalDate>) guiEvent.getData()
                );
            } else if (guiEvent.getCmd() == ServiceSelectorComponent.Commands.BUTTON_PRESSED_SELECT_END_DATE) {
                this.openDialogDatePicker(
                        parentComponent,
                        ServiceSelectorComponent.Commands.SET_END_DATE,
                        (Optional<LocalDate>) guiEvent.getData()
                );
            } else if (guiEvent.getCmd() == ServiceSelectorComponent.Commands.BUTTON_PRESSED_CANCEL) {
                final var dialog = SwingUtilities.getWindowAncestor(serviceSelectorComponent);
                dialog.dispatchEvent(new WindowEvent(dialog, WindowEvent.WINDOW_CLOSING));
            } else if (guiEvent.getCmd() == ServiceSelectorComponent.Commands.BUTTON_PRESSED_SAVE) {
                final var payload = (Payload.ServiceCreation) guiEvent.getData();
                if (payload.startDate().isEmpty() && payload.endDate().isEmpty()) {
                    JOptionPane.showMessageDialog(
                            parentComponent,
                            "Bitte geben Sie ein Start- und Enddatum an.",
                            "Fehler",
                            JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }
                if (payload.startDate().isEmpty()) {
                    JOptionPane.showMessageDialog(
                            parentComponent,
                            "Bitte geben Sie ein Startdatum an.",
                            "Fehler",
                            JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }
                if (payload.endDate().isEmpty()) {
                    JOptionPane.showMessageDialog(
                            parentComponent,
                            "Bitte geben Sie ein Enddatum an.",
                            "Fehler",
                            JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }
                if (payload.startDate().get().isAfter(payload.endDate().get())) {
                    JOptionPane.showMessageDialog(
                            parentComponent,
                            "Das Startdatum muss vor dem Enddatum liegen.",
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

                final var serviceType = (Leistungsbeschreibung) payload.serviceType();
                bookedService.setLeistungsbeschreibung(serviceType);

                final var dialog = SwingUtilities.getWindowAncestor(serviceSelectorComponent);
                this.removeObserver(serviceSelectorComponent);
                dialog.dispose();
                this.fireUpdateEvent(new UpdateEvent(
                        this,
                        eventToEmit,
                        bookedService
                ));

            }
        });

        // open Dialog
        this.openInDialog(serviceSelectorComponent, parentWindow, "Leistung auswählen", windowLocation, (e) -> {
            final var decision = JOptionPane.showConfirmDialog(
                    null,
                    "Wollen Sie die Auswahl einer Leistung wirklich abbrechen?",
                    "Leistungsauswahl abbrechen",
                    JOptionPane.YES_NO_OPTION
            );

            final var close = decision == JOptionPane.YES_OPTION;
            if (close) {
                this.removeObserver(serviceSelectorComponent);
                final var window = e.getWindow();
                this.app.getConfig().setWindowLocation("Dialog::ServiceSelector", WindowLocation.from(window));
            }
            return close;
        });
    }

    // Windows

    public void openWindowBooking() {
        if (this.windowBooking.isDisplayable()) {
            this.windowBooking.grabFocus();
            return;
        }

        this.openInWindow(this.windowBooking, "Buchungen", "Window::Booking", event -> {
            ((JFrame) SwingUtilities.getWindowAncestor(this.windowMain)).setState(Frame.NORMAL);
            this.windowMain.grabFocus();
            return true;
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
            return true;
        });
    }

    public void openWindowConfiguration(final PropertyManager propertyManager) throws Exception {
        this.configurationBuilder = Configuration.builder().propertyManager(propertyManager);
        this.windowConfiguration = new GUIConfiguration(this.configurationBuilder.build());
        this.windowConfigurationObserver = new GUIConfigurationObserver();
        this.windowConfiguration.addObserver(this.windowConfigurationObserver);
        this.addObserver(this.windowConfiguration);
        // Main GUI and Configuration GUI have the same window location
        final var windowLocation = this.configurationBuilder.build().getWindowLocation("Window::Main");
        this.openInWindow(this.windowConfiguration, "Konfiguration", windowLocation, (event) -> {
            final var window = event.getWindow();
            this.configurationBuilder.build().setWindowLocation("Window::Main", WindowLocation.from(window));
            this.exitApplication();
            return true;
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
            return true;
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
            return true;
        });
    }

    public void openWindowMain(final PropertyManager propertyManager) throws Exception {
        this.configurationBuilder = Configuration.builder().propertyManager(propertyManager);
        this.app.setConfig(this.configurationBuilder.build());

        this.initialize();

        final var windowLocation = this.app.getConfig().getWindowLocation("Window::Main");
        this.openInWindow(this.windowMain, "Campingplatzverwaltung", windowLocation, (event) -> {
            final var window = event.getWindow();
            this.app.getConfig().setWindowLocation("Window::Main", WindowLocation.from(window));
            this.exitApplication();
            return true;
        });
    }

    public void openWindowMain() {
        final var configWindow = (SwingUtilities.getWindowAncestor(this.windowConfiguration));
        this.windowConfiguration.removeObserver(this.windowConfigurationObserver);
        this.removeObserver(this.windowConfiguration);
        this.windowConfigurationObserver = null;
        this.app.setConfig(this.configurationBuilder.build());
        final var windowLocation = WindowLocation.from(configWindow);
        this.app.getConfig().setWindowLocation("Window::Main", windowLocation);
        configWindow.dispose();

        this.initialize();

        this.openInWindow(this.windowMain, "Campingplatzverwaltung", windowLocation, (event) -> {
            final var window = event.getWindow();
            this.app.getConfig().setWindowLocation("Window::Main", WindowLocation.from(window));
            this.exitApplication();
            return true;
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
            return true;
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
            return true;
        });
    }

    // General methods

    private void doEntityUpdate() {
        this.fireUpdateEvent(new UpdateEvent(this, Commands.UPDATE_ADDRESSES, this.entityManager.find(Adresse.class)));
        this.fireUpdateEvent(new UpdateEvent(this, Commands.UPDATE_EQUIPMENT, this.entityManager.find(Ausruestung.class)
                .stream()
                .sorted(Ausruestung::compareTo)
                .collect(Collectors.toList())));
        this.fireUpdateEvent(new UpdateEvent(this, Commands.UPDATE_AREAS, this.entityManager.find(Bereich.class)));
        this.fireUpdateEvent(new UpdateEvent(this, Commands.UPDATE_BOOKINGS, this.entityManager.find(Buchung.class)
                .stream()
                .sorted(Buchung::compareTo)
                .collect(Collectors.toList())));
        this.fireUpdateEvent(new UpdateEvent(this, Commands.UPDATE_CHIPCARDS, this.entityManager.find(Chipkarte.class)
                .stream()
                .sorted(Chipkarte::compareTo)
                .collect(Collectors.toList())));
        this.fireUpdateEvent(new UpdateEvent(this, Commands.UPDATE_FACILITIES, this.entityManager.find(Einrichtung.class)));
        this.fireUpdateEvent(new UpdateEvent(this, Commands.UPDATE_CONTRACTORS, this.entityManager.find(Fremdfirma.class)));
        this.fireUpdateEvent(new UpdateEvent(this, Commands.UPDATE_GUESTS, this.entityManager.find(Gast.class)
                .stream()
                .sorted(Gast::compareTo)
                .collect(Collectors.toList())));
        this.fireUpdateEvent(new UpdateEvent(this, Commands.UPDATE_BOOKED_SERVICES, this.entityManager.find(GebuchteLeistung.class)
                .stream()
                .sorted(GebuchteLeistung::compareTo)
                .collect(Collectors.toList())));
        this.fireUpdateEvent(new UpdateEvent(this, Commands.UPDATE_SERVICES, this.entityManager.find(Leistungsbeschreibung.class)));
        this.fireUpdateEvent(new UpdateEvent(this, Commands.UPDATE_OPENING_DAYS, this.entityManager.find(Oeffnungstag.class)));
        this.fireUpdateEvent(new UpdateEvent(this, Commands.UPDATE_OPENING_HOURS, this.entityManager.find(Oeffnungszeit.class)));
        this.fireUpdateEvent(new UpdateEvent(this, Commands.UPDATE_PERSONS, this.entityManager.find(Person.class)));
        this.fireUpdateEvent(new UpdateEvent(this, Commands.UPDATE_STAFF, this.entityManager.find(Personal.class)));
        this.fireUpdateEvent(new UpdateEvent(this, Commands.UPDATE_INVOICES, this.entityManager.find(Rechnung.class)));
        this.fireUpdateEvent(new UpdateEvent(this, Commands.UPDATE_PITCHES, this.entityManager.find(Stellplatz.class)
                .stream()
                .sorted(Stellplatz::compareTo)
                .collect(Collectors.toList())));
        this.fireUpdateEvent(new UpdateEvent(this, Commands.UPDATE_DISTURBANCES, this.entityManager.find(Stoerung.class)));
        this.fireUpdateEvent(new UpdateEvent(this, Commands.UPDATE_MAINTENANCE, this.entityManager.find(Wartung.class)));
    }

    // Utility Methods

    private JFrame getNearestWindow(final Container content) {
        final var maybeFrame = SwingUtilities.getWindowAncestor(content);
        if (maybeFrame instanceof JFrame frame) {
            return frame;
        }

        return (JFrame) SwingUtilities.getWindowAncestor(maybeFrame);
    }

    @SuppressWarnings("UnusedReturnValue")
    private JDialog openInDialog(final Container content,
                                 final Frame parentWindow,
                                 final String title,
                                 final WindowLocation windowLocation,
                                 final Function<WindowEvent, Boolean> onExit) {
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
                if (onExit.apply(event)) {
                    dialog.dispose();
                }
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
                                final Function<WindowEvent, Boolean> onExit) {
        final var config = Optional
                .ofNullable(this.getConfig())
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
                if (onExit.apply(event)) {
                    frame.dispose();
                }
            }
        });
        return frame;
    }

    @SuppressWarnings("UnusedReturnValue")
    private JFrame openInWindow(final Container content,
                                final String title,
                                final String windowLocationKey,
                                final Function<WindowEvent, Boolean> onExit) {
        final var windowLocation = Optional.ofNullable(this.getConfig())
                .orElse(this.configurationBuilder.build())
                .getWindowLocation(windowLocationKey);
        return this.openInWindow(content, title, windowLocation, (event) -> {
            final var close = onExit.apply(event);
            if (close) {
                final var window = event.getWindow();
                this.app.getConfig().setWindowLocation(windowLocationKey, WindowLocation.from(window));
            }
            return close;
        });
    }
}
