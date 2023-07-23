package swe.ka.dhbw.control;

import de.dhbwka.swe.utils.event.*;
import de.dhbwka.swe.utils.model.Attribute;
import de.dhbwka.swe.utils.model.IDepictable;
import de.dhbwka.swe.utils.util.PropertyManager;
import swe.ka.dhbw.database.EntityManager;
import swe.ka.dhbw.event.GUIBuchungObserver;
import swe.ka.dhbw.event.GUIConfigurationObserver;
import swe.ka.dhbw.event.GUIMainObserver;
import swe.ka.dhbw.model.*;
import swe.ka.dhbw.ui.*;
import swe.ka.dhbw.ui.components.BookingCreateComponent;
import swe.ka.dhbw.ui.components.BookingOverviewComponent;
import swe.ka.dhbw.ui.components.CalendarComponent;
import swe.ka.dhbw.ui.components.GuestSelectorComponent;
import swe.ka.dhbw.util.WindowLocation;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GUIController implements IUpdateEventSender {
    public enum Commands implements EventCommand {
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
                                } catch (Exception e) {
                                    // ignore exception as it will not occur
                                }
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
                }).collect(HashMap::new, (map, entry) -> {
                    map.put(entry.getKey(), entry.getValue());
                }, HashMap::putAll);
    }

    private List<? extends IDepictable> getBuchungen() {
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
                            new Attribute("Weitere Gäste", b, String.class, b.getZugehoerigeGaeste().stream().map(g -> g.getName()).collect(Collectors.joining(", ")), null, true, false, false, true),
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

    public void fireUpdateEvent(final UpdateEvent updateEvent) {
        for (final var eventListener : this.updateEventObservers) {
            if (eventListener instanceof IUpdateEventListener updateListener) {
                updateListener.processUpdateEvent(updateEvent);
            }
        }
    }

    public void handleWindowBookingAppointmentOverviewNextWeek(final LocalDate currentWeek) {
        this.fireUpdateEvent(new UpdateEvent(
                        this,
                        BookingOverviewComponent.Commands.UPDATE_WEEK,
                        currentWeek.plusWeeks(1)
                )
        );
    }

    // Event Handlers

    public void handleWindowBookingAppointmentOverviewPreviousWeek(final LocalDate currentWeek) {
        this.fireUpdateEvent(new UpdateEvent(
                        this,
                        BookingOverviewComponent.Commands.UPDATE_WEEK,
                        currentWeek.minusWeeks(1)
                )
        );
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
        final var decision = JOptionPane.showConfirmDialog(null,
                "Wollen Sie die Erstellung der Buchung wirklich abbrechen?",
                "Buchung abbrechen",
                JOptionPane.YES_NO_OPTION);

        if (decision == JOptionPane.YES_OPTION) {
            this.fireUpdateEvent(new UpdateEvent(
                    this,
                    BookingCreateComponent.Commands.RESET
            ));
            this.fireUpdateEvent(new UpdateEvent(
                    this,
                    GUIBuchung.Commands.SWITCH_TAB,
                    GUIBuchung.Tabs.BOOKING_LIST
            ));
        }
    }

    public void handleWindowConfigurationSetAccentColor(final Color currentColor) {
        final var nextColor = JColorChooser.showDialog(this.windowConfiguration, "Farbe auswählen", currentColor);
        if (nextColor != null) {
            this.configurationBuilder = this.configurationBuilder.accentColor(nextColor);
            this.fireUpdateEvent(new UpdateEvent(
                    this,
                    GUIConfiguration.Commands.REBUILD_UI,
                    this.configurationBuilder.build()
            ));
        }
    }

    public void handleWindowConfigurationSetDarkMode(final boolean darkModeActive) {
        if (darkModeActive) {
            this.configurationBuilder = this.configurationBuilder.darkMode();
        } else {
            this.configurationBuilder = this.configurationBuilder.lightMode();
        }
        this.fireUpdateEvent(new UpdateEvent(
                this,
                GUIConfiguration.Commands.REBUILD_UI,
                this.configurationBuilder.build()
        ));
    }

    public void handleWindowConfigurationSetTextFont(final Font font) {
        this.configurationBuilder = this.configurationBuilder.font(font);
        this.fireUpdateEvent(new UpdateEvent(
                this,
                GUIConfiguration.Commands.REBUILD_UI,
                this.configurationBuilder.build()
        ));
    }

    public void handleWindowMainCreateBooking() {
        this.openWindowBooking();
        this.fireUpdateEvent(new UpdateEvent(this, GUIBuchung.Commands.SWITCH_TAB, GUIBuchung.Tabs.BOOKING_CREATE));
    }

    // Dialogs

    public void openDialogDatePicker(final Optional<LocalDate> optionalDate, final GUIComponent source, final String cmdText) {
        final var windowLocation = this.getConfig().getWindowLocation("Dialog::CalendarComponent").withWidth(300).withHeight(300);
        final var calendarComponent = new CalendarComponent(this.getConfig(), optionalDate);
        final var parentWindow = (JFrame) SwingUtilities.getWindowAncestor(source);
        final var dialog = this.openInDialog(calendarComponent, parentWindow, "Datum auswählen", windowLocation, (event) -> {
            final var window = event.getWindow();
            this.app.getConfig().setWindowLocation("Dialog::CalendarComponent", WindowLocation.from(window));
        });
        calendarComponent.addObserver((IGUIEventListener) guiEvent -> {
            if (guiEvent.getCmd() != CalendarComponent.Commands.DATE_SELECTED) {
                return;
            }
            dialog.dispatchEvent(new WindowEvent(dialog, WindowEvent.WINDOW_CLOSING));
            GUIController.this.fireUpdateEvent(new UpdateEvent(
                    GUIController.this,
                    new EventCommand() {
                        @Override
                        public String getCmdText() {
                            return cmdText;
                        }

                        @Override
                        public Class<?> getPayloadType() {
                            return LocalDate.class;
                        }
                    },
                    guiEvent.getData()
            ));
        });
    }

    public void openDialogGuestSelector(final GUIComponent source, final String cmdText, final Set<Gast> withoutGuests) {
        final var guests = this.entityManager.find(Gast.class).stream().filter(g -> !withoutGuests.contains(g)).toList();
        final var windowLocation = this.getConfig().getWindowLocation("Dialog::GuestSelector").withWidth(300).withHeight(300);
        final var guestSelectorComponent = new GuestSelectorComponent(this.getConfig(), guests);

        final var parentWindow = (JFrame) SwingUtilities.getWindowAncestor(source);
        final var dialog = this.openInDialog(guestSelectorComponent, parentWindow, "Gast auswählen", windowLocation, (event) -> {
            final var window = event.getWindow();
            this.app.getConfig().setWindowLocation("Dialog::GuestSelector", WindowLocation.from(window));
        });
        guestSelectorComponent.addObserver((IGUIEventListener) guiEvent -> {
//            if (guiEvent.getCmd() != GuestSelectorComponent.Commands.GUEST_SELECTED) {
//                return;
//            }
//            dialog.dispatchEvent(new WindowEvent(dialog, WindowEvent.WINDOW_CLOSING));
//            GUIController.this.fireUpdateEvent(new UpdateEvent(
//                    GUIController.this,
//                    new EventCommand() {
//                        @Override
//                        public String getCmdText() {
//                            return cmdText;
//                        }
//
//                        @Override
//                        public Class<?> getPayloadType() {
//                            return Gast.class;
//                        }
//                    },
//                    guiEvent.getData()
//            ));
        });
    }

    // Windows

    @SuppressWarnings("unchecked")
    public void openWindowBooking() {
        if (this.windowBooking != null && this.windowBooking.isDisplayable()) {
            this.windowBooking.grabFocus();
            return;
        }

        if (this.windowBooking == null) {
            this.windowBooking = new GUIBuchung(
                    this.getConfig(),
                    this.getBuchungen(),
                    this.getAppointments(),
                    LocalDate.now(),
                    this.entityManager.find(Chipkarte.class).stream().filter(c -> c.getStatus() == Chipkarte.Status.VERFUEGBAR).toList()
            );
        }

        final var observer = new GUIBuchungObserver();
        this.windowBooking.addObserver(observer);
        this.addObserver(this.windowBooking);

        this.openInWindow(this.windowBooking, "Buchungen", "Window::Booking", event -> {
            ((JFrame) SwingUtilities.getWindowAncestor(this.windowMain)).setState(Frame.NORMAL);
            this.windowMain.grabFocus();
        });
    }

    public void openWindowCheckInCheckOut() {
        if (this.windowCheckInCheckOut != null && this.windowCheckInCheckOut.isDisplayable()) {
            this.windowCheckInCheckOut.grabFocus();
            return;
        }

        if (this.windowCheckInCheckOut == null) {
            this.windowCheckInCheckOut = new GUICheckInCheckOut(this.getConfig());
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
        if (this.windowFacility != null && this.windowFacility.isDisplayable()) {
            this.windowFacility.grabFocus();
            return;
        }

        if (this.windowFacility == null) {
            this.windowFacility = new GUIEinrichtung(this.getConfig());
        }

        this.openInWindow(this.windowFacility, "Einrichtungen", "Window::Facility", event -> {
            ((JFrame) SwingUtilities.getWindowAncestor(this.windowMain)).setState(Frame.NORMAL);
            this.windowMain.grabFocus();
        });
    }

    public void openWindowGuest() {
        if (this.windowGuest != null && this.windowGuest.isDisplayable()) {
            this.windowGuest.grabFocus();
            return;
        }

        if (this.windowGuest == null) {
            this.windowGuest = new GUIGast(this.getConfig());
        }

        this.openInWindow(this.windowGuest, "Gäste", "Window::Guest", event -> {
            ((JFrame) SwingUtilities.getWindowAncestor(this.windowMain)).setState(Frame.NORMAL);
            this.windowMain.grabFocus();
        });
    }

    public void openWindowMain() {
        final var configWindow = (SwingUtilities.getWindowAncestor(this.windowConfiguration));
        this.windowConfiguration.removeObserver(this.windowConfigurationObserver);
        this.removeObserver(this.windowConfiguration);
        this.windowConfigurationObserver = null;
        this.app.setConfig(this.configurationBuilder.build());
        this.app.getConfig().setWindowLocation("Window::Main", WindowLocation.from(configWindow));
        configWindow.dispose();

        final var observer = new GUIMainObserver();
        this.windowMain = new GUIMain(this.getConfig());
        this.windowMain.addObserver(observer);
        this.openInWindow(this.windowMain, "Campingplatzverwaltung", "Window::Main", event -> {
            this.exitApplication();
        });
    }

    public void openWindowPitch() {
        if (this.windowPitch != null && this.windowPitch.isDisplayable()) {
            this.windowPitch.grabFocus();
            return;
        }

        if (this.windowPitch == null) {
            this.windowPitch = new GUIStellplatz(this.getConfig());
        }

        this.openInWindow(this.windowPitch, "Stellplätze", "Window::Pitch", event -> {
            ((JFrame) SwingUtilities.getWindowAncestor(this.windowMain)).setState(Frame.NORMAL);
            this.windowMain.grabFocus();
        });
    }

    public void openWindowStaff() {
        if (this.windowStaff != null && this.windowStaff.isDisplayable()) {
            this.windowStaff.grabFocus();
            return;
        }

        if (this.windowStaff == null) {
            this.windowStaff = new GUIPersonal(this.getConfig());
        }

        this.openInWindow(this.windowStaff, "Personal", "Window::Staff", event -> {
            ((JFrame) SwingUtilities.getWindowAncestor(this.windowMain)).setState(Frame.NORMAL);
            this.windowMain.grabFocus();
        });
    }

    // Utility Methods

    private JDialog openInDialog(final Container content,
                                 final JFrame parentWindow,
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
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setLocation(Math.max(windowLocation.x(), 0), Math.max(windowLocation.y(), 0));
        dialog.setSize(windowLocation.width() > 0 ? windowLocation.width() : 100, windowLocation.height() > 0 ? windowLocation.height() : 100);
        dialog.setResizable(false);
        dialog.setBackground(this.getConfig().getBackgroundColor());
        dialog.setForeground(this.getConfig().getTextColor());
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent event) {
                onExit.accept(event);
                dialog.dispose();
            }
        });
        try {
            dialog.setIconImage(ImageIO.read(Objects.requireNonNull(this.getClass().getResourceAsStream("/Logo.png"))));
        } catch (IOException e) {
            //ignore this case
        }
        dialog.setVisible(true);
        return dialog;
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
        } catch (IOException e) {
            //ignore this case
        }
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
}
