package swe.ka.dhbw.ui;

import de.dhbwka.swe.utils.event.EventCommand;
import de.dhbwka.swe.utils.event.GUIEvent;
import de.dhbwka.swe.utils.event.IGUIEventListener;
import de.dhbwka.swe.utils.event.UpdateEvent;
import de.dhbwka.swe.utils.gui.ButtonElement;
import de.dhbwka.swe.utils.gui.ObservableComponent;
import de.dhbwka.swe.utils.model.IDepictable;
import swe.ka.dhbw.control.ReadonlyConfiguration;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.IsoFields;
import java.util.List;
import java.util.Map;

public class BookingOverviewComponent extends GUIComponent implements IGUIEventListener {
    public enum Commands implements EventCommand {
        NEXT_WEEK("BookingOverviewComponent.nextWeek", LocalDate.class),
        PREVIOUS_WEEK("BookingOverviewComponent.previousWeek", LocalDate.class),
        BUCHUNG_SELECTED("BookingOverviewComponent.buchungSelected", IDepictable.class),
        UPDATE_WEEK("BookingOverviewComponent.updateWeek", LocalDate.class);

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


    private static final String PREVIOUS_WEEK_BUTTON_ELEMENT_ID = "BookingOverviewComponent::PREVIOUS_WEEK_BUTTON_ELEMENT_ID";
    private static final String NEXT_WEEK_BUTTON_ELEMENT_ID = "BookingOverviewComponent::NEXT_WEEK_BUTTON_ELEMENT_ID";
    private static final String BUCHUNG_ELEMENT_ID = "BookingOverviewComponent::BUCHUNG_ELEMENT_ID";
    private final ReadonlyConfiguration config;
    private String previousWeekLabel = "<";
    private String nextWeekLabel = ">";
    private LocalDate currentWeek;
    private Map<LocalDate, List<? extends IDepictable>> appointments;
    private JLabel timespanLabel;
    private JPanel calendar;

    public BookingOverviewComponent(final Map<LocalDate, List<? extends IDepictable>> appointments,
                                    final LocalDate currentWeek,
                                    final ReadonlyConfiguration config) {
        super("BookingOverviewComponent");
        this.appointments = appointments;
        this.config = config;
        this.setCurrentWeek(currentWeek);
        this.initUI();
    }

    public LocalDate getCurrentWeek() {
        return this.currentWeek;
    }

    public void setCurrentWeek(final LocalDate currentWeek) {
        switch (currentWeek.getDayOfWeek()) {
            case MONDAY:
                this.currentWeek = currentWeek;
                break;
            case TUESDAY:
                this.currentWeek = currentWeek.minusDays(1);
                break;
            case WEDNESDAY:
                this.currentWeek = currentWeek.minusDays(2);
                break;
            case THURSDAY:
                this.currentWeek = currentWeek.minusDays(3);
                break;
            case FRIDAY:
                this.currentWeek = currentWeek.minusDays(4);
                break;
            case SATURDAY:
                this.currentWeek = currentWeek.minusDays(5);
                break;
            case SUNDAY:
                this.currentWeek = currentWeek.minusDays(6);
                break;
        }
    }

    public String getPreviousWeekLabel() {
        return previousWeekLabel;
    }

    public void setPreviousWeekLabel(final String previousWeekLabel) {
        this.previousWeekLabel = previousWeekLabel;
    }

    public String getNextWeekLabel() {
        return nextWeekLabel;
    }

    public void setNextWeekLabel(final String nextWeekLabel) {
        this.nextWeekLabel = nextWeekLabel;
    }

    private String getTimespanLabelText() {
        final var kw = this.getCurrentWeek().get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
        final var startOfWeek = this.getCurrentWeek().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        final var endOfWeek = this.getCurrentWeek().plusDays(6).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));

