package swe.ka.dhbw.ui.components;

import de.dhbwka.swe.utils.gui.ObservableComponent;
import swe.ka.dhbw.control.ReadonlyConfiguration;

import javax.swing.*;
import java.awt.*;

public class BookingImportExportComponent extends ObservableComponent {
    private final ReadonlyConfiguration config;

    public BookingImportExportComponent(final ReadonlyConfiguration config) {
        super("BookingExportComponent");
        this.config = config;
        this.initUI();
    }

    private void initUI() {
        this.setLayout(new BorderLayout());
        final var label = new JLabel(
                "Stellen Sie sich hier verschiedene Optionen für das selektive Importieren und Exportieren von Buchungsdaten vor!");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);
        label.setFont(this.config.getHeaderFont());
        label.setForeground(this.config.getTextColor());
        label.setBackground(this.config.getBackgroundColor());
        label.setOpaque(true);
        this.add(label, BorderLayout.CENTER);
    }
}
