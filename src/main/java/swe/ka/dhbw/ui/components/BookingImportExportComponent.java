package swe.ka.dhbw.ui.components;

import de.dhbwka.swe.utils.event.UpdateEvent;
import swe.ka.dhbw.control.ReadonlyConfiguration;
import swe.ka.dhbw.ui.GUIComponent;

import javax.swing.*;
import java.awt.*;

public class BookingImportExportComponent extends GUIComponent {

    public BookingImportExportComponent(final ReadonlyConfiguration config) {
        super("BookingExportComponent", config);
        this.initUI();
    }

    @Override
    public void processUpdateEvent(final UpdateEvent ue) {
        // nothing to process here
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
