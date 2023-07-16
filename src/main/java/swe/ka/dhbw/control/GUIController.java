package swe.ka.dhbw.control;

import de.dhbwka.swe.utils.event.IUpdateEventListener;
import de.dhbwka.swe.utils.event.IUpdateEventSender;
import de.dhbwka.swe.utils.event.UpdateEvent;
import de.dhbwka.swe.utils.model.Attribute;
import de.dhbwka.swe.utils.model.IDepictable;
import de.dhbwka.swe.utils.util.IOUtilities;
import swe.ka.dhbw.database.EntityManager;
import swe.ka.dhbw.event.GUIBuchungObserver;
import swe.ka.dhbw.event.GUIConfigurationObserver;
import swe.ka.dhbw.event.GUIObserver;
import swe.ka.dhbw.model.Buchung;
import swe.ka.dhbw.ui.BookingOverviewComponent;
import swe.ka.dhbw.ui.GUIBuchung;
import swe.ka.dhbw.ui.GUIConfiguration;

import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.*;

public class GUIController implements IUpdateEventSender {
    private static GUIController instance;
    private final Set<GUIObserver> guiObservers = new HashSet<>();
    private final Set<EventListener> updateEventObervers = new HashSet<>();
    private GUIConfiguration guiConfiguration;
    private GUIBuchung guiBuchung;
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

        this.guiBuchung = new GUIBuchung(this.app.getConfig(), this.getAppointments(), LocalDate.now());
        this.guiBuchung.addObserver(new GUIBuchungObserver(this));
        this.addObserver(this.guiBuchung);

        IOUtilities.openInJFrame(
                this.guiBuchung,
                800,
                600,
                50,
                50,
                "Buchungen",
                Color.black,
                true
        );
    }

    public void showConfiguration() {
        final var observer = new GUIConfigurationObserver();
        this.guiObservers.add(observer);
        this.guiConfiguration = new GUIConfiguration();
        this.guiConfiguration.addObserver(observer);
        IOUtilities.openInJFrame(new GUIConfiguration(), 400, 400, 0, 0, "Configuration", java.awt.Color.black, true);

    }
}
