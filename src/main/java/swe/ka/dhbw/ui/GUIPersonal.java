package swe.ka.dhbw.ui;

import de.dhbwka.swe.utils.event.UpdateEvent;
import swe.ka.dhbw.control.ReadonlyConfiguration;

import javax.swing.*;
import java.awt.*;

public class GUIPersonal extends GUIComponent {
    public GUIPersonal(final ReadonlyConfiguration config) {
        super("GUIPersonal", config);
        this.initUI();
    }

    @Override
    public void processUpdateEvent(final UpdateEvent updateEvent) {

    }

    private void initUI() {
        this.setLayout(new BorderLayout());
        final var label = new JLabel("Stellen Sie sich hier eine wunderbare Personaldatenverwaltung vor!");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);
        label.setFont(this.config.getHeaderFont());
        label.setForeground(this.config.getTextColor());
        label.setBackground(this.config.getBackgroundColor());
        label.setOpaque(true);
        this.add(label, BorderLayout.CENTER);
    }
}
