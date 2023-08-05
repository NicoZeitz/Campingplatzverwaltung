package swe.ka.dhbw.ui;

import de.dhbwka.swe.utils.event.UpdateEvent;
import swe.ka.dhbw.control.ReadonlyConfiguration;

public class GUIStellplatz extends GUIComponent {
    public GUIStellplatz(final ReadonlyConfiguration config) {
        super("GUIStellplatz", config);
        super.createEmptyMessage("Stellen Sie sich hier eine wunderbare Stellplatzverwaltung vor!");
    }

    @Override
    public void processUpdateEvent(final UpdateEvent updateEvent) {
        // placeholder
    }
}
