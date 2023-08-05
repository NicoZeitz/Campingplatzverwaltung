package swe.ka.dhbw.ui;

import de.dhbwka.swe.utils.event.UpdateEvent;
import swe.ka.dhbw.control.ReadonlyConfiguration;

public class GUIEinrichtung extends GUIComponent {
    public GUIEinrichtung(final ReadonlyConfiguration config) {
        super("GUIEinrichtung", config);
        super.createEmptyMessage("Stellen Sie sich hier eine wunderbare Einrichtungsverwaltung vor!");
    }

    @Override
    public void processUpdateEvent(final UpdateEvent updateEvent) {
        // placeholder
    }
}
