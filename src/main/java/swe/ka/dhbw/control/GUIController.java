package swe.ka.dhbw.control;

import de.dhbwka.swe.utils.event.IUpdateEventListener;
import de.dhbwka.swe.utils.event.IUpdateEventSender;
import de.dhbwka.swe.utils.event.UpdateEvent;
import de.dhbwka.swe.utils.model.Attribute;
import de.dhbwka.swe.utils.model.IDepictable;
import swe.ka.dhbw.database.EntityManager;
import swe.ka.dhbw.event.GUIBuchungObserver;
import swe.ka.dhbw.event.GUIConfigurationObserver;
import swe.ka.dhbw.event.GUIMainObserver;
import swe.ka.dhbw.event.GUIObserver;
import swe.ka.dhbw.model.Buchung;
import swe.ka.dhbw.ui.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.*;
import java.util.function.Consumer;

public class GUIController implements IUpdateEventSender {
    private static GUIController instance;
    private final Set<GUIObserver> guiObservers = new HashSet<>();
    private final Set<EventListener> updateEventObervers = new HashSet<>();
    private final Map<String, WindowLocation> windowLocations = new HashMap<>();
    private GUIConfiguration guiConfiguration;
    private GUIBuchung guiBuchung;
    private GUIPersonal guiPersonal;
    private GUIGast guiGast;
    private GUIEinrichtung guiEinrichtung;
    private GUIStellplatz guiStellplatz;
    private EntityManager entityManager;
    private Campingplatzverwaltung app;

    private GUIController() {
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
                }, (map1, map2) -> {
                    map1.putAll(map2);
                });
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

    public void exitApplication() {
        System.exit(0);
    }

    public void fireUpdateEvent(final UpdateEvent updateEvent) {
        for (final var eventListener : this.updateEventObervers) {
            if (eventListener instanceof IUpdateEventListener updateListener) {
                updateListener.processUpdateEvent(updateEvent);
            }
        }
    }

    public void gatherConfigurationAndOpenMainGUI() {
        //this.guiConfiguration
    }

    public void openGUIBuchung() {
        if (this.guiBuchung != null) {
            return;
        }

        final var observer = new GUIBuchungObserver();
        this.guiBuchung = new GUIBuchung(this.app.getConfig(), this.getAppointments(), LocalDate.now());
        this.guiBuchung.addObserver(observer);
        this.addObserver(this.guiBuchung);

        final var windowLocation = this.windowLocations.computeIfAbsent("Buchungen", k -> new WindowLocation(50, 50, 800, 600));
        this.openInJFrame(this.guiBuchung, windowLocation, "Buchungen", event -> {
            final var window = event.getWindow();
            this.windowLocations.put("Buchungen", new WindowLocation(window.getX(), window.getY(), window.getWidth(), window.getHeight()));
            this.guiObservers.remove(this.guiBuchung);
            this.guiBuchung.removeObserver(observer);
            this.guiBuchung = null;
        });
    }

    public void openGUIConfiguration() {
        final var observer = new GUIConfigurationObserver();
        this.guiObservers.add(observer);
        this.guiConfiguration = new GUIConfiguration();
        this.guiConfiguration.addObserver(observer);

        final var windowLocation = this.windowLocations.computeIfAbsent("Configuration", k -> new WindowLocation(50, 50, 800, 600));
        this.openInJFrame(new GUIConfiguration(), windowLocation, "Configuration", event -> {
            this.guiObservers.remove(observer);
            this.guiConfiguration = null;
        });

    }

    public void openGUIEinrichtung() {
        if (this.guiEinrichtung != null) {
            return;
        }

        this.guiEinrichtung = new GUIEinrichtung(this.app.getConfig());

        final var windowLocation = this.windowLocations.computeIfAbsent("Einrichtung", k -> new WindowLocation(50, 50, 800, 600));
        this.openInJFrame(this.guiEinrichtung, windowLocation, "Einrichtungen", event -> {
            final var window = event.getWindow();
            this.windowLocations.put("Einrichtung", new WindowLocation(window.getX(), window.getY(), window.getWidth(), window.getHeight()));
            this.guiEinrichtung = null;
        });
    }

    public void openGUIGast() {
        if (this.guiGast != null) {
            return;
        }

        this.guiGast = new GUIGast(this.app.getConfig());

        final var windowLocation = this.windowLocations.computeIfAbsent("Gast", k -> new WindowLocation(50, 50, 800, 600));
        this.openInJFrame(this.guiGast, windowLocation, "Gäste", event -> {
            final var window = event.getWindow();
            this.windowLocations.put("Gast", new WindowLocation(window.getX(), window.getY(), window.getWidth(), window.getHeight()));
            this.guiGast = null;
        });
    }

    public void openGUIMain() {
        final var observer = new GUIMainObserver();
        final var guiMain = new GUIMain(this.app.getConfig());
        guiMain.addObserver(observer);

        final var windowLocation = this.windowLocations.computeIfAbsent("Main", k -> new WindowLocation(100, 100, 800, 600));
        this.openInJFrame(guiMain, windowLocation, "Campingplatzverwaltung", event -> this.exitApplication());
    }

    public void openGUIPersonal() {
        if (this.guiPersonal != null) {
            return;
        }

        this.guiPersonal = new GUIPersonal(this.app.getConfig());

        final var windowLocation = this.windowLocations.computeIfAbsent("Personal", k -> new WindowLocation(50, 50, 800, 600));
        this.openInJFrame(this.guiPersonal, windowLocation, "Personal", event -> {
            final var window = event.getWindow();
            this.windowLocations.put("Personal", new WindowLocation(window.getX(), window.getY(), window.getWidth(), window.getHeight()));
            this.guiPersonal = null;
        });
    }

    public void openGUIStellplatz() {
        if (this.guiStellplatz != null) {
            return;
        }

        this.guiStellplatz = new GUIStellplatz(this.app.getConfig());

        final var windowLocation = this.windowLocations.computeIfAbsent("Stellplatz", k -> new WindowLocation(50, 50, 800, 600));
        this.openInJFrame(this.guiStellplatz, windowLocation, "Stellplätze", event -> {
            final var window = event.getWindow();
            this.windowLocations.put("Stellplatz", new WindowLocation(window.getX(), window.getY(), window.getWidth(), window.getHeight()));
            this.guiStellplatz = null;
        });
    }

    private JFrame openInJFrame(final Container content,
                                final WindowLocation windowLocation,
                                final String title,
                                final Consumer<WindowEvent> onExit) {
        final JFrame frame = new JFrame(title == null ? content.getClass().getName() : title);
        frame.setBackground(Color.white);
        content.setBackground(Color.white);
        frame.setSize(windowLocation.width > 0 ? windowLocation.width : 100,
                windowLocation.height > 0 ? windowLocation.height : 100);
        frame.setLocation(windowLocation.x > 0 ? windowLocation.x : 0, windowLocation.y > 0 ? windowLocation.y : 0);
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

    private record WindowLocation(int x, int y, int width, int height) {
    }
}
