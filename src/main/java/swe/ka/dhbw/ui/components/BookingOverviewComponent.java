package swe.ka.dhbw.ui.components;

import de.dhbwka.swe.utils.event.EventCommand;
import de.dhbwka.swe.utils.event.GUIEvent;
import de.dhbwka.swe.utils.event.IGUIEventListener;
import de.dhbwka.swe.utils.event.UpdateEvent;
import de.dhbwka.swe.utils.gui.ButtonElement;
import de.dhbwka.swe.utils.gui.ObservableComponent;
import de.dhbwka.swe.utils.model.IDepictable;
import swe.ka.dhbw.control.ReadonlyConfiguration;
import swe.ka.dhbw.ui.GUIComponent;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.IsoFields;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class BookingOverviewComponent extends GUIComponent implements IGUIEventListener {
    // Commands
    public enum Commands implements EventCommand {
        // outgoing gui events
        NEXT_WEEK("BookingOverviewComponent::NEXT_WEEK", LocalDate.class),
        PREVIOUS_WEEK("BookingOverviewComponent::PREVIOUS_WEEK", LocalDate.class),
        BOOKING_SELECTED("BookingOverviewComponent::BOOKING_SELECTED", IDepictable.class),
        // incoming update events
        UPDATE_APPOINTMENTS("BookingOverviewComponent::UPDATE_APPOINTMENTS", Map.class),
        UPDATE_WEEK("BookingOverviewComponent::UPDATE_WEEK", LocalDate.class);

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

    // UI IDs
    private static final String PREVIOUS_WEEK_BUTTON_ELEMENT_ID = "BookingOverviewComponent::PREVIOUS_WEEK_BUTTON_ELEMENT_ID";
    private static final String NEXT_WEEK_BUTTON_ELEMENT_ID = "BookingOverviewComponent::NEXT_WEEK_BUTTON_ELEMENT_ID";

    // Components
    private JLabel timespanLabel;
    private JPanel calendarComponent;

    // Data
    private String previousWeekLabel = "<";
    private String nextWeekLabel = ">";
    private LocalDate currentWeek;
    private Map<LocalDate, List<? extends IDepictable>> appointments = new HashMap<>();

    public BookingOverviewComponent(final ReadonlyConfiguration config) {
        super("BookingOverviewComponent", config);
        this.setCurrentWeek(LocalDate.now());
        this.initUI();
    }

    public LocalDate getCurrentWeek() {
        return this.currentWeek;
    }

    public void setCurrentWeek(final LocalDate currentWeek) {
        this.currentWeek = switch (currentWeek.getDayOfWeek()) {
            case MONDAY -> currentWeek;
            case TUESDAY -> currentWeek.minusDays(1);
            case WEDNESDAY -> currentWeek.minusDays(2);
            case THURSDAY -> currentWeek.minusDays(3);
            case FRIDAY -> currentWeek.minusDays(4);
            case SATURDAY -> currentWeek.minusDays(5);
            case SUNDAY -> currentWeek.minusDays(6);
        };
    }

    public String getPreviousWeekLabel() {
        return previousWeekLabel;
    }

    @SuppressWarnings("unused")
    public void setPreviousWeekLabel(final String previousWeekLabel) {
        this.previousWeekLabel = previousWeekLabel;
    }

    public String getNextWeekLabel() {
        return nextWeekLabel;
    }

    @SuppressWarnings("unused")
    public void setNextWeekLabel(final String nextWeekLabel) {
        this.nextWeekLabel = nextWeekLabel;
    }

    private String getTimespanLabelText() {
        final var kw = this.getCurrentWeek().get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
        final var startOfWeek = this.getCurrentWeek().format(DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.GERMANY));
        final var endOfWeek = this.getCurrentWeek().plusDays(6).format(DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.GERMANY));

        return "KW %d (%s - %s)".formatted(kw, startOfWeek, endOfWeek);
    }

    @Override
    public void processGUIEvent(final GUIEvent guiEvent) {
        if (guiEvent.getSource() instanceof ObservableComponent component) {
            final var id = component.getID();
            switch (id) {
                case PREVIOUS_WEEK_BUTTON_ELEMENT_ID -> this.fireGUIEvent(new GUIEvent(this, Commands.PREVIOUS_WEEK, this.getCurrentWeek()));
                case NEXT_WEEK_BUTTON_ELEMENT_ID -> this.fireGUIEvent(new GUIEvent(this, Commands.NEXT_WEEK, this.getCurrentWeek()));
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void processUpdateEvent(final UpdateEvent updateEvent) {
        if (updateEvent.getCmd() instanceof Commands command) {
            switch (command) {
                case UPDATE_WEEK -> {
                    this.setCurrentWeek((LocalDate) updateEvent.getData());
                    this.timespanLabel.setText(this.getTimespanLabelText());
                    this.rebuildUI();
                }
                case UPDATE_APPOINTMENTS -> {
                    this.appointments = ((Map<LocalDate, List<? extends IDepictable>>) updateEvent.getData());
                    this.rebuildUI();
                }
            }
        }
    }

    private void buildUIAppointments(final JComponent component) {
        final var days = new String[] {"MO", "DI", "MI", "DO", "FR", "SA", "SO"};
        for (var i = 0; i < days.length; ++i) {
            final var day = days[i];

            // The day (MO - SO)
            final var header = new JLabel(day);
            header.setToolTipText(this.getCurrentWeek().plusDays(i).format(DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.GERMANY)));
            header.setFont(this.config.getLargeFont());
            header.setHorizontalAlignment(SwingConstants.CENTER);
            header.setBackground(this.config.getAccentColor());
            header.setForeground(this.config.getTextColor());
            header.setBorder(BorderFactory.createEmptyBorder());
            header.setOpaque(true);

            // All bookings on this day
            final var viewPort = new JPanel();
            viewPort.setBackground(this.config.getBackgroundColor());
            viewPort.setOpaque(true);

            final var appointments = this.appointments.get(this.currentWeek.plusDays(i));
            if (appointments != null && appointments.size() > 0) {
                viewPort.setLayout(new GridBagLayout());
                // @formatter:off
                for (var j = 0; j < appointments.size(); ++j) {
                    final var appointment = appointments.get(j);
                    final var entry = new JButton();
                    entry.addActionListener(e -> this.fireGUIEvent(new GUIEvent(this, Commands.BOOKING_SELECTED, appointment)));
                    entry.setToolTipText("Buchung anzeigen");
                    entry.setFont(this.config.getFont());
                    entry.setForeground(this.config.getTextColor());
                    entry.setBackground(this.config.getSecondaryBackgroundColor());
                    entry.setText("<html>%s</html>".formatted(appointment.getVisibleText().replaceAll("\n", "<br>")));
                    entry.setHorizontalAlignment(SwingConstants.CENTER);
                    entry.setBorder(BorderFactory.createEmptyBorder());
                    viewPort.add(entry, new GridBagConstraints(0, j, 1, 1, 1d, 0d, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 10, 0), 0, 40));
                }

                final var padding = new JPanel();
                padding.setBackground(this.config.getBackgroundColor());
                viewPort.add(padding, new GridBagConstraints(0, appointments.size(), 1, 1, 1d, 1d, GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
                // @formatter:on
            } else {
                // No bookings available on that day
                final var infoMessage = new JLabel("Keine Buchungen");
                infoMessage.setFont(this.config.getFont());
                infoMessage.setForeground(this.config.getTextColor());
                infoMessage.setHorizontalAlignment(SwingConstants.CENTER);
                viewPort.add(infoMessage);
                viewPort.setLayout(new GridLayout(1, 1));
                viewPort.setBackground(this.config.getSecondaryBackgroundColor());
            }
            // @formatter:off
            final var scrollPane = new JScrollPane(viewPort, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            scrollPane.setBorder(BorderFactory.createEmptyBorder());

            component.add(header,     new GridBagConstraints(i, 0, 1, 1, 1d, 0d, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 50));
            component.add(scrollPane, new GridBagConstraints(i, 1, 1, 1, 1d, 1d, GridBagConstraints.CENTER, GridBagConstraints.BOTH,       new Insets(5, 5, 5, 5), 0, 0));
            // @formatter:on
        }
    }

    private void initUI() {
        this.setLayout(new BorderLayout());
        this.setBackground(this.config.getBackgroundColor());

        // Title
        final var title = new JPanel();
        title.setLayout(new GridLayout(2, 1, 0, 5));
        title.setBackground(this.config.getBackgroundColor());
        title.setOpaque(true);

        final var titleHeader = new JLabel("Terminübersicht");
        titleHeader.setHorizontalAlignment(SwingConstants.CENTER);
        titleHeader.setFont(this.config.getHeaderFont());
        titleHeader.setForeground(this.config.getTextColor());
        title.add(titleHeader);

        this.timespanLabel = new JLabel(this.getTimespanLabelText());
        this.timespanLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.timespanLabel.setFont(this.config.getFont());
        this.timespanLabel.setForeground(this.config.getTextColor());
        title.add(this.timespanLabel);

        this.add(title, BorderLayout.NORTH);

        // Navigation to previous week
        final var previousWeek = ButtonElement
                .builder(PREVIOUS_WEEK_BUTTON_ELEMENT_ID)
                .toolTip("Vorherige Woche")
                .font(this.config.getHeaderFont())
                .backgroundColor(this.config.getBackgroundColor())
                .textColor(this.config.getTextColor())
                .buttonText(this.getPreviousWeekLabel())
                .build();
        // BUG:SWE-UTILS: one cannot add the observer with the .observer() build method as this adds the observer 2 times
        // once in the ButtonElement::buildUI method that is called in the constructor and once in the ButtonElement.BEBuilder::build method
        previousWeek.addObserver(this);
        this.add(previousWeek, BorderLayout.WEST);

        // Navigation to next week
        final var nextWeek = ButtonElement
                .builder(NEXT_WEEK_BUTTON_ELEMENT_ID)
                .toolTip("Nächste Woche")
                .font(this.config.getHeaderFont())
                .backgroundColor(this.config.getBackgroundColor())
                .textColor(this.config.getTextColor())
                .buttonText(this.getNextWeekLabel())
                .build();
        nextWeek.addObserver(this);
        this.add(nextWeek, BorderLayout.EAST);

        // Main Calendar
        this.calendarComponent = new JPanel(new GridBagLayout());
        this.calendarComponent.setBackground(this.config.getBackgroundColor());
        this.buildUIAppointments(this.calendarComponent);
        this.add(this.calendarComponent, BorderLayout.CENTER);
    }

    private void rebuildUI() {
        // remove the entire calendar component to avoid a bug where there are sometimes weird artifacts when window is in fullscreen
        this.calendarComponent.removeAll();
        this.remove(this.calendarComponent);
        this.calendarComponent = new JPanel(new GridBagLayout());
        this.calendarComponent.setBackground(this.config.getBackgroundColor());
        this.buildUIAppointments(this.calendarComponent);
        this.add(this.calendarComponent, BorderLayout.CENTER);
    }
}
