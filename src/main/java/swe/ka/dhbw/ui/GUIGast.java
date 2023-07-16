package swe.ka.dhbw.ui;

import de.dhbwka.swe.utils.event.UpdateEvent;
import swe.ka.dhbw.control.ReadonlyConfiguration;

import javax.swing.*;
import java.awt.*;

public class GUIGast extends GUIComponent {
    private final ReadonlyConfiguration config;

    public GUIGast(final ReadonlyConfiguration config) {
        this.config = config;
        this.initUI();
    }

    @Override
    public void processUpdateEvent(final UpdateEvent updateEvent) {

    }

    private void initUI() {
        this.setLayout(new BorderLayout());
        this.setBackground(Color.WHITE);
        this.setOpaque(true);
        final var label = new JLabel("Stellen Sie sich hier eine wunderbare Gästedatenverwaltung vor!");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);
        label.setFont(this.config.getHeaderFont());
        this.add(label, BorderLayout.CENTER);
    }
}
