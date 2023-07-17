package swe.ka.dhbw.control;

import de.dhbwka.swe.utils.event.IUpdateEventListener;
import de.dhbwka.swe.utils.event.IUpdateEventSender;
import de.dhbwka.swe.utils.event.UpdateEvent;
import de.dhbwka.swe.utils.model.Attribute;
import de.dhbwka.swe.utils.model.IDepictable;
import de.dhbwka.swe.utils.util.PropertyManager;
import swe.ka.dhbw.database.EntityManager;
import swe.ka.dhbw.event.GUIBuchungObserver;
import swe.ka.dhbw.event.GUIConfigurationObserver;
import swe.ka.dhbw.event.GUIMainObserver;
import swe.ka.dhbw.model.Bereich;
import swe.ka.dhbw.model.Buchung;
import swe.ka.dhbw.model.Foto;
import swe.ka.dhbw.ui.*;
import swe.ka.dhbw.ui.components.BookingOverviewComponent;
import swe.ka.dhbw.util.WindowLocation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class GUIController implements IUpdateEventSender {
    private static GUIController instance;
    private final Set<EventListener> updateEventObervers = new HashSet<>();
    private GUIBuchung guiBuchung;
    private GUIPersonal guiPersonal;
    private GUIGast guiGast;
    private GUIEinrichtung guiEinrichtung;
    private GUIStellplatz guiStellplatz;
    private GUIConfiguration guiConfiguration;
    private GUIConfigurationObserver guiConfigurationObserver;
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
                            // @formatter:on
                        };
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
        return this.updateEventObervers.add(eventListener);
    }

    @Override
    public boolean removeObserver(final EventListener eventListener) {
        return this.updateEventObervers.remove(eventListener);
    }

    public void bookingOpenEditTab(final String elementID) {
        this.fireUpdateEvent(new UpdateEvent(
                        this,
                        GUIBuchung.Commands.OPEN_TAB,
                        new GUIBuchung.TabPayload(
                                // TODO: real data this.entityManager.findOne(Buchung.class, elementID),
                                // TODO: close tab panes after done editing
                                "Buchung " + elementID + " bearbeiten",
                                this.guiPersonal,
                                "Die Buchung mit der Buchungsnummer " + elementID + " bearbeiten"
                        )
                )
        );

    }

    public void bookingOverviewNextWeek(final LocalDate currentWeek) {
        this.fireUpdateEvent(new UpdateEvent(
                        this,
                        BookingOverviewComponent.Commands.UPDATE_WEEK,
                        currentWeek.plusWeeks(1)
                )
        );
    }

    public void bookingOverviewPreviousWeek(final LocalDate currentWeek) {
        this.fireUpdateEvent(new UpdateEvent(
                        this,
                        BookingOverviewComponent.Commands.UPDATE_WEEK,
                        currentWeek.minusWeeks(1)
                )
        );
    }

    public void configurationSetAccentColor(final Color color) {
        this.configurationBuilder = this.configurationBuilder.accentColor(color);
        this.fireUpdateEvent(new UpdateEvent(
                this,
                GUIConfiguration.Commands.REBUILD_UI,
                this.configurationBuilder.build()
        ));
    }

    public void configurationSetDarkMode(final boolean darkModeActive) {
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

    public void exitApplication() {
        if (this.app.getConfig() == null) {
            this.app.setConfig(this.configurationBuilder.build());
        }
        this.app.exitApplication();
    }

    public void fireUpdateEvent(final UpdateEvent updateEvent) {
        for (final var eventListener : this.updateEventObervers) {
            if (eventListener instanceof IUpdateEventListener updateListener) {
                updateListener.processUpdateEvent(updateEvent);
            }
        }
    }

    public void openGUIBuchung() {
        if (this.guiBuchung != null && this.guiBuchung.isDisplayable()) {
            this.guiBuchung.grabFocus();
            return;
        }

        if (this.guiBuchung == null) {
            this.guiBuchung = new GUIBuchung(this.getConfig(), this.getBuchungen(), this.getAppointments(), LocalDate.now());
        }

        final var observer = new GUIBuchungObserver();
        this.guiBuchung.addObserver(observer);
        this.addObserver(this.guiBuchung);

        this.openInJFrame(this.guiBuchung, this.getConfig().getWindowLocation("Buchungen"), "Buchungen", event -> {
            final var window = event.getWindow();
            this.app.getConfig()
                    .setWindowLocation("Buchungen",
                            new WindowLocation(window.getX(), window.getY(), window.getWidth(), window.getHeight()));
            this.guiBuchung.removeObserver(observer);
            window.dispose();
        });
    }

    public void openGUIConfiguration(final PropertyManager propertyManager) throws Exception {
        this.configurationBuilder = Configuration.builder().propertyManager(propertyManager);
        this.guiConfiguration = new GUIConfiguration(this.configurationBuilder.build());
        this.guiConfigurationObserver = new GUIConfigurationObserver();
        this.guiConfiguration.addObserver(this.guiConfigurationObserver);
        this.addObserver(this.guiConfiguration);
        this.openInJFrame(this.guiConfiguration,
                // Main GUI and Configuration GUI have the same window location
                this.configurationBuilder.build().getWindowLocation("Main"),
                "Configuration",
                event -> this.openGUIMain(Optional.of(event.getWindow())));
    }

    public void openGUIEinrichtung() {
        if (this.guiEinrichtung != null && this.guiEinrichtung.isDisplayable()) {
            this.guiEinrichtung.grabFocus();
            return;
        }

        if (this.guiEinrichtung == null) {
            this.guiEinrichtung = new GUIEinrichtung(this.getConfig());
        }

        this.openInJFrame(this.guiEinrichtung, this.getConfig().getWindowLocation("Einrichtung"), "Einrichtungen", event -> {
            final var window = event.getWindow();
            this.app.getConfig()
                    .setWindowLocation("Einrichtung",
                            new WindowLocation(window.getX(), window.getY(), window.getWidth(), window.getHeight()));
            window.dispose();
        });
    }

    public void openGUIGast() {
        if (this.guiGast != null && this.guiGast.isDisplayable()) {
            this.guiGast.grabFocus();
            return;
        }

        if (this.guiGast == null) {
            this.guiGast = new GUIGast(this.getConfig());
        }

        this.openInJFrame(this.guiGast, this.getConfig().getWindowLocation("Gast"), "Gäste", event -> {
            final var window = event.getWindow();
            this.app.getConfig()
                    .setWindowLocation("Gast",
                            new WindowLocation(window.getX(), window.getY(), window.getWidth(), window.getHeight()));
            window.dispose();
        });
    }

    public void openGUIMain(final Optional<Window> configurationWindow) {
        final var configWindow = configurationWindow.orElse(SwingUtilities.getWindowAncestor(this.guiConfiguration));
        this.guiConfiguration.removeObserver(this.guiConfigurationObserver);
        this.removeObserver(this.guiConfiguration);
        this.guiConfigurationObserver = null;
        this.app.setConfig(this.configurationBuilder.build());
        this.app.getConfig().setWindowLocation("Main", new WindowLocation(
                configWindow.getX(),
                configWindow.getY(),
                configWindow.getWidth(),
                configWindow.getHeight()));
        configWindow.dispose();

        // TODO: Remove
//        this.openInJFrame(new CalendarComponent(this.getConfig(), "CalendarComponent", Optional.empty()),
//                this.getConfig().getWindowLocation("Main"),
//                "Calendar",
//                event -> this.exitApplication());

        final var observer = new GUIMainObserver();
        final var guiMain = new GUIMain(this.getConfig());
        guiMain.addObserver(observer);
        this.openInJFrame(guiMain, this.getConfig().getWindowLocation("Main"), "Campingplatzverwaltung", event -> this.exitApplication());
    }

    public void openGUIPersonal() {
        if (this.guiPersonal != null && this.guiPersonal.isDisplayable()) {
            this.guiPersonal.grabFocus();
            return;
        }

        if (this.guiPersonal == null) {
            this.guiPersonal = new GUIPersonal(this.getConfig());
        }

        this.openInJFrame(this.guiPersonal, this.getConfig().getWindowLocation("Personal"), "Personal", event -> {
            final var window = event.getWindow();
            this.app.getConfig()
                    .setWindowLocation("Personal",
                            new WindowLocation(window.getX(), window.getY(), window.getWidth(), window.getHeight()));
            window.dispose();
        });
    }

    public void openGUIStellplatz() {
        if (this.guiStellplatz != null && this.guiStellplatz.isDisplayable()) {
            this.guiStellplatz.grabFocus();
            return;
        }

        if (this.guiStellplatz == null) {
            this.guiStellplatz = new GUIStellplatz(this.getConfig());
        }

        this.openInJFrame(this.guiStellplatz, this.getConfig().getWindowLocation("Stellplatz"), "Stellplätze", event -> {
            final var window = event.getWindow();
            this.app.getConfig()
                    .setWindowLocation("Stellplatz",
                            new WindowLocation(window.getX(), window.getY(), window.getWidth(), window.getHeight()));
            window.dispose();
        });
    }

    private JFrame openInJFrame(final Container content,
                                final WindowLocation windowLocation,
                                final String title,
                                final Consumer<WindowEvent> onExit) {
        final JFrame frame = new JFrame(title == null ? content.getClass().getName() : title);
        frame.setBackground(Color.white);
        content.setBackground(Color.white);
        frame.setSize(windowLocation.width() > 0 ? windowLocation.width() : 100,
                windowLocation.height() > 0 ? windowLocation.height() : 100);
        frame.setLocation(Math.max(windowLocation.x(), 0), Math.max(windowLocation.y(), 0));
        frame.add(content, "Center");
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent event) {
                if (onExit != null) {
                    onExit.accept(event);
                }
                frame.dispose();
            }
        });
        frame.setVisible(true);
        return frame;

    }
}