        return "KW %d (%s - %s)".formatted(kw, startOfWeek, endOfWeek);
    }

    public void setAppointments(final Map<LocalDate, List<? extends IDepictable>> appointments) {
        this.appointments = appointments;
    }

    @Override
    public void processGUIEvent(final GUIEvent ge) {
        if (ge.getSource() instanceof ObservableComponent component) {
            final var id = component.getID();
            switch (id) {
                case PREVIOUS_WEEK_BUTTON_ELEMENT_ID: {
                    this.fireGUIEvent(new GUIEvent(this, Commands.PREVIOUS_WEEK, this.getCurrentWeek()));
                    break;
                }
                case NEXT_WEEK_BUTTON_ELEMENT_ID: {
                    this.fireGUIEvent(new GUIEvent(this, Commands.NEXT_WEEK, this.getCurrentWeek()));
                    break;
                }
            }
        }
    }

    @Override
    public void processUpdateEvent(final UpdateEvent ue) {
        if (ue.getCmd() == Commands.UPDATE_WEEK) {
            this.setCurrentWeek((LocalDate) ue.getData());
            this.timespanLabel.setText(this.getTimespanLabelText());
            this.calendar.removeAll();
            // TODO:BUG: when switching and window is fullscreen sometimes there are weird artifacts
            this.initUIBuchungen(this.calendar);
        }
    }

    private void initUI() {
        this.setLayout(new BorderLayout());
        this.setBackground(Color.WHITE);

        // Titel
        final var title = new JPanel();
        title.setLayout(new GridLayout(2, 1, 0, 5));
        title.setBackground(Color.WHITE);
        title.setOpaque(true);

        final var titleHeader = new JLabel("Terminübersicht");
        titleHeader.setHorizontalAlignment(SwingConstants.CENTER);
        titleHeader.setFont(this.config.getHeaderFont());
        title.add(titleHeader);

        this.timespanLabel = new JLabel(this.getTimespanLabelText());
        this.timespanLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.timespanLabel.setFont(this.config.getFont());
        title.add(this.timespanLabel);

        this.add(title, BorderLayout.NORTH);

        // Navigation zur vorherigen Woche
        final var previousWeek = ButtonElement
                .builder(PREVIOUS_WEEK_BUTTON_ELEMENT_ID)
                .toolTip("Vorherige Woche")
                .font(this.config.getHeaderFont())
                .backgroundColor(Color.WHITE)
                .buttonText(this.getPreviousWeekLabel())
                .build();
        previousWeek.addObserver(this); // cannot add in builder as this adds the observer two times
        this.add(previousWeek, BorderLayout.WEST);

        // Navigation zur nächsten Woche
        final var nextWeek = ButtonElement
                .builder(NEXT_WEEK_BUTTON_ELEMENT_ID)
                .toolTip("Nächste Woche")
                .font(this.config.getHeaderFont())
                .backgroundColor(Color.WHITE)
                .buttonText(this.getNextWeekLabel())
                .build();
        nextWeek.addObserver(this); // cannot add in builder as this adds the observer two times
        this.add(nextWeek, BorderLayout.EAST);

        // Main Calendar
        this.calendar = new JPanel(new GridBagLayout());
        this.calendar.setBackground(Color.WHITE);
        this.initUIBuchungen(this.calendar);
        this.add(this.calendar, BorderLayout.CENTER);
    }

    private void initUIBuchungen(final JComponent component) {
        final var days = new String[] {"MO", "DI", "MI", "DO", "FR", "SA", "SO"};
        for (var i = 0; i < days.length; ++i) {
            final var day = days[i];

            // Der Tag (MO - SO)
            final var header = new JLabel(day);
            header.setToolTipText(this.getCurrentWeek().plusDays(i).format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
            header.setFont(this.config.getLargeFont());
            header.setHorizontalAlignment(SwingConstants.CENTER);
            header.setBackground(this.config.getAccentColor());
            header.setBorder(BorderFactory.createEmptyBorder());
            header.setOpaque(true);

            // Alle Buchungen an diesem Tag
            final var viewPort = new JPanel();
            viewPort.setBackground(Color.WHITE);
            viewPort.setOpaque(true);

            final var buchungen = this.appointments.get(this.currentWeek.plusDays(i));
            if (buchungen != null && buchungen.size() > 0) {
                viewPort.setLayout(new GridBagLayout());
                // @formatter:off
                for (var j = 0; j < buchungen.size(); ++j) {
                    final var buchung = buchungen.get(j);
                    final var entry = new JButton();
                    entry.addActionListener(e -> this.fireGUIEvent(new GUIEvent(this, Commands.BUCHUNG_SELECTED, buchung)));
                    entry.setToolTipText("Buchung anzeigen");
                    entry.setFont(this.config.getFont());
                    entry.setBackground(new Color(238, 238, 238));
                    entry.setText("<html>%s</html>".formatted(buchung.getVisibleText().replaceAll("\n", "<br>")));
                    entry.setHorizontalAlignment(SwingConstants.CENTER);
                    entry.setBorder(BorderFactory.createEmptyBorder());
                    viewPort.add(entry, new GridBagConstraints(0, j, 1, 1, 1d, 0d, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 10, 0), 0, 40));
                }

                final var padding = new JPanel();
                padding.setBackground(Color.WHITE);
                viewPort.add(padding, new GridBagConstraints(0, buchungen.size(), 1, 1, 1d, 1d, GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
                // @formatter:on
            } else {
                // Keine Buchungen vorhanden
                final var infoMessage = new JLabel("Keine Buchungen");
                infoMessage.setFont(this.config.getFont());
                infoMessage.setHorizontalAlignment(SwingConstants.CENTER);
                viewPort.add(infoMessage);
                viewPort.setLayout(new GridLayout(1, 1));
                viewPort.setBackground(new Color(238, 238, 238));
            }
            // @formatter:off
            final var scrollPane = new JScrollPane(viewPort, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            scrollPane.setBorder(BorderFactory.createEmptyBorder());

            component.add(header,     new GridBagConstraints(i, 0, 1, 1, 1d, 0d, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 50));
            component.add(scrollPane, new GridBagConstraints(i, 1, 1, 1, 1d, 1d, GridBagConstraints.CENTER, GridBagConstraints.BOTH,       new Insets(5, 5, 5, 5), 0, 0));
            // @formatter:on
        }
    }
}
