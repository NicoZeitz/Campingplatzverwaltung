package swe.ka.dhbw.ui.components;

import de.dhbwka.swe.utils.event.EventCommand;
import de.dhbwka.swe.utils.event.GUIEvent;
import de.dhbwka.swe.utils.event.IGUIEventListener;
import de.dhbwka.swe.utils.event.UpdateEvent;
import de.dhbwka.swe.utils.gui.BrowseSelector;
import de.dhbwka.swe.utils.util.PropertyManager;
import swe.ka.dhbw.control.ReadonlyConfiguration;
import swe.ka.dhbw.ui.GUIComponent;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CalendarComponent extends GUIComponent implements IGUIEventListener {
    public enum Commands implements EventCommand {
        // outgoing gui events
        BUTTON_PRESSED_DATE_SELECTED("CalendarComponent::BUTTON_PRESSED_DATE_SELECTED", LocalDate.class);

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

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public CalendarComponent(final ReadonlyConfiguration config, final Optional<LocalDate> date) {
        super("CalendarComponent", config);
        this.initUI(date);
    }

    @Override
    public void processGUIEvent(final GUIEvent guiEvent) {
        if (guiEvent.getCmd() == de.dhbwka.swe.utils.gui.CalendarComponent.Commands.DATE_SELECTED) {
            final var date = (LocalDate) guiEvent.getData();
            this.fireGUIEvent(new GUIEvent(this, Commands.BUTTON_PRESSED_DATE_SELECTED, date));
        }
    }

    @Override
    public void processUpdateEvent(final UpdateEvent updateEvent) {
        // nothing to react to
    }

    private void colorizeCalendar(de.dhbwka.swe.utils.gui.CalendarComponent calendarComponent) {
        calendarComponent.setBackground(this.config.getBackgroundColor());
        calendarComponent.setForeground(this.config.getTextColor());
        calendarComponent.setOpaque(true);
        calendarComponent.setFont(this.config.getFont());
        calendarComponent.setBorder(BorderFactory.createEmptyBorder());
        calendarComponent.addObserver(this);

        final var topPanel = (JPanel) calendarComponent.getComponent(0);
        topPanel.setBackground(this.config.getBackgroundColor());
        topPanel.setForeground(this.config.getTextColor());
        topPanel.setOpaque(true);
        topPanel.setBorder(BorderFactory.createEmptyBorder());
        final var browseSelectors = topPanel.getComponents();
        for (final var browseSelector : browseSelectors) {
            for (final var component : ((BrowseSelector) browseSelector).getComponents()) {
                final var button = (JComponent) component;

                button.setBackground(this.config.getBackgroundColor());
                button.setForeground(this.config.getTextColor());
                button.setOpaque(true);

                if (button instanceof JButton) {
                    button.setBackground(this.config.getAccentColor());
                } else {
                    button.setBorder(BorderFactory.createEmptyBorder());
                }
            }
        }
        final var bottomPanel = (JPanel) calendarComponent.getComponent(1);
        bottomPanel.setBackground(this.config.getBackgroundColor());
        bottomPanel.setForeground(this.config.getTextColor());
        bottomPanel.setOpaque(true);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder());
        final var children = bottomPanel.getComponents();
        for (final var child : children) {
            final var panel = (JPanel) child;
            panel.setBackground(this.config.getBackgroundColor());
            panel.setForeground(this.config.getTextColor());
            panel.setOpaque(true);
            panel.setBorder(BorderFactory.createEmptyBorder());
            for (final var element : panel.getComponents()) {
                final var jComponent = (JComponent) element;
                jComponent.setBackground(this.config.getBackgroundColor());
                jComponent.setForeground(this.config.getTextColor());
                jComponent.setOpaque(true);
            }
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private void initUI(final Optional<LocalDate> date) {
        this.setLayout(new BorderLayout());
        this.setBackground(this.config.getBackgroundColor());
        this.setForeground(this.config.getTextColor());
        this.setOpaque(true);
        this.setFont(this.config.getFont());

        // The calendar component will throw NullPointerExceptions when no property manager is provided
        // This is why we provide a useless property manager
        // Furthermore all other builder properties will get overridden by the property manager (even when no value is provided for a property)
        // Which is why we need to set the selected button color manually
        final var calendar = de.dhbwka.swe.utils.gui.CalendarComponent.builder(super.generateRandomID())
                .date(date.orElse(LocalDate.now()))
                .selectedButtonColor(this.config.getAccentColor()) // this is useless as a property manager overrides all values provided by the builder
                .propertymanager(new PropertyManager(new HashMap<>(Map.of(
                        de.dhbwka.swe.utils.gui.CalendarComponent.Properties.BTN_SELECTED_COLOR.getPropertyName(),
                        "%d,%d,%d".formatted(
                                this.config.getAccentColor().getRed(),
                                this.config.getAccentColor().getGreen(),
                                this.config.getAccentColor().getBlue()
                        )))))
                .build();
        this.colorizeCalendar(calendar);
        this.add(calendar, BorderLayout.CENTER);
    }
}
