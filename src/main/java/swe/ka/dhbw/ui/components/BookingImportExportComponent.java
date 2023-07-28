package swe.ka.dhbw.ui.components;

import de.dhbwka.swe.utils.event.UpdateEvent;
import swe.ka.dhbw.control.ReadonlyConfiguration;
import swe.ka.dhbw.ui.GUIComponent;

public class BookingImportExportComponent extends GUIComponent {

    public BookingImportExportComponent(final ReadonlyConfiguration config) {
        super("BookingImportExportComponent", config);
        super.createEmptyMessage("Stellen Sie sich hier verschiedene Optionen für das selektive Importieren und Exportieren von Buchungsdaten vor!");
    }

    @Override
    public void processUpdateEvent(final UpdateEvent updateEvent) {
        // placeholder
    }
}
