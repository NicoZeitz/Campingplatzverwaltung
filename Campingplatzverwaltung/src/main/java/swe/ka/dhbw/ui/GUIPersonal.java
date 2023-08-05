package swe.ka.dhbw.ui;

import de.dhbwka.swe.utils.event.UpdateEvent;
import swe.ka.dhbw.control.ReadonlyConfiguration;

public class GUIPersonal extends GUIComponent {
    public GUIPersonal(final ReadonlyConfiguration config) {
        super("GUIPersonal", config);
        super.createEmptyMessage("Stellen Sie sich hier eine wunderbare Personaldatenverwaltung vor!");
    }

    @Override
    public void processUpdateEvent(final UpdateEvent updateEvent) {
        // placeholder
    }
}
