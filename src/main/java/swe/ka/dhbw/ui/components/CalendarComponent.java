package swe.ka.dhbw.ui.components;

import de.dhbwka.swe.utils.event.GUIEvent;
import de.dhbwka.swe.utils.event.IGUIEventListener;
import de.dhbwka.swe.utils.event.UpdateEvent;
import de.dhbwka.swe.utils.gui.BrowseSelector;
import swe.ka.dhbw.control.ReadonlyConfiguration;
import swe.ka.dhbw.event.LogObserver;
import swe.ka.dhbw.ui.GUIComponent;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.Optional;

public class CalendarComponent extends GUIComponent implements IGUIEventListener {
    private final ReadonlyConfiguration config;

    public CalendarComponent(final ReadonlyConfiguration config, final String id, final Optional<LocalDate> date) {
        super("CalendarComponent");
        this.config = config;
        this.initUI(id, date);
    }

    @Override
    public void processGUIEvent(final GUIEvent guiEvent) {
        LogObserver.logGUIEvent(guiEvent);
    }

    @Override
    public void processUpdateEvent(final UpdateEvent updateEvent) {
        // nothing to react to
    }

    private void initUI(final String id, final Optional<LocalDate> date) {
        this.setLayout(new BorderLayout());

        final var calendar = de.dhbwka.swe.utils.gui.CalendarComponent.builder(id)
                .date(date.orElse(LocalDate.now()))
                .selectedButtonColor(this.config.getAccentColor())
                .build();
        calendar.setBackground(this.config.getBackgroundColor());
        calendar.setForeground(this.config.getTextColor());
        calendar.setOpaque(true);
        calendar.setFont(this.config.getFont());
        calendar.setBorder(BorderFactory.createEmptyBorder());
        calendar.addObserver(this);

        final var topPanel = (JPanel) calendar.getComponent(0);
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
        final var bottomPanel = (JPanel) calendar.getComponent(1);
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

        /*
        JPanel
          JPanel
            List<JButton>
          JPanel
            List<JLabel>
        */

        this.add(calendar, BorderLayout.CENTER);
    }
}
